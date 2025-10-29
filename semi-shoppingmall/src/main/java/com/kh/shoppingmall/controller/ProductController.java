package com.kh.shoppingmall.controller;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.ProductOptionDao;
import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.CategoryService;
import com.kh.shoppingmall.service.ProductService;
import com.kh.shoppingmall.service.ReviewService;
import com.kh.shoppingmall.service.WishlistService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private WishlistService wishlistService;
    @Autowired private ReviewService reviewService;
    @Autowired private ProductOptionDao productOptionDao;

    // ================= 상품 목록 =================
    @GetMapping("/list")
    public String list(@RequestParam(value = "column", required = false) String column,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "categoryNo", required = false) Integer categoryNo,
                       @RequestParam(required = false) String order,
                       HttpSession session,
                       Model model) throws SQLException {

        List<ProductDto> list = productService.getFilteredProducts(column, keyword, categoryNo, order);
        list.forEach(p -> p.setProductAvgRating(reviewService.getAverageRating(p.getProductNo())));

        model.addAttribute("productList", list);
        model.addAttribute("column", column);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryNo", categoryNo);
        model.addAttribute("order", order);

        String loginId = (String) session.getAttribute("loginId");

        Map<Integer, Boolean> wishlistStatus = new HashMap<>();
        Map<Integer, Integer> wishlistCounts = productService.getWishlistCounts();

        for (ProductDto p : list) {
            boolean inWishlist = false;
            if (loginId != null) {
                inWishlist = wishlistService.checkItem(loginId, p.getProductNo());
            }
            wishlistStatus.put(p.getProductNo(), inWishlist);
        }

        model.addAttribute("wishlistStatus", wishlistStatus);
        model.addAttribute("wishlistCounts", wishlistCounts);
        model.addAttribute("categoryTree", categoryService.getCategoryTree());

        return "/WEB-INF/views/product/list.jsp";
    }

    // ================= 상품 등록 페이지 =================
    @GetMapping("/add")
    public String addPage(@RequestParam(required = false) Integer parentCategoryNo, Model model) {
        model.addAttribute("parentCategoryList", categoryService.getParentCategories());
        List<CategoryDto> childCategoryList = (parentCategoryNo != null)
                ? categoryService.getChildrenByParent(parentCategoryNo)
                : new ArrayList<>();
        model.addAttribute("childCategoryList", childCategoryList);
        model.addAttribute("selectedParentNo", parentCategoryNo);
        return "/WEB-INF/views/product/add.jsp";
    }

    // ================= 상품 등록 처리 (옵션 포함) =================
    @PostMapping("/add")
    public String add(@ModelAttribute ProductDto productDto,
                      @RequestParam(required = false) List<Integer> categoryNoList,
                      @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
                      @RequestParam("optionNameList") List<String> optionNameList,
                      @RequestParam("optionValueList") List<String> optionValueList) throws Exception {

        if (categoryNoList == null) categoryNoList = new ArrayList<>();

        int productNo = productService.register(productDto, new ArrayList<>(), categoryNoList, thumbnailFile);

        if (optionNameList != null && optionValueList != null) {
            for (int i = 0; i < optionNameList.size(); i++) {
                String name = optionNameList.get(i);
                String value = optionValueList.get(i);
                if (name != null && !name.isBlank() && value != null && !value.isBlank()) {
                    ProductOptionDto option = ProductOptionDto.builder()
                            .productNo(productNo)
                            .optionName(name)
                            .optionValue(value)
                            .build();
                    productOptionDao.insert(option);
                }
            }
        }
        return "redirect:addFinish";
    }

    // 상품 등록 완료
    @GetMapping("/addFinish")
    public String addFinish() {
        return "/WEB-INF/views/product/addFinish.jsp";
    }

    // ================= 상품 상세 =================
    @GetMapping("/detail")
    public String detail(@RequestParam(required = false) Integer productNo,
                         Model model,
                         HttpSession session) {

        if (productNo == null) return "redirect:/product/list";

        ProductDto product = productService.getProduct(productNo);
        if (product == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");

        List<ProductOptionDto> optionList = productService.getOptionsByProduct(productNo);

        // ✅ 옵션 분리: 색상 / 사이즈
        List<String> colorList = new ArrayList<>();
        List<String> sizeList = new ArrayList<>();

        for (ProductOptionDto opt : optionList) {
            if ("색상".equals(opt.getOptionName()) && !colorList.contains(opt.getOptionValue())) {
                colorList.add(opt.getOptionValue());
            } else if ("사이즈".equals(opt.getOptionName()) && !sizeList.contains(opt.getOptionValue())) {
                sizeList.add(opt.getOptionValue());
            }
        }

        String loginId = (String) session.getAttribute("loginId");
        boolean wishlisted = loginId != null && wishlistService.checkItem(loginId, productNo);

        double avg = reviewService.getAverageRating(productNo);
        product.setProductAvgRating(avg);

        model.addAttribute("product", product);
        model.addAttribute("colorList", colorList);
        model.addAttribute("sizeList", sizeList);
        model.addAttribute("reviewList", reviewService.getReviewsDetailByProduct(productNo));
        model.addAttribute("wishlisted", wishlisted);
        model.addAttribute("wishlistCount", wishlistService.count(productNo));
        model.addAttribute("avgRating", avg);

        // 예상 도착일 계산
        LocalDate today = LocalDate.now();
        LocalDate estimatedDate = today.plusDays(4);
        DayOfWeek dayOfWeek = estimatedDate.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY) estimatedDate = estimatedDate.plusDays(2);
        else if (dayOfWeek == DayOfWeek.SUNDAY) estimatedDate = estimatedDate.plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일(E)", Locale.KOREAN);
        model.addAttribute("estimatedDeliveryDate", estimatedDate.format(formatter));

        return "/WEB-INF/views/product/detail.jsp";
    }

    // ================= 상품 수정 페이지 =================
    @GetMapping("/edit")
    public String editPage(@RequestParam int productNo, Model model) {
        ProductDto product = productService.getProduct(productNo);
        if (product == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");

        model.addAttribute("product", product);
        model.addAttribute("parentCategoryList", categoryService.getParentCategories());

        List<CategoryDto> childCategoryList = (product.getParentCategoryNo() != null)
                ? categoryService.getChildrenByParent(product.getParentCategoryNo())
                : new ArrayList<>();
        model.addAttribute("childCategoryList", childCategoryList);

        return "/WEB-INF/views/product/edit.jsp";
    }

    // ================= 상품 수정 처리 =================
    @PostMapping("/edit")
    public String edit(@ModelAttribute ProductDto productDto,
                       @RequestParam(required = false) List<Integer> categoryNoList,
                       @RequestParam(required = false) MultipartFile thumbnailFile,
                       @RequestParam(required = false) List<Integer> deleteAttachmentNoList) throws Exception {

        if (categoryNoList == null) categoryNoList = new ArrayList<>();
        productService.update(productDto, new ArrayList<>(), categoryNoList, thumbnailFile, deleteAttachmentNoList);
        return "redirect:detail?productNo=" + productDto.getProductNo();
    }

    // ================= 상품 삭제 =================
    @PostMapping("/delete")
    public String delete(@RequestParam int productNo) throws Exception {
        ProductDto product = productService.getProduct(productNo);
        if (product == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");
        productService.delete(productNo);
        return "redirect:list";
    }
}

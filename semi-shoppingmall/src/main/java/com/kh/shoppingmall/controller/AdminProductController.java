package com.kh.shoppingmall.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
@RequestMapping("/admin/product")
public class AdminProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private WishlistService wishlistService;
    @Autowired private ReviewService reviewService;

    // 상품 목록
    @GetMapping("/list")
    public String list(@RequestParam(value = "column", required = false) String column,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "categoryNo", required = false) Integer categoryNo,
                       @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                       HttpSession session, Model model) throws SQLException {

        List<ProductDto> list = productService.getFilteredProducts(column, keyword, categoryNo, order);

        model.addAttribute("productList", list);
        model.addAttribute("column", column);
        model.addAttribute("keyword", keyword);
        model.addAttribute("order", order);
        model.addAttribute("categoryNo", categoryNo);
        model.addAttribute("wishlistCounts", productService.getWishlistCounts());
        model.addAttribute("categoryTree", categoryService.getCategoryTree());
        return "/WEB-INF/views/admin/product/list.jsp";
    }

    // 상품 등록 페이지
    @GetMapping("/add")
    public String addPage(@RequestParam(required = false) Integer parentCategoryNo, Model model) {
        model.addAttribute("parentCategoryList", categoryService.getParentCategories());
        List<CategoryDto> childList = (parentCategoryNo != null)
                ? categoryService.getChildrenByParent(parentCategoryNo)
                : new ArrayList<>();
        model.addAttribute("childCategoryList", childList);
        model.addAttribute("selectedParentNo", parentCategoryNo);
        return "/WEB-INF/views/admin/product/add.jsp";
    }

    @PostMapping("/add")
    public String add(
        @ModelAttribute ProductDto productDto,
        @RequestParam(value = "parentCategoryNo", required = false) Integer parentCategoryNo,
        @RequestParam(value = "childCategoryNo", required = false) Integer childCategoryNo,
        @RequestParam("thumbnailFile") MultipartFile thumbnailFile
    ) throws Exception {

        // 카테고리 구성
        List<Integer> categoryNoList = new ArrayList<>();
        if (parentCategoryNo != null) categoryNoList.add(parentCategoryNo);
        if (childCategoryNo != null) categoryNoList.add(childCategoryNo);

        // 옵션은 나중에 따로 등록할 거라서 null 전달
        int productNo = productService.register(productDto, null, categoryNoList, thumbnailFile);

        // 등록 후 → 옵션 관리 페이지로 이동
        return "redirect:/admin/product/option/manage?productNo=" + productNo;
    }

    // 상품 상세
    @GetMapping("/detail")
    public String adminDetail(@RequestParam(required = false) Integer productNo,
                              Model model, HttpSession session) {

        if (productNo == null)
            return "redirect:/admin/product/list";

        ProductDto product = productService.getProduct(productNo);
        if (product == null)
            throw new TargetNotfoundException("존재하지 않는 상품 번호");

        List<ProductOptionDto> optionList = productService.getOptionsByProduct(productNo);
        String loginId = (String) session.getAttribute("loginId");
        boolean wishlisted = loginId != null && wishlistService.checkItem(loginId, productNo);

        double avg = reviewService.getAverageRating(productNo);
        product.setProductAvgRating(avg);

        model.addAttribute("product", product);
        model.addAttribute("optionList", optionList);
        model.addAttribute("reviewList", reviewService.getReviewsDetailByProduct(productNo));
        model.addAttribute("wishlisted", wishlisted);
        model.addAttribute("wishlistCount", wishlistService.count(productNo));
        model.addAttribute("avgRating", avg);

        return "/WEB-INF/views/admin/product/detail.jsp";
    }

    // 상품 수정 페이지
    @GetMapping("/edit")
    public String editPage(@RequestParam int productNo, Model model) {
        ProductDto product = productService.getProduct(productNo);
        if (product == null)
            throw new TargetNotfoundException("존재하지 않는 상품 번호");

        model.addAttribute("product", product);
        model.addAttribute("parentCategoryList", categoryService.getParentCategories());
        List<CategoryDto> child = (product.getParentCategoryNo() != null)
                ? categoryService.getChildrenByParent(product.getParentCategoryNo())
                : new ArrayList<>();
        model.addAttribute("childCategoryList", child);
        return "/WEB-INF/views/admin/product/edit.jsp";
    }

    // 상품 수정 처리
    @PostMapping("/edit")
    public String edit(@ModelAttribute ProductDto productDto,
                       @RequestParam(required = false) List<Integer> categoryNoList,
                       @RequestParam(required = false) MultipartFile thumbnailFile,
                       @RequestParam(required = false) List<Integer> deleteAttachmentNoList) throws Exception {

        if (categoryNoList == null)
            categoryNoList = new ArrayList<>();

        productService.update(productDto, new ArrayList<>(), categoryNoList, thumbnailFile, deleteAttachmentNoList);
        return "redirect:detail?productNo=" + productDto.getProductNo();
    }

    // 상품 삭제
    @PostMapping("/delete")
    public String delete(@RequestParam int productNo) throws Exception {
        ProductDto product = productService.getProduct(productNo);
        if (product == null)
            throw new TargetNotfoundException("존재하지 않는 상품 번호");

        productService.delete(productNo);
        return "redirect:list";
    }

    // 하위 카테고리 불러오기 (상품등록 AJAX용)
    @GetMapping("/category/children")
    public String getChildren(@RequestParam int parentCategoryNo, Model model) {
        List<CategoryDto> childCategoryList = categoryService.getChildrenByParent(parentCategoryNo);
        model.addAttribute("childCategoryList", childCategoryList);
        return "/WEB-INF/views/admin/category/children.jsp";
    }
}

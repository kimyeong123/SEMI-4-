package com.kh.shoppingmall.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    // ---------------- 상품 목록 ----------------
    @GetMapping("/list")
    public String list(@RequestParam(value = "column", required = false) String column,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "categoryNo", required = false) Integer categoryNo,
                       HttpSession session, Model model) throws SQLException {

        List<ProductDto> list = productService.getFilteredProducts(column, keyword, categoryNo);
        for (ProductDto p : list)
            p.setProductAvgRating(reviewService.getAverageRating(p.getProductNo()));

        model.addAttribute("productList", list);
        model.addAttribute("column", column);
        model.addAttribute("keyword", keyword);
        model.addAttribute("wishlistCounts", productService.getWishlistCounts());
        model.addAttribute("categoryTree", categoryService.getCategoryTree());
        return "/WEB-INF/views/admin/product/list.jsp";
    }

    // ---------------- 상품 등록 (GET) ----------------
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

    // ---------------- 상품 등록 (POST, 다중 옵션 포함) ----------------
    @PostMapping("/add")
    public String add(@ModelAttribute ProductDto productDto,
                      @RequestParam(value = "parentCategoryNo", required = false) Integer parentCategoryNo,
                      @RequestParam(value = "childCategoryNo", required = false) Integer childCategoryNo,
                      @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
                      @RequestParam("detailImageList") List<MultipartFile> detailImageList,
                      @RequestParam(value = "optionNameList", required = false) List<String> optionNameList,
                      @RequestParam(value = "optionValueList", required = false) List<String> optionValueList
    ) throws Exception {

        //  1. 카테고리 구성
        List<Integer> categoryNoList = new ArrayList<>();
        if (parentCategoryNo != null) categoryNoList.add(parentCategoryNo);
        if (childCategoryNo != null) categoryNoList.add(childCategoryNo);

        //  2. 옵션 구성 (이 부분 수정됨)
        List<ProductOptionDto> optionList = new ArrayList<>();
        if (optionNameList != null && !optionNameList.isEmpty() &&
            optionValueList != null && !optionValueList.isEmpty()) {

            // 옵션 이름은 한 세트로 처리 (예: 색상)
            String optionName = optionNameList.get(0);

            // 여러 값 (예: 빨강, 파랑, 노랑)
            for (String value : optionValueList) {
                if (value != null && !value.trim().isEmpty()) {
                    ProductOptionDto option = new ProductOptionDto();
                    option.setOptionName(optionName);
                    option.setOptionValue(value);
                    option.setOptionStock(10); // 기본 재고
                    optionList.add(option);
                }
            }
        }

        //  3. 상품 등록
        int productNo = productService.register(productDto, optionList, categoryNoList, thumbnailFile, detailImageList);

        //  4. 상세 페이지로 이동
        return "redirect:/admin/product/detail?productNo=" + productNo;
    }

    // ---------------- 상품 상세 ----------------
        @GetMapping("/detail")
        public String adminDetail(
            @RequestParam(required = false) Integer productNo, 
            Model model, 
            HttpSession session) {
            
            // 1. productNo 누락 시 처리: 관리자 목록으로 리다이렉트
            if (productNo == null) {
                return "redirect:/admin/product/list"; 
            }

            // 2. 상품 조회 및 예외 처리
            ProductDto product = productService.getProduct(productNo);
            if (product == null) {
                throw new TargetNotfoundException("존재하지 않는 상품 번호");
            }
            
            // 상품 옵션 목록 조회
            List<ProductOptionDto> optionList = productService.getOptionsByProduct(productNo); // productService 사용 권장

            // 위시리스트 정보 조회 (관리자 페이지에서는 이 정보가 필요 없을 수도 있지만, 기존 로직 유지)
            String loginId = (String) session.getAttribute("loginId");
            boolean wishlisted = loginId != null && wishlistService.checkItem(loginId, productNo);

            // 리뷰 평점 평균 계산
            double avg = reviewService.getAverageRating(productNo);
            product.setProductAvgRating(avg);

            model.addAttribute("product", product);
            model.addAttribute("optionList", optionList);
            model.addAttribute("reviewList", reviewService.getReviewsDetailByProduct(productNo));
            model.addAttribute("wishlisted", wishlisted);
            model.addAttribute("wishlistCount", wishlistService.count(productNo));
            model.addAttribute("avgRating", avg); // 평균 평점 다시 추가

            return "/WEB-INF/views/admin/product/detail.jsp";
        }

    // ---------------- 상품 수정 ----------------
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

    @PostMapping("/edit")
    public String edit(@ModelAttribute ProductDto productDto,
                       @RequestParam(required = false) List<Integer> categoryNoList,
                       @RequestParam(required = false) MultipartFile thumbnailFile,
                       @RequestParam(required = false) List<MultipartFile> detailImageList,
                       @RequestParam(required = false) List<Integer> deleteAttachmentNoList) throws Exception {

        if (categoryNoList == null) categoryNoList = new ArrayList<>();
        if (detailImageList == null) detailImageList = new ArrayList<>();

        productService.update(productDto, new ArrayList<>(), categoryNoList, thumbnailFile, detailImageList, deleteAttachmentNoList);
        return "redirect:detail?productNo=" + productDto.getProductNo();
    }

    // ---------------- 상품 삭제 ----------------
    @PostMapping("/delete")
    public String delete(@RequestParam int productNo) throws Exception {
        ProductDto product = productService.getProduct(productNo);
        if (product == null)
            throw new TargetNotfoundException("존재하지 않는 상품 번호");
        productService.delete(productNo);
        return "redirect:list";
    }
}

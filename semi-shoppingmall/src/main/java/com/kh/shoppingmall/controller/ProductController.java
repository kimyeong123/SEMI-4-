package com.kh.shoppingmall.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.CategoryService;
import com.kh.shoppingmall.service.ProductService;
import com.kh.shoppingmall.service.ReviewService;
import com.kh.shoppingmall.service.WishlistService;
import com.kh.shoppingmall.vo.ReviewDetailVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WishlistService wishlistService;
    @Autowired
    private ReviewService reviewService;

    // 상품 목록
    @GetMapping("/list")
    public String list(@RequestParam(value = "column", required = false) String column,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       HttpSession session,
                       Model model) {

        List<ProductDto> list = productService.getProductList(column, keyword);

        for (ProductDto p : list) {
	        double avgRating = reviewService.getAverageRating(p.getProductNo());
	        p.setProductAvgRating(avgRating);
	    }
        
        model.addAttribute("productList", list);
        model.addAttribute("column", column);
        model.addAttribute("keyword", keyword);

        String loginId = (String) session.getAttribute("loginId");

        Map<Integer, Boolean> wishlistStatus = new HashMap<>();
        Map<Integer, Integer> wishlistCounts = new HashMap<>();

        for (ProductDto p : list) {
            if (loginId != null) {
                wishlistStatus.put(p.getProductNo(), wishlistService.checkItem(loginId, p.getProductNo()));
            } else {
                wishlistStatus.put(p.getProductNo(), false);
            }
            wishlistCounts.put(p.getProductNo(), wishlistService.count(p.getProductNo()));
        }

        model.addAttribute("wishlistStatus", wishlistStatus);
        model.addAttribute("wishlistCounts", wishlistCounts);

        return "/WEB-INF/views/product/list.jsp";
    }

    // 상품 등록 페이지
    @GetMapping("/add")
    public String addPage(@RequestParam(required = false) Integer parentCategoryNo, Model model) {
        List<CategoryDto> parentCategoryList = categoryService.getParentCategories();
        model.addAttribute("parentCategoryList", parentCategoryList);

        List<CategoryDto> childCategoryList = new ArrayList<>();
        if (parentCategoryNo != null) {
            childCategoryList = categoryService.getChildrenByParent(parentCategoryNo);
        }
        model.addAttribute("childCategoryList", childCategoryList);
        model.addAttribute("selectedParentNo", parentCategoryNo);

        return "/WEB-INF/views/product/add.jsp";
    }

    // 상품 등록 처리
    @PostMapping("/add")
    public String add(@ModelAttribute ProductDto productDto,
                      @RequestParam(required = false) List<Integer> categoryNoList,
                      @RequestParam MultipartFile thumbnailFile,
                      @RequestParam(required = false) List<MultipartFile> detailImageList) throws Exception {

        if (categoryNoList == null)
            categoryNoList = new ArrayList<>();
        if (detailImageList == null)
            detailImageList = new ArrayList<>();

        productService.register(productDto, new ArrayList<>(), categoryNoList, thumbnailFile, detailImageList);
        return "redirect:addFinish";
    }

    // 상품 등록 완료
    @GetMapping("/addFinish")
    public String addFinish() {
        return "/WEB-INF/views/product/addFinish.jsp";
    }

    // 상품 상세
    @GetMapping("/detail")
    public String detail(@RequestParam int productNo, Model model, HttpSession session) {
        ProductDto product = productService.getProduct(productNo);
        if (product == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");

        model.addAttribute("product", product);

        String loginId = (String) session.getAttribute("loginId");
        boolean wishlisted = false;
        int wishlistCount = wishlistService.count(productNo); // 총 찜 개수
        if (loginId != null) {
            wishlisted = wishlistService.checkItem(loginId, productNo);
        }
     // 리뷰 평점 평균 계산
	    double avg = reviewService.getAverageRating(productNo);
	    product.setProductAvgRating(avg);
	    
        List<ReviewDetailVO> reviewList = reviewService.getReviewsDetailByProduct(productNo);
        model.addAttribute("reviewList", reviewList);

        model.addAttribute("wishlisted", wishlisted);
        model.addAttribute("wishlistCount", wishlistCount);

        return "/WEB-INF/views/product/detail.jsp";
    }

    // 상품 수정 페이지
    @GetMapping("/edit")
    public String editPage(@RequestParam int productNo, Model model) {
        ProductDto product = productService.getProduct(productNo);
        if (product == null)
            throw new TargetNotfoundException("존재하지 않는 상품 번호");

        List<CategoryDto> parentCategoryList = categoryService.getParentCategories();
        List<CategoryDto> childCategoryList = new ArrayList<>();
        if (product.getParentCategoryNo() != null) {
            childCategoryList = categoryService.getChildrenByParent(product.getParentCategoryNo());
        }

        model.addAttribute("product", product);
        model.addAttribute("parentCategoryList", parentCategoryList);
        model.addAttribute("childCategoryList", childCategoryList);

        return "/WEB-INF/views/product/edit.jsp";
    }

    // 상품 수정 처리
    @PostMapping("/edit")
    public String edit(@ModelAttribute ProductDto productDto,
                       @RequestParam(required = false) List<Integer> categoryNoList,
                       @RequestParam(required = false) MultipartFile thumbnailFile,
                       @RequestParam(required = false) List<MultipartFile> detailImageList,
                       @RequestParam(required = false) List<Integer> deleteAttachmentNoList) throws Exception {

        if (categoryNoList == null)
            categoryNoList = new ArrayList<>();
        if (detailImageList == null)
            detailImageList = new ArrayList<>();

        productService.update(productDto, new ArrayList<>(), categoryNoList, thumbnailFile, detailImageList,
                deleteAttachmentNoList);
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
}

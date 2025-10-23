package com.kh.shoppingmall.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.CategoryService;
import com.kh.shoppingmall.service.ProductService;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // ---------------- 상품 목록 ----------------
    @GetMapping("/list")
    public String list(Model model) {
        List<ProductDto> productList = productService.getProductList(null, null);
        model.addAttribute("productList", productList);
        return "/WEB-INF/views/admin/product/list.jsp";
    }

    // ---------------- 상품 등록 페이지 ----------------
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

        return "/WEB-INF/views/admin/product/add.jsp";
    }

    // ---------------- 상품 등록 처리 ----------------
    @PostMapping("/add")
    public String add(@ModelAttribute ProductDto productDto,
                      @RequestParam(required = false) List<Integer> categoryNoList,
                      @RequestParam MultipartFile thumbnailFile,
                      @RequestParam(required = false) List<MultipartFile> detailImageList) throws Exception {

        if (categoryNoList == null) categoryNoList = new ArrayList<>();
        if (detailImageList == null) detailImageList = new ArrayList<>();

        productService.register(productDto, new ArrayList<>(), categoryNoList, thumbnailFile, detailImageList);
        return "redirect:addFinish";
    }

    // ---------------- 상품 등록 완료 ----------------
    @GetMapping("/addFinish")
    public String addFinish() {
        return "/WEB-INF/views/admin/product/addFinish.jsp";
    }

    // ---------------- 상품 상세 ----------------
    @GetMapping("/detail")
    public String detail(@RequestParam int productNo, Model model) {
        ProductDto product = productService.getProduct(productNo);
        if (product == null)
            throw new TargetNotfoundException("존재하지 않는 상품 번호");

        model.addAttribute("product", product);
        return "/WEB-INF/views/admin/product/detail.jsp";
    }

    // ---------------- 상품 수정 페이지 ----------------
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

        return "/WEB-INF/views/admin/product/edit.jsp";
    }

    // ---------------- 상품 수정 처리 ----------------
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

        // 상품 자체 삭제 (썸네일 + 상세 이미지 + 옵션 + 카테고리 매핑 모두 포함)
        productService.delete(productNo);

        return "redirect:list";
    }

}

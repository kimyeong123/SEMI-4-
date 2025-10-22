package com.kh.shoppingmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.shoppingmall.dao.ProductDao;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.CategoryService;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryService categoryService;

    // ---------------- 상품 등록 페이지 ----------------
    @GetMapping("/add")
    public String add(@RequestParam(required = false) Integer parentCategoryNo, Model model) {
        // 부모 카테고리 조회
        List<CategoryDto> parentCategoryList = categoryService.getParentCategories();
        model.addAttribute("parentCategoryList", parentCategoryList);

        // 선택된 부모 카테고리에 따른 하위 카테고리 조회
        List<CategoryDto> childCategoryList = null;
        if (parentCategoryNo != null) {
            childCategoryList = categoryService.getChildrenByParent(parentCategoryNo);
        }
        model.addAttribute("childCategoryList", childCategoryList);
        model.addAttribute("selectedParentNo", parentCategoryNo);

        // 뷰 이름만 반환 (InternalResourceViewResolver가 경로 처리)
        return "/WEB-INF/views/admin/product/add.jsp";
    }

    // ---------------- 상품 등록 처리 ----------------
    @PostMapping("/add")
    public String add(
            @ModelAttribute ProductDto productDto,
            @RequestParam int parentCategoryNo,
            @RequestParam int childCategoryNo) {
        
        // 상품 등록
        productDao.insert(productDto);

        // 등록 완료 페이지로 리다이렉트
        return "redirect:addFinish";
    }

    // ---------------- 상품 등록 완료 페이지 ----------------
    @GetMapping("/addFinish")
    public String addFinish() {
    	return "/WEB-INF/views/admin/product/addFinish.jsp";
    }

    // ---------------- 상품 목록 ----------------
    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(required = false) String column,
                       @RequestParam(required = false) String keyword) {

        boolean isSearch = column != null && keyword != null;
        model.addAttribute("column", column);
        model.addAttribute("keyword", keyword);

        List<ProductDto> productList = isSearch ?
                productDao.selectList(column, keyword) :
                productDao.selectList();

        model.addAttribute("productList", productList);
        return "/WEB-INF/views/admin/product/list.jsp";
    }

    // ---------------- 상품 상세 ----------------
    @GetMapping("/detail")
    public String detail(@RequestParam int productNo, Model model) {
        ProductDto productDto = productDao.selectOne(productNo);
        if (productDto == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");

        model.addAttribute("productDto", productDto);
        return "/WEB-INF/views/admin/product/detail.jsp";
    }

    // ---------------- 상품 수정 페이지 ----------------
    @GetMapping("/edit")
    public String edit(@RequestParam int productNo, Model model) {
        ProductDto productDto = productDao.selectOne(productNo);
        if (productDto == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");

        model.addAttribute("productDto", productDto);
        return "/WEB-INF/views/admin/product/edit.jsp";
    }

    // ---------------- 상품 수정 처리 ----------------
    @PostMapping("/edit")
    public String edit(@ModelAttribute ProductDto productDto) {
        productDao.update(productDto);
        return "redirect:detail?productNo=" + productDto.getProductNo();
    }

    // ---------------- 상품 삭제 ----------------
    @GetMapping("/delete")
    public String delete(@RequestParam int productNo) {
        ProductDto productDto = productDao.selectOne(productNo);
        if (productDto == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");

        productDao.delete(productNo);
        return "redirect:list";
    }
}

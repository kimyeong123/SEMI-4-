package com.kh.shoppingmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.CategoryService;

@Controller
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // ---------------- 목록 ----------------
    @GetMapping("/list")
    public String list(Model model) {
        List<CategoryDto> categoryList = categoryService.getAllCategories();
        model.addAttribute("categoryList", categoryList);
        return "/WEB-INF/views/admin/category/list.jsp";
    }

    // ---------------- 등록 페이지 ----------------
    @GetMapping("/add")
    public String addPage(Model model) {
        List<CategoryDto> parentCategoryList = categoryService.getParentCategories();
        model.addAttribute("parentCategoryList", parentCategoryList);
        return "/WEB-INF/views/admin/category/add.jsp";
    }

    // ---------------- 등록 처리 ----------------
    @PostMapping("/add")
    public String add(@ModelAttribute CategoryDto categoryDto) {
        categoryService.addCategory(categoryDto);
        return "redirect:list";
    }

    // ---------------- 수정 페이지 ----------------
    @GetMapping("/edit")
    public String editPage(@RequestParam int categoryNo, Model model) {
        List<CategoryDto> allCategories = categoryService.getAllCategories();
        CategoryDto category = null;
        for (CategoryDto c : allCategories) {
            if (c.getCategoryNo() == categoryNo) {
                category = c;
                break;
            }
        }

        if (category == null) {
            throw new TargetNotfoundException("존재하지 않는 카테고리 번호");
        }

        List<CategoryDto> parentCategoryList = categoryService.getParentCategories();
        model.addAttribute("category", category);
        model.addAttribute("parentCategoryList", parentCategoryList);

        return "/WEB-INF/views/admin/category/edit.jsp";
    }

    // ---------------- 수정 처리 ----------------
    @PostMapping("/edit")
    public String edit(@ModelAttribute CategoryDto categoryDto) {
        categoryService.updateCategory(categoryDto);
        return "redirect:list";
    }

    // ---------------- 삭제 ----------------
    @GetMapping("/delete")
    public String delete(@RequestParam int categoryNo) {
        categoryService.deleteCategory(categoryNo);
        return "redirect:list";
    }

    // ---------------- 상세 페이지 ----------------
    @GetMapping("/detail")
    public String detail(@RequestParam int categoryNo, Model model) {
        List<CategoryDto> allCategories = categoryService.getAllCategories();
        CategoryDto category = null;
        for (CategoryDto c : allCategories) {
            if (c.getCategoryNo() == categoryNo) {
                category = c;
                break;
            }
        }

        if (category == null) {
            throw new TargetNotfoundException("존재하지 않는 카테고리 번호");
        }

        model.addAttribute("category", category);
        return "/WEB-INF/views/admin/category/detail.jsp";
    }

    // ---------------- 부모 선택 시 하위 카테고리 반환 ----------------
    @GetMapping("/children")
    @ResponseBody
    public List<CategoryDto> getChildren(@RequestParam int parentCategoryNo) {
        return categoryService.getChildrenByParent(parentCategoryNo);
    }
}

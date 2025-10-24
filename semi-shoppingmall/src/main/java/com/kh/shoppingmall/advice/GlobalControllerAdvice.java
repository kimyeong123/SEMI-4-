package com.kh.shoppingmall.advice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kh.shoppingmall.service.CategoryService;
import com.kh.shoppingmall.vo.CategoryTreeVO;

@ControllerAdvice
public class GlobalControllerAdvice {
	
	@Autowired
    private CategoryService categoryService;

    // "@ModelAttribute"가 붙은 메소드는 모든 컨트롤러 요청 전에 실행됨
    @ModelAttribute("categoryTree")
    public List<CategoryTreeVO> addCategoryTreeToModel() {
    	
        return categoryService.getCategoryTree();
    }
}

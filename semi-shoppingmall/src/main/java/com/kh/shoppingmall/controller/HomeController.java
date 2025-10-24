package com.kh.shoppingmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.shoppingmall.service.CategoryService;
import com.kh.shoppingmall.vo.CategoryTreeVO;

@Controller
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;

	@RequestMapping("/") // 가장 짧은 주소 부여
	public String home(Model model) {

		//카테고리 트리 데이터 조회
		List<CategoryTreeVO> categoryTree = categoryService.getCategoryTree();

		//모델에 카테고리 트리 추가
		model.addAttribute("categoryTree", categoryTree);

		return "/WEB-INF/views/home.jsp";
	}
}

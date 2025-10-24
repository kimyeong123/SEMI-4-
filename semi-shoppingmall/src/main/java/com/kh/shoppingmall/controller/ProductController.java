package com.kh.shoppingmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.service.CategoryService;
import com.kh.shoppingmall.service.ProductService;

@Controller
@RequestMapping("/product")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	//상품 목록 
	@GetMapping("/list")
	public String list(@RequestParam(value = "column", required = false) String column,
			@RequestParam(value = "keyword", required = false) String keyword, Model model) {

		List<ProductDto> list = productService.getProductList(column, keyword);

		model.addAttribute("productList", list);
		model.addAttribute("column", column);
		model.addAttribute("keyword", keyword);

		return "/WEB-INF/views/product/list.jsp";
	}
}

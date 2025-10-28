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
	@Autowired
	private ProductOptionDao productOptionDao;

	// 인터셉터 구현후에 관리자 기능 삭제
	// 상품 목록
	@GetMapping("/list")
	
	public String list(@RequestParam(value = "column", required = false) String column,
	                       @RequestParam(value = "keyword", required = false) String keyword,
	                       @RequestParam(value = "categoryNo", required = false) Integer categoryNo,
	                       @RequestParam(required = false) String order,
	                       HttpSession session,
	                       Model model) throws SQLException {

		// 1. 필터링된 상품 조회 (정렬 기준으로 order 매개변수 사용)
		list<productdto> list = productservice.getfilteredproducts(column, keyword, categoryno, order);

		// 2. 리뷰 평균 계산
		list.forEach(p -> p.setProductAvgRating(reviewService.getAverageRating(p.
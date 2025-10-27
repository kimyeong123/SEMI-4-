package com.kh.shoppingmall.controller;

import java.sql.SQLException;
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
	                   HttpSession session,
	                   Model model) throws SQLException {

	    // 1. 필터링된 상품 조회
	    List<ProductDto> list = productService.getFilteredProducts(column, keyword, categoryNo);

	    // 2. 리뷰 평균 계산
	    list.forEach(p -> p.setProductAvgRating(reviewService.getAverageRating(p.getProductNo())));

	    // 3. 모델에 기본 정보 추가
	    model.addAttribute("productList", list);
	    model.addAttribute("column", column);
	    model.addAttribute("keyword", keyword);

	    // 4. 로그인 아이디 확인
	    String loginId = (String) session.getAttribute("loginId");

	    // 5. 위시리스트 상태 & 카운트
	    Map<Integer, Boolean> wishlistStatus = new HashMap<>();
	    model.addAttribute("wishlistStatus", wishlistStatus);
	    
	    Map<Integer, Integer> wishlistCounts = productService.getWishlistCounts();
	    model.addAttribute("wishlistCounts", wishlistCounts);

	    for (ProductDto p : list) {
	        boolean inWishlist = false;
	        if (loginId != null) {
	            // 로그인 되어 있으면 위시리스트 확인
	            inWishlist = wishlistService.checkItem(loginId, p.getProductNo());
	        }
	        // Map에 상품번호와 상태 저장
	        wishlistStatus.put(p.getProductNo(), inWishlist);
	    }

	    // 6. 카테고리 트리 추가
	    model.addAttribute("categoryTree", categoryService.getCategoryTree());

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
			@RequestParam(required = false) List<Integer> categoryNoList, @RequestParam MultipartFile thumbnailFile,
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
		if (product == null)
			throw new TargetNotfoundException("존재하지 않는 상품 번호");

		model.addAttribute("product", product);
		
		//상품 옵션 목록 조회
		List<ProductOptionDto> optionList = productOptionDao.selectListByProduct(productNo);
	    model.addAttribute("optionList", optionList);
	    
	    //위시리스트 정보 조회
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

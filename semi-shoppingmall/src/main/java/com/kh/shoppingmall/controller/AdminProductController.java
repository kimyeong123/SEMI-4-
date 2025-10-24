package com.kh.shoppingmall.controller;

import java.sql.SQLException;
import java.util.ArrayList;
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
@RequestMapping("/admin/product")
public class AdminProductController {

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
		                   @RequestParam(value = "categoryNo", required = false) Integer categoryNo,
		                   HttpSession session,
		                   Model model) throws SQLException {

			List<ProductDto> list = productService.getFilteredProducts(column, keyword, categoryNo);

		    // 리뷰 평균 계산
		    for (ProductDto p : list) {
		        double avgRating = reviewService.getAverageRating(p.getProductNo());
		        p.setProductAvgRating(avgRating);
		    }

		    // 모델에 기본 정보 추가
		    model.addAttribute("productList", list);
		    model.addAttribute("column", column);
		    model.addAttribute("keyword", keyword);

		    // 로그인 아이디 확인
//		    String loginId = (String) session.getAttribute("loginId");

		    // 위시리스트 카운트
		    Map<Integer, Integer> wishlistCounts = productService.getWishlistCounts();
		    model.addAttribute("wishlistCounts", wishlistCounts);

		    // 카테고리 트리 추가
		    model.addAttribute("categoryTree", categoryService.getCategoryTree());

		    return "/WEB-INF/views/admin/product/list.jsp";
		}

	//상품 등록
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
	@PostMapping("/add")
	public String add(@ModelAttribute ProductDto productDto,
	        @RequestParam("parentCategoryNo") Integer parentCategoryNo,
	        @RequestParam("childCategoryNo") Integer childCategoryNo,
	        @RequestParam("thumbnailFile") MultipartFile thumbnailFile,
	        @RequestParam("detailImageList") List<MultipartFile> detailImageList) throws Exception {

		// 선택된 카테고리 번호 리스트로 만들기
	    List<Integer> categoryNoList = new ArrayList<>();
	    if (parentCategoryNo != null) categoryNoList.add(parentCategoryNo);
	    if (childCategoryNo != null) categoryNoList.add(childCategoryNo);

		productService.register(productDto, new ArrayList<>(), categoryNoList, thumbnailFile, detailImageList);
		return "redirect:addFinish";
	}

	//상품 등록 완료
	@GetMapping("/addFinish")
	public String addFinish() {
		return "/WEB-INF/views/admin/product/addFinish.jsp";
	}

	// 상품 상세
	@GetMapping("/detail")
	public String detail(@RequestParam int productNo, Model model, HttpSession session) {
	    // 1. 상품 정보 조회
	    ProductDto product = productService.getProduct(productNo);
	    if (product == null) 
	        throw new TargetNotfoundException("존재하지 않는 상품 번호");

	    // 2. 위시리스트 상태 및 개수 조회
	    String loginId = (String) session.getAttribute("loginId");
	    boolean wishlisted = loginId != null && wishlistService.checkItem(loginId, productNo);
	    int wishlistCount = wishlistService.count(productNo);

	    // 3. 리뷰 목록 조회
	    List<ReviewDetailVO> reviewList = reviewService.getReviewsDetailByProduct(productNo);

	    // 3-1. 리뷰 평점 평균 계산
	    double avg = reviewService.getAverageRating(productNo);
	    product.setProductAvgRating(avg);

	    // 4. 모델에 추가
	    model.addAttribute("product", product);
	    model.addAttribute("reviewList", reviewList);
	    model.addAttribute("wishlisted", wishlisted);
	    model.addAttribute("wishlistCount", wishlistCount);

	    // 5. 관리자용 상세 JSP 반환
	    return "/WEB-INF/views/admin/product/detail.jsp";
	}


	//상품 수정 페이지
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

	//상품 삭제
	@PostMapping("/delete")
	public String delete(@RequestParam int productNo) throws Exception {
		ProductDto product = productService.getProduct(productNo);
		if (product == null)
			throw new TargetNotfoundException("존재하지 않는 상품 번호");
		productService.delete(productNo);
		return "redirect:list";
	}

}

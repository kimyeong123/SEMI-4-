package com.kh.shoppingmall.controller;

import java.sql.SQLException;
import java.time.DayOfWeek; // LocalDate 등 임포트
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.shoppingmall.dao.ProductOptionDao;
// DAO 대신 Service 사용 (ProductOptionDao 제거)
// import com.kh.shoppingmall.dao.ProductOptionDao; 
import com.kh.shoppingmall.dto.CategoryDto;
import com.kh.shoppingmall.dto.ProductDto;
import com.kh.shoppingmall.dto.ProductOptionDto; // SKU DTO
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.service.CategoryService;
import com.kh.shoppingmall.service.ProductService;
import com.kh.shoppingmall.service.ReviewService;
import com.kh.shoppingmall.service.WishlistService;
import com.kh.shoppingmall.vo.ReviewDetailVO; // Review VO

import jakarta.servlet.http.HttpSession;
import lombok.Data;

@Controller
@RequestMapping("/admin/product")
public class AdminProductController {

    @Autowired private ProductService productService;
    @Autowired private CategoryService categoryService;
    @Autowired private WishlistService wishlistService;
    @Autowired private ReviewService reviewService;
    @Autowired private ProductOptionDao productOptionDao;
    // @Autowired private ProductOptionDao productOptionDao; // DAO 대신 Service 사용

    // ================= 상품 목록 =================
    @GetMapping("/list")
    public String list(@RequestParam(value = "column", required = false) String column,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "categoryNo", required = false) Integer categoryNo,
                       @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
                       HttpSession session, Model model) throws SQLException {

        List<ProductDto> list = productService.getFilteredProducts(column, keyword, categoryNo, order);
        
        // (참고) N+1 문제: 목록 조회 후, 각 상품마다 리뷰 평균을 다시 조회하면 성능 저하. 
        // 뷰(View)에서 미리 계산해두는 것이 좋음.
        list.forEach(p -> p.setProductAvgRating(reviewService.getAverageRating(p.getProductNo())));

        model.addAttribute("productList", list);
        model.addAttribute("column", column);
        model.addAttribute("keyword", keyword);
        model.addAttribute("order", order);
        model.addAttribute("categoryNo", categoryNo);
        model.addAttribute("wishlistCounts", productService.getWishlistCounts()); // Service에 구현 필요
        model.addAttribute("categoryTree", categoryService.getCategoryTree());
        return "/WEB-INF/views/admin/product/list.jsp";
    }

    // ================= 상품 등록 페이지 =================
    @GetMapping("/add")
    public String addPage(@RequestParam(required = false) Integer parentCategoryNo, Model model) {
        model.addAttribute("parentCategoryList", categoryService.getParentCategories());
        List<CategoryDto> childList = (parentCategoryNo != null)
                ? categoryService.getChildrenByParent(parentCategoryNo)
                : new ArrayList<>();
        model.addAttribute("childCategoryList", childList);
        model.addAttribute("selectedParentNo", parentCategoryNo);
        return "/WEB-INF/views/admin/product/add.jsp";
    }

    // ================= 상품 등록 처리 (옵션은 별도 관리 가정) =================
    @PostMapping("/add")
    public String add(
        @ModelAttribute ProductDto productDto,
        @RequestParam(value = "parentCategoryNo", required = false) Integer parentCategoryNo,
        @RequestParam(value = "childCategoryNo", required = false) Integer childCategoryNo,
        @RequestParam("thumbnailFile") MultipartFile thumbnailFile
    ) throws Exception {

        List<Integer> categoryNoList = new ArrayList<>();
        if (parentCategoryNo != null) categoryNoList.add(parentCategoryNo);
        if (childCategoryNo != null) categoryNoList.add(childCategoryNo);

        // 옵션은 나중에 따로 등록할 거라서 빈 리스트(new ArrayList<>()) 전달
        int productNo = productService.register(productDto, new ArrayList<>(), categoryNoList, thumbnailFile);

        // 등록 후 → 옵션 관리 페이지로 이동
        return "redirect:/admin/product/option/manage?productNo=" + productNo; // (옵션 관리 페이지 URL 예시)
    }

    // 상품 등록 완료
    @GetMapping("/addFinish")
    public String addFinish() {
        return "/WEB-INF/views/admin/product/addFinish.jsp";
    }

    // ================= 상품 상세 (SKU 방식에 맞게 수정됨) =================
    @GetMapping("/detail")
    public String adminDetail(@RequestParam(required = false) Integer productNo,
                              Model model, HttpSession session) {

        if (productNo == null) return "redirect:/admin/product/list";

        ProductDto product = productService.getProduct(productNo);
        if (product == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");

        // 1. ✨ (수정) SKU(옵션 조합) 목록 조회
        //    ProductService가 DAO를 호출하여 "S / 치즈", "M / 블랙" 등이 담긴 리스트를 반환
        List<ProductOptionDto> optionList = productService.getOptionsByProduct(productNo);

        // 2. ❌ (삭제) 옵션 분리 로직 (SKU 방식과 맞지 않음)
        // List<String> colorList = new ArrayList<>();
        // List<String> sizeList = new ArrayList<>();
        // for (ProductOptionDto opt : optionList) { ... }

        String loginId = (String) session.getAttribute("loginId");
        boolean wishlisted = loginId != null && wishlistService.checkItem(loginId, productNo);
        
        double avg = reviewService.getAverageRating(productNo);
        // product.setProductAvgRating(avg); // DTO를 수정하는 것보다 Model에 담는 것을 권장

        // 3. 리뷰 목록 조회
        List<ReviewDetailVO> reviewList = reviewService.getReviewsDetailByProduct(productNo);
        
        // 4. 모델에 데이터 추가
        model.addAttribute("product", product);
        model.addAttribute("optionList", optionList); // ✨ (수정) SKU 목록 통째로 전달
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("wishlisted", wishlisted);
        model.addAttribute("wishlistCount", wishlistService.count(productNo)); // ✨ (수정) count -> countByProductNo
        model.addAttribute("avgRating", avg);

        // 5. (추가) 예상 도착일 (고객용 상세페이지와 로직 통일)
        LocalDate today = LocalDate.now();
        LocalDate estimatedDate = today.plusDays(4);
        DayOfWeek dayOfWeek = estimatedDate.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY) estimatedDate = estimatedDate.plusDays(2);
        else if (dayOfWeek == DayOfWeek.SUNDAY) estimatedDate = estimatedDate.plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM월 dd일(E)", Locale.KOREAN);
        model.addAttribute("estimatedDeliveryDate", estimatedDate.format(formatter));

        return "/WEB-INF/views/admin/product/detail.jsp";
    }

    // ================= 상품 수정 페이지 =================
    @GetMapping("/edit")
    public String editPage(@RequestParam int productNo, Model model) {
        ProductDto product = productService.getProduct(productNo);
        if (product == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");

        model.addAttribute("product", product);
        model.addAttribute("parentCategoryList", categoryService.getParentCategories());
        
        // (참고) productDto에 parentCategoryNo 필드가 있어야 이 로직이 작동합니다.
        List<CategoryDto> child = (product.getParentCategoryNo() != null) 
                ? categoryService.getChildrenByParent(product.getParentCategoryNo())
                : new ArrayList<>();
        model.addAttribute("childCategoryList", child);
        return "/WEB-INF/views/admin/product/edit.jsp";
    }

    // ================= 상품 수정 처리 (옵션은 별도 관리 가정) =================
    @PostMapping("/edit")
    public String edit(@ModelAttribute ProductDto productDto,
                       @RequestParam(required = false) List<Integer> categoryNoList,
                       @RequestParam(required = false) MultipartFile thumbnailFile,
                       @RequestParam(required = false) List<Integer> deleteAttachmentNoList) throws Exception {

        if (categoryNoList == null) categoryNoList = new ArrayList<>();
        
        // 옵션은 별도 관리하므로 빈 리스트(new ArrayList<>()) 전달
        productService.update(productDto, new ArrayList<>(), categoryNoList, thumbnailFile, deleteAttachmentNoList);
        
        return "redirect:detail?productNo=" + productDto.getProductNo();
    }

    // ================= 상품 삭제 =================
    @PostMapping("/delete")
    public String delete(@RequestParam int productNo) throws Exception {
        ProductDto product = productService.getProduct(productNo);
        if (product == null) throw new TargetNotfoundException("존재하지 않는 상품 번호");
        
        productService.delete(productNo);
        return "redirect:list";
    }

    // ================= 하위 카테고리 불러오기 (상품등록 AJAX용) =================
    @GetMapping("/category/children")
    public String getChildren(@RequestParam int parentCategoryNo, Model model) {
        List<CategoryDto> childCategoryList = categoryService.getChildrenByParent(parentCategoryNo);
        model.addAttribute("childCategoryList", childCategoryList);
        return "/WEB-INF/views/admin/category/children.jsp";
    }
    
 // ================= 상품 옵션(SKU) 관리 페이지 (GET) =================
    @GetMapping("/option/manage")
    public String manageOptions(@RequestParam int productNo, Model model) {
        // 1. 상품 정보 조회 (헤더에 이름 표시용)
        ProductDto product = productService.getProduct(productNo);
        if (product == null) {
            throw new TargetNotfoundException("상품을 찾을 수 없습니다.");
        }
        // 2. 현재 저장된 SKU 목록 조회
        List<ProductOptionDto> optionList = productOptionDao.selectListByProduct(productNo);

        model.addAttribute("product", product);
        model.addAttribute("optionList", optionList); // 현재 저장된 SKU 목록
        return "/WEB-INF/views/admin/option/manage.jsp"; // ✨ 새 JSP 파일
    }

    // ================= 상품 옵션(SKU) 일괄 저장 (POST) =================
    @PostMapping("/option/save")
    public String saveOptions(@RequestParam int productNo, 
                              // ✨ SKU 목록을 List<ProductOptionDto>로 받음
                              @ModelAttribute ProductOptionListWrapper vo, 
                              RedirectAttributes attr) {
        
        // ProductService에 옵션 업데이트 로직이 구현되어 있어야 함
        // (이 로직은 1. 기존 옵션 삭제 -> 2. 새 옵션 추가)
        productService.updateOptions(productNo, vo.getOptionList()); // Service에 새 메소드 필요
        
        attr.addFlashAttribute("message", "옵션이 성공적으로 저장되었습니다.");
        return "redirect:/admin/product/detail?productNo=" + productNo;
    }

    // 컨트롤러가 List<ProductOptionDto>를 직접 받기 위한 래퍼(Wrapper) 클래스
    // (별도 파일로 만들거나 컨트롤러 내부에 static class로 만들어도 됨)
    @Data // Lombok
    public static class ProductOptionListWrapper {
        private List<ProductOptionDto> optionList;
    }
}

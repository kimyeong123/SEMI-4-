package com.kh.shoppingmall.controller; // 패키지를 분리하는 것이 좋습니다.


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.shoppingmall.dto.ReviewDto; // ReviewDto가 필요할 수 있습니다.
import com.kh.shoppingmall.service.ReviewService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/review") // HTML 페이지를 위한 기본 경로
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService; // 리뷰 수정 시 기존 정보를 가져오기 위해 필요

    // 1. 리뷰 작성 페이지로 이동
    @GetMapping("/write/{productNo}")
    public String showWriteForm(@PathVariable int productNo, Model model, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        
        // 1. (인증 체크) 로그인 안 했으면 로그인 페이지로
        if (memberId == null) {
            return "redirect:/login"; // 로그인 페이지 경로로 리다이렉트
        }
        
        // 2. (구매 여부 체크 - Service에서 로직 수행)
        // boolean canWrite = reviewService.checkIfPurchased(memberId, productNo);
        // if (!canWrite) {
        //    return "redirect:/mypage/orderlist"; // 구매 내역 페이지 등으로 리다이렉트
        // }
        
        // 3. 작성 폼 페이지로 상품 번호 전달
        model.addAttribute("productNo", productNo);
        return "review/writeForm"; // "src/main/resources/templates/review/writeForm.html" 뷰를 반환
    }

    // 2. 리뷰 수정 페이지로 이동
    @GetMapping("/edit/{reviewNo}")
    public String showEditForm(@PathVariable int reviewNo, Model model, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");

        // 1. (인증 체크) 로그인 안 했으면 로그인 페이지로
        if (memberId == null) {
            return "redirect:/login";
        }
        
        // 2. 기존 리뷰 정보 가져오기
        ReviewDto reviewDto = reviewService.getReview(reviewNo); // (이런 서비스 메소드가 필요합니다)

        // 3. (권한 체크)
        // 리뷰가 없거나, 작성자가 현재 사용자가 아니면
        if (reviewDto == null || !reviewDto.getMemberId().equals(memberId)) {
            // 권한 없음 페이지 또는 상품 상세 페이지로 리다이렉트
            return "redirect:/product/detail/" + reviewDto.getProductNo(); 
        }

        // 4. 수정 폼 페이지로 기존 리뷰 데이터 전달
        model.addAttribute("review", reviewDto);
        return "review/editForm"; // "src/main/resources/templates/review/editForm.html" 뷰를 반환
    }
}
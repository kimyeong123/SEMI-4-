package com.kh.shoppingmall.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.service.ReviewService;

import jakarta.servlet.http.HttpSession;

@Controller // View 반환을 담당하는 일반 컨트롤러
@RequestMapping("/review") // 일반 컨트롤러 기본 경로
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;

    // 1. 리뷰 수정 처리 (PUT 대신 POST 사용)
    @PostMapping("/update")
    public String updateReview(
            HttpSession session,
            @ModelAttribute ReviewDto reviewDto,
            RedirectAttributes redirectAttributes) {

        String currentMemberId = (String) session.getAttribute("loginId");
        
        // 1. 로그인 체크 (인증)
        if (currentMemberId == null) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
        
        // 2. 권한 확인 (작성자 확인)
        String authorId = reviewService.getAuthorId(reviewDto.getReviewNo());

        if (authorId == null || !currentMemberId.equals(authorId)) {
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없거나 리뷰가 존재하지 않습니다.");
            // 오류 발생 시 상품 상세 페이지로 리다이렉트
            return "redirect:/product/detail?productNo=" + reviewDto.getProductNo(); 
        }
        
        // 3. 수정 진행
        boolean result = reviewService.updateReview(reviewDto);
        
        if (result) {
            redirectAttributes.addFlashAttribute("message", "리뷰가 성공적으로 수정되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "리뷰 수정에 실패했습니다.");
        }

        return "redirect:/product/detail?productNo=" + reviewDto.getProductNo();
    }

    // 2. 리뷰 삭제 처리 (DELETE 대신 POST 사용)
    @PostMapping("/delete/{reviewNo}")
    public String deleteReview(
            HttpSession session,
            @PathVariable int reviewNo,
            @RequestParam int productNo, // 리다이렉트를 위해 상품 번호 추가 수신
            RedirectAttributes redirectAttributes) {

        String currentMemberId = (String) session.getAttribute("loginId");
        
        // 1. 로그인 체크 (인증)
        if (currentMemberId == null) {
            return "redirect:/login"; 
        }
        
        // 2. 권한 확인 (작성자 확인)
        String authorId = reviewService.getAuthorId(reviewNo);

        if (authorId == null || !currentMemberId.equals(authorId)) {
            redirectAttributes.addFlashAttribute("error", "삭제 권한이 없거나 리뷰가 존재하지 않습니다.");
            return "redirect:/product/detail?productNo=" + productNo; 
        }
        
        // 3. 삭제 진행
        boolean result = reviewService.deleteReview(reviewNo);
        
        if (result) {
            redirectAttributes.addFlashAttribute("message", "리뷰가 성공적으로 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "리뷰 삭제에 실패했습니다.");
        }

        return "redirect:/product/detail?productNo=" + productNo;
    }
}
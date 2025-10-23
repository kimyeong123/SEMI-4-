package com.kh.shoppingmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.service.ReviewService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/review")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;

    // 1. 리뷰 작성 페이지로 이동
    @GetMapping("/write")
    public String showWriteForm(@RequestParam int productNo, Model model, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        
        if (memberId == null) {
            return "redirect:/login";
        }

        model.addAttribute("productNo", productNo);
        return "review/writeForm";
    }

    // 2. 리뷰 수정 페이지로 이동
    @GetMapping("/edit")
    public String showEditForm(@RequestParam int reviewNo, Model model, HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");

        if (memberId == null) {
            return "redirect:/login";
        }
        
        ReviewDto reviewDto = reviewService.getReview(reviewNo);

        if (reviewDto == null || !reviewDto.getMemberId().equals(memberId)) {
            return "redirect:/product/detail?productNo=" + (reviewDto != null ? reviewDto.getProductNo() : 0);
        }

        model.addAttribute("review", reviewDto);
        return "review/editForm";
    }

    // 3. 리뷰 수정 처리
    @PostMapping("/update")
    public String updateReview(
            HttpSession session,
            @ModelAttribute ReviewDto reviewDto,
            RedirectAttributes redirectAttributes) {

        String currentMemberId = (String) session.getAttribute("loginId");
        
        if (currentMemberId == null) {
            return "redirect:/login";
        }
        
        String authorId = reviewService.getAuthorId(reviewDto.getReviewNo());

        if (authorId == null || !currentMemberId.equals(authorId)) {
            redirectAttributes.addFlashAttribute("error", "수정 권한이 없거나 리뷰가 존재하지 않습니다.");
            return "redirect:/product/detail?productNo=" + reviewDto.getProductNo(); 
        }
        
        boolean result = reviewService.updateReview(reviewDto);
        
        if (result) {
            redirectAttributes.addFlashAttribute("message", "리뷰가 성공적으로 수정되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "리뷰 수정에 실패했습니다.");
        }

        return "redirect:/product/detail?productNo=" + reviewDto.getProductNo();
    }

    // 4. 리뷰 삭제 처리
    @PostMapping("/delete")
    public String deleteReview(
            HttpSession session,
            @RequestParam int reviewNo,
            @RequestParam int productNo,
            RedirectAttributes redirectAttributes) {

        String currentMemberId = (String) session.getAttribute("loginId");
        
        if (currentMemberId == null) {
            return "redirect:/login";
        }
        
        String authorId = reviewService.getAuthorId(reviewNo);

        if (authorId == null || !currentMemberId.equals(authorId)) {
            redirectAttributes.addFlashAttribute("error", "삭제 권한이 없거나 리뷰가 존재하지 않습니다.");
            return "redirect:/product/detail?productNo=" + productNo; 
        }
        
        boolean result = reviewService.deleteReview(reviewNo);
        
        if (result) {
            redirectAttributes.addFlashAttribute("message", "리뷰가 성공적으로 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "리뷰 삭제에 실패했습니다.");
        }

        return "redirect:/product/detail?productNo=" + productNo;
    }
}

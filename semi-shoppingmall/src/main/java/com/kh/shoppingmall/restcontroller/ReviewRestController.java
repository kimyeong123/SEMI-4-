package com.kh.shoppingmall.restcontroller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.service.ReviewService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/review")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    // 리뷰 등록 (첨부파일 있을 수도, 없을 수도 있음)
    @PostMapping("/")
    public ResponseEntity<Map<String, String>> insertReview(
            HttpSession session,
            @ModelAttribute ReviewDto reviewDto,
            @RequestParam(required = false) List<MultipartFile> attach
    ) throws IOException {

        // 로그인 안 된 사용자
        String currentMemberId = (String) session.getAttribute("loginId");
        if (currentMemberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "로그인이 필요합니다."));
        }

        reviewDto.setMemberId(currentMemberId);

        // attach가 null이면 빈 리스트로 대체
        if (attach == null) attach = List.of();

        reviewService.insertReview(reviewDto, attach);

        return ResponseEntity.ok(Map.of("message", "리뷰가 성공적으로 등록되었습니다."));
    }
}

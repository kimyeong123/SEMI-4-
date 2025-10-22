package com.kh.shoppingmall.restcontroller;

import java.io.IOException;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.service.ReviewService;
import com.kh.shoppingmall.vo.ReviewDetailVO;

import jakarta.servlet.http.HttpSession;

@RestController // 이 클래스가 REST API 요청을 처리함을 나타냅니다.
@RequestMapping("/rest/review") // 모든 메서드의 기본 경로를 "/rest/review"로 설정합니다.
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService; // 리뷰 관련 비즈니스 로직 처리를 담당합니다.

    // 리뷰 목록 조회 (GET)
    // 경로: /rest/review/list/{productNo}
    @GetMapping("/list/{productNo}")
    public ResponseEntity<List<ReviewDetailVO>> getReviewsDetailByProduct(
            @PathVariable int productNo) { // URL 경로에서 상품 번호를 추출합니다.

        // 서비스에서 상세 리뷰 목록을 조회합니다.
        List<ReviewDetailVO> list = reviewService.getReviewsDetailByProduct(productNo);

        // 조회된 목록과 함께 200 OK 상태 코드를 반환합니다.
        return ResponseEntity.ok(list);
    }

    // 리뷰 등록 (POST)
    // 경로: /rest/review/
    // [수정] try-catch를 제거하고, throws IOException을 메소드 시그니처에 추가합니다.
    @PostMapping("/")
    public ResponseEntity<Map<String, String>> insertReview(
            // HttpSession을 사용하여 로그인된 사용자 정보를 가져옵니다.
            HttpSession session,
            // 리뷰 DTO는 @ModelAttribute로 받습니다. (파일 첨부 시 함께 사용하기 위함)
            @ModelAttribute ReviewDto reviewDto,
            // 첨부 파일은 @RequestPart로 받습니다. (multipart/form-data 요청의 파일 파트)
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) 
            throws IOException { // 파일 처리 중 오류가 발생할 수 있으므로 선언

        // 1. (인증 체크) 세션에서 로그인 ID를 가져와 현재 사용자 ID로 설정합니다.
        String currentMemberId = (String) session.getAttribute("loginId"); // 세션 키는 "loginId"로 가정
        if (currentMemberId == null) {
            // 로그인되어 있지 않다면 401 Unauthorized 상태를 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        reviewDto.setMemberId(currentMemberId); // 리뷰 DTO에 작성자 ID 설정

        // 2. 서비스에 등록 요청
        reviewService.insertReview(reviewDto, attachments);

        // 3. 성공 응답: 새로운 리소스 생성 시 201 Created 상태 코드를 반환합니다.
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "리뷰가 성공적으로 등록되었습니다."));
    }

    // 리뷰 수정 (PUT)
    // 경로: /rest/review/
    @PutMapping("/")
    public ResponseEntity<Map<String, String>> updateReview(
            // HttpSession을 사용하여 로그인된 사용자 정보를 가져옵니다.
            HttpSession session,
            // 수정 데이터는 주로 JSON 형태로 요청 본문(@RequestBody)을 통해 받습니다.
            @RequestBody ReviewDto reviewDto) {

        // 1. (인증 체크) 세션에서 로그인 ID를 가져와 현재 사용자 ID로 설정합니다.
        String currentMemberId = (String) session.getAttribute("loginId");
        if (currentMemberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        // 2. (권한 체크) 리뷰 번호로 작성자 ID를 조회하여 현재 사용자와 일치하는지 확인합니다.
        String authorId = reviewService.getAuthorId(reviewDto.getReviewNo()); // 서비스에서 작성자 ID 조회
        if (authorId == null) {
            // 리뷰가 존재하지 않음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 리뷰를 찾을 수 없습니다."));
        }
        if (!currentMemberId.equals(authorId)) {
            // 작성자가 아닐 경우 403 Forbidden 상태를 반환합니다.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "리뷰 수정 권한이 없습니다."));
        }

        // 3. 서비스에 수정 요청
        boolean result = reviewService.updateReview(reviewDto);

        // 4. 응답
        if (result) {
            return ResponseEntity.ok(Map.of("message", "리뷰가 성공적으로 수정되었습니다.")); // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "수정 대상 리뷰를 찾을 수 없습니다."));
        }
    }

    // 리뷰 삭제 (DELETE)
    // 경로: /rest/review/{reviewNo}
    @DeleteMapping("/{reviewNo}")
    public ResponseEntity<Map<String, String>> deleteReview(
            // HttpSession을 사용하여 로그인된 사용자 정보를 가져옵니다.
            HttpSession session,
            // 삭제할 리뷰 번호를 URL 경로에서 추출합니다.
            @PathVariable int reviewNo) {

        // 1. (인증 체크) 세션에서 로그인 ID를 가져와 현재 사용자 ID로 설정합니다.
        String currentMemberId = (String) session.getAttribute("loginId");
        if (currentMemberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        // 2. (권한 체크) 리뷰 번호로 작성자 ID를 조회하여 현재 사용자와 일치하는지 확인합니다.
        String authorId = reviewService.getAuthorId(reviewNo); // 서비스에서 작성자 ID 조회
        if (authorId == null) {
            // 리뷰가 존재하지 않음
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "해당 리뷰를 찾을 수 없습니다."));
        }
        if (!currentMemberId.equals(authorId)) {
            // 작성자가 아닐 경우 403 Forbidden 상태를 반환합니다.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "리뷰 삭제 권한이 없습니다."));
        }

        // 3. 서비스에 삭제 요청
        boolean result = reviewService.deleteReview(reviewNo);

        // 4. 응답
        if (result) {
            return ResponseEntity.ok(Map.of("message", "리뷰가 성공적으로 삭제되었습니다.")); // 200 OK
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "삭제 대상 리뷰를 찾을 수 없습니다."));
        }
    }
}
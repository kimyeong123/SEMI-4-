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

@RestController
@RequestMapping("/rest/review") // REST API 기본 경로 설정
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    // 1. 특정 상품의 상세 리뷰 목록 조회 (READ)
    // 특정 상품에 대한 상세 리뷰 목록을 조회하는 API
    @GetMapping("/list/{productNo}")
    public ResponseEntity<List<ReviewDetailVO>> getReviewsByProduct(@PathVariable int productNo) {
        
        List<ReviewDetailVO> reviewList = reviewService.getReviewsDetailByProduct(productNo);
        
        return ResponseEntity.ok(reviewList);
    }
    
    // 2. 리뷰 등록 (CREATE - 파일 포함)
    // 리뷰 등록 요청을 처리하고 결과를 JSON으로 반환합니다.
    @PostMapping("/")
    public ResponseEntity<Map<String, String>> insertReview(
            HttpSession session, // 
            @ModelAttribute ReviewDto reviewDto, 
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) { 
        
        // ** (HttpSession 사용) 세션에서 로그인 ID를 가져옵니다. **
        String currentMemberId = (String) session.getAttribute("loginId");
        
        // ** (미인증 사용자 처리) 로그인 ID가 없으면 401 Unauthorized 반환 **
        if (currentMemberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        
        reviewDto.setMemberId(currentMemberId);
        
        try {
            reviewService.insertReview(reviewDto, attachments);
            // 성공 시 201 Created 상태와 메시지를 반환합니다.
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "리뷰가 성공적으로 등록되었습니다."));
        } catch (IOException e) { 
            // 파일 처리 중 발생한 오류는 400 Bad Request로 처리
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "파일 업로드 중 오류가 발생했습니다. 첨부 파일을 확인해 주세요."));
        } catch (Exception e) {
            // 그 외 DB 트랜잭션 오류 등은 500 Internal Server Error 상태 반환
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "리뷰 등록 중 시스템 오류가 발생했습니다."));
        }
    }
    
    // 3. 리뷰 수정 (UPDATE - JSON 데이터 사용)
    // 기존 리뷰를 수정하는 API (내용 및 평점)
    @PutMapping("/")
    public ResponseEntity<Map<String, String>> updateReview(
            HttpSession session, 
            @RequestBody ReviewDto reviewDto) {
        
        // ** (HttpSession 사용) 세션에서 로그인 ID를 가져와 권한을 확인합니다. **
        String currentMemberId = (String) session.getAttribute("loginId");
        
        // ** (미인증 사용자 처리) 로그인 ID가 없으면 401 Unauthorized 반환 **
        if (currentMemberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        
        String authorId = reviewService.getAuthorId(reviewDto.getReviewNo());

        if (authorId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "수정하려는 리뷰가 존재하지 않습니다."));
        }
        
        if (!currentMemberId.equals(authorId)) {
            // 작성자가 아닌 경우 403 Forbidden 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "리뷰 수정 권한이 없습니다."));
        }
        
        // 권한 확인 통과 후 수정 진행
        boolean result = reviewService.updateReview(reviewDto);
        
        if (result) {
            return ResponseEntity.ok(Map.of("message", "리뷰가 성공적으로 수정되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "리뷰 수정에 실패했습니다. (DB 오류)"));
        }
    }

    // 4. 리뷰 삭제 (DELETE)
    // 특정 리뷰를 삭제하는 API
    @DeleteMapping("/{reviewNo}")
    public ResponseEntity<Map<String, String>> deleteReview(
            HttpSession session, // 
            @PathVariable int reviewNo) {
        
        // ** (HttpSession 사용) 세션에서 로그인 ID를 가져와 권한을 확인합니다. **
        String currentMemberId = (String) session.getAttribute("loginId");
        
        // ** (미인증 사용자 처리) 로그인 ID가 없으면 401 Unauthorized 반환 **
        if (currentMemberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }
        
        String authorId = reviewService.getAuthorId(reviewNo);

        if (authorId == null) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "삭제하려는 리뷰가 존재하지 않습니다."));
        }
        
        if (!currentMemberId.equals(authorId)) {
            // 작성자가 아닌 경우 403 Forbidden 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "리뷰 삭제 권한이 없습니다."));
        }
        
        // 권한 확인 통과 후 삭제 진행
        boolean result = reviewService.deleteReview(reviewNo);
        
        if (result) {
            return ResponseEntity.ok(Map.of("message", "리뷰가 성공적으로 삭제되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "리뷰 삭제에 실패했습니다. (DB 오류)"));
        }
    }
}
package com.kh.shoppingmall.restcontroller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.shoppingmall.service.WishlistService;

import jakarta.servlet.http.HttpSession;

@RestController // REST API 컨트롤러
@RequestMapping("/rest/wishlist") // 기본 경로 설정
public class WishlistRestController {

    @Autowired
    private WishlistService wishlistService; // 찜하기 관련 비즈니스 로직 처리

    // 1. 찜하기 추가 (POST /rest/wishlist)
    // 상품 상세 페이지 등에서 '찜하기' 버튼 클릭 시 호출
    @PostMapping("/")
    public ResponseEntity<Map<String, String>> addWishlist(
            HttpSession session, 
            @RequestParam int productNo) { // 찜할 상품 번호를 요청 파라미터로 받습니다.

        // (인증 체크) 세션에서 로그인 ID를 가져옵니다.
        String currentMemberId = (String) session.getAttribute("loginId");
        if (currentMemberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        // 서비스 호출 및 결과 처리
        try {
            boolean isSuccess = wishlistService.insertWishlist(currentMemberId, productNo);
            if (isSuccess) {
                // 201 Created는 리소스가 성공적으로 생성되었음을 의미합니다.
                return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "찜하기에 추가되었습니다."));
            } else {
                // 이미 찜한 상태일 가능성, 혹은 DB 처리 오류
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "이미 찜한 상품입니다."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "찜하기 처리 중 시스템 오류가 발생했습니다."));
        }
    }
    
    // 2. 찜하기 삭제 (DELETE /rest/wishlist)
    // 찜 목록 페이지나 상품 상세 페이지에서 '찜하기 취소' 시 호출
    @DeleteMapping("/")
    public ResponseEntity<Map<String, String>> deleteWishlist(
            HttpSession session, 
            @RequestParam int productNo) { // 찜 취소할 상품 번호를 요청 파라미터로 받습니다.

        // (인증 체크) 세션에서 로그인 ID를 가져옵니다.
        String currentMemberId = (String) session.getAttribute("loginId");
        if (currentMemberId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        // 서비스 호출 및 결과 처리
        boolean isSuccess = wishlistService.deleteWishlist(currentMemberId, productNo);
        
        if (isSuccess) {
            return ResponseEntity.ok(Map.of("message", "찜하기가 취소되었습니다.")); // 200 OK
        } else {
            // 삭제할 대상이 없거나, DB 처리 실패
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "찜하기 목록에서 해당 상품을 찾을 수 없습니다."));
        }
    }

     // 찜하기 여부 확인 (GET /rest/wishlist/check?productNo=...)
     // 상품 상세 페이지 로딩 시, 해당 상품을 찜했는지 확인
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkWishlist(
            HttpSession session, 
            @RequestParam int productNo) {

        // (인증 체크) 세션에서 로그인 ID를 가져옵니다.
        String currentMemberId = (String) session.getAttribute("loginId");
        
        // ** (미인증 사용자 처리) 로그인하지 않은 경우 'false'를 반환합니다. **
        if (currentMemberId == null) {
            // 로그인해야만 찜하기 상태를 확인할 수 있으므로, false 응답과 함께 200 OK를 반환하거나 
            // 401 Unauthorized를 반환할 수 있지만, 여기서는 간결하게 false 응답을 보냅니다.
            return ResponseEntity.ok(Map.of("isWished", false));
        }
        
        // 서비스 호출: 찜 여부 확인
        boolean isWished = wishlistService.checkWishlist(currentMemberId, productNo);
        
        // 찜 여부 결과와 함께 200 OK를 반환합니다.
        return ResponseEntity.ok(Map.of("isWished", isWished));
    }
}
package com.kh.shoppingmall.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.shoppingmall.error.NeedPermissionException;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.error.UnauthorizationException;
import com.kh.shoppingmall.service.ReviewService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class ReviewAuthorInterceptor implements HandlerInterceptor { 

    @Autowired
    private ReviewService reviewService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 1. 현재 로그인 사용자 ID 확인 (세션에서)
        HttpSession session = request.getSession();
        String currentMemberId = (String) session.getAttribute("loginId");

        // 비로그인 상태면 예외 발생 (401 Unauthorized)
        if (currentMemberId == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }

        // 2. 요청 파라미터에서 reviewNo 추출
        String reviewNoStr = request.getParameter("reviewNo");
        
        // reviewNo가 필수적인 요청인데 누락된 경우 처리
        if (reviewNoStr == null || reviewNoStr.trim().isEmpty()) {
             throw new TargetNotfoundException("리뷰 번호가 필요합니다.");
        }

        int reviewNo;
        try {
            reviewNo = Integer.parseInt(reviewNoStr);
        } catch (NumberFormatException e) {
            throw new TargetNotfoundException("유효하지 않은 리뷰 번호입니다.");
        }

        // 3. Service를 통해 리뷰 작성자 ID 확인
        String authorId = reviewService.getAuthorId(reviewNo);

        // 해당 리뷰가 존재하지 않으면 예외 발생 (404 Not Found)
        if (authorId == null) {
            throw new TargetNotfoundException("해당 리뷰를 찾을 수 없습니다.");
        }

        // 4. 작성자와 로그인 사용자 ID 비교
        if (!currentMemberId.equals(authorId)) {
            // 작성자가 아니면 예외 발생 (403 Forbidden)
            throw new NeedPermissionException("리뷰 수정/삭제 권한이 없습니다.");
        }

        // 5. 모든 검증 통과 시 요청 처리 계속
        return true;
    }
}
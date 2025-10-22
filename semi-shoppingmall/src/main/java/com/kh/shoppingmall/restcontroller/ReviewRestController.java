package com.kh.shoppingmall.restcontroller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.error.NeedPermissionException;
import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.error.UnauthorizationException;
import com.kh.shoppingmall.service.ReviewService;
import com.kh.shoppingmall.vo.ReviewDetailVO;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/rest/review")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    // 1. 리뷰 목록 조회 (비회원도 가능)
    @GetMapping("/list")
    public List<ReviewDetailVO> list(@RequestParam int productNo) {
        return reviewService.getReviewsDetailByProduct(productNo);
    }

    // 2. 리뷰 작성 (회원만)
    @PostMapping("/add")
    public boolean addReview(
            HttpSession session,
            @ModelAttribute ReviewDto reviewDto,
            @RequestParam(required = false) List<MultipartFile> attachments) throws IOException {

        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) throw new UnauthorizationException("로그인이 필요합니다.");

        reviewDto.setMemberId(memberId);
        if (attachments == null) attachments = List.of();

        return reviewService.insertReview(reviewDto, attachments);
    }

    // 3. 리뷰 수정 (작성자만)
    @PostMapping("/update")
    public boolean updateReview(HttpSession session, @RequestBody ReviewDto reviewDto) {

        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) throw new UnauthorizationException("로그인이 필요합니다.");

        String authorId = reviewService.getAuthorId(reviewDto.getReviewNo());
        if (authorId == null) throw new TargetNotfoundException("해당 리뷰를 찾을 수 없습니다.");
        if (!memberId.equals(authorId)) throw new NeedPermissionException("리뷰 수정 권한이 없습니다.");

        return reviewService.updateReview(reviewDto);
    }

    // 4. 리뷰 삭제 (작성자만)
    @PostMapping("/delete")
    public boolean deleteReview(HttpSession session, @RequestParam int reviewNo) {

        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) throw new UnauthorizationException("로그인이 필요합니다.");

        String authorId = reviewService.getAuthorId(reviewNo);
        if (authorId == null) throw new TargetNotfoundException("해당 리뷰를 찾을 수 없습니다.");
        if (!memberId.equals(authorId)) throw new NeedPermissionException("리뷰 삭제 권한이 없습니다.");

        return reviewService.deleteReview(reviewNo);
    }
}
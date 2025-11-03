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
@CrossOrigin
@RestController
@RequestMapping("/rest/review")
public class ReviewRestController {

    @Autowired
    private ReviewService reviewService;

    // 1. 리뷰 목록 조회 (비회원도 가능)
    @GetMapping("/list")
    public List<ReviewDetailVO> getReviews(@RequestParam int productNo) {
        return reviewService.getReviewsDetailByProduct(productNo);
    }

    // 2. 리뷰 등록
    @PostMapping("/add")
    public boolean addReview(
            HttpSession session,
            @ModelAttribute ReviewDto reviewDto,
            @RequestParam(required = false) List<MultipartFile> attachments) throws IOException {

        String currentMemberId = (String) session.getAttribute("loginId");
        if (currentMemberId == null) throw new UnauthorizationException("로그인이 필요합니다.");

        reviewDto.setMemberId(currentMemberId);
        if (attachments == null) attachments = List.of();

        return reviewService.insertReview(reviewDto, attachments);
    }

    @PostMapping("/update")
    public boolean updateReview(
            HttpSession session,
            @RequestParam int reviewNo,
            @RequestParam String reviewContent) {

        String currentMemberId = (String) session.getAttribute("loginId");
        if (currentMemberId == null)
            throw new UnauthorizationException("로그인이 필요합니다.");

        // 리뷰 존재 확인
        ReviewDto findDto = reviewService.selectOne(reviewNo);
        if (findDto == null)
            throw new TargetNotfoundException("존재하지 않는 리뷰");

        // 작성자 확인
        if (!currentMemberId.equals(findDto.getMemberId()))
            throw new NeedPermissionException("권한 부족");

        // 수정
        findDto.setReviewContent(reviewContent);
        return reviewService.updateReview(findDto);
    }


    // 4. 리뷰 삭제
    @PostMapping("/delete")
    public boolean deleteReview(HttpSession session, @RequestParam int reviewNo) {

        String currentMemberId = (String) session.getAttribute("loginId");
        if (currentMemberId == null) throw new UnauthorizationException("로그인이 필요합니다.");

        String authorId = reviewService.getAuthorId(reviewNo);
        if (authorId == null) throw new TargetNotfoundException("해당 리뷰를 찾을 수 없습니다.");
        if (!currentMemberId.equals(authorId)) throw new NeedPermissionException("리뷰 삭제 권한이 없습니다.");

        return reviewService.deleteReview(reviewNo); 
    }
}

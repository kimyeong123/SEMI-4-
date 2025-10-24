package com.kh.shoppingmall.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.ReviewDao;
import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.vo.ReviewDetailVO;

@Service
public class ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    @Autowired
    private AttachmentService attachmentService; 

    // ================= 리뷰 등록 =================
    @Transactional(rollbackFor = Exception.class)
    public boolean insertReview(ReviewDto reviewDto, List<MultipartFile> attachments) throws IOException {
        int reviewNo = reviewDao.insert(reviewDto);
        reviewDto.setReviewNo(reviewNo);

        // 첨부파일 처리
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    int attachmentNo = attachmentService.save(file);
                    attachmentService.updateReviewNo(attachmentNo, reviewNo);
                }
            }
        }

        // 더 이상 평점 갱신 호출 없음
        return true;
    }

    // ================= 리뷰 수정 =================
    @Transactional
    public boolean updateReview(ReviewDto reviewDto) {
        return reviewDao.update(reviewDto);
    }

    // ================= 리뷰 삭제 =================
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReview(int reviewNo) {
        ReviewDto findDto = reviewDao.selectOne(reviewNo);
        if (findDto == null) return false;

        List<Integer> deleteAttachmentNoList = attachmentService.selectAttachmentNosByReviewNo(reviewNo);

        boolean result = reviewDao.delete(reviewNo);
        if (!result) return false;

        // 첨부파일 삭제 (실패해도 트랜잭션에 영향 없음)
        for (Integer attachmentNo : deleteAttachmentNoList) {
            try {
                attachmentService.delete(attachmentNo);
            } catch (Exception e) {
                System.err.println("Attachment delete failed: " + attachmentNo);
                e.printStackTrace();
            }
        }

        // 더 이상 평점 갱신 호출 없음
        return true;
    }

    // ================= 조회 =================
    public List<ReviewDetailVO> getReviewsDetailByMember(String memberId) {
        return reviewDao.selectDetailListByMember(memberId);
    }

    public List<ReviewDetailVO> getReviewsDetailByProduct(int productNo) {
        return reviewDao.selectDetailListByProduct(productNo);
    }

    public ReviewDto getReview(int reviewNo) { 
        return reviewDao.selectOne(reviewNo);
    }

    public String getAuthorId(int reviewNo) {
        return reviewDao.selectAuthorId(reviewNo);
    }
    
    public ReviewDto selectOne(int reviewNo) {
        return reviewDao.selectOne(reviewNo);
    }

    // ================= 평균 평점 조회 =================
    public double getAverageRating(int productNo) {
        Double avg = reviewDao.selectAverageRating(productNo);
        return avg != null ? avg : 0.0;
    }
}

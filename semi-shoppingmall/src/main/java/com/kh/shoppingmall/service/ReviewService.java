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
    
    @Autowired
    private ProductService productService; 

    // ================= 리뷰 등록 =================
    @Transactional(rollbackFor = Exception.class)
    public boolean insertReview(ReviewDto reviewDto, List<MultipartFile> attachments) throws IOException {
        int reviewNo = reviewDao.insert(reviewDto);
        reviewDto.setReviewNo(reviewNo);
        int productNo = reviewDto.getProductNo();
        
        // 첨부파일 처리
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    int attachmentNo = attachmentService.save(file);
                    attachmentService.updateReviewNo(attachmentNo, reviewNo);
                }
            }
        }
        
        // 상품 평점 갱신
        productService.refreshAvgRating(productNo);
        return true;
    }

    // ================= 리뷰 수정 =================
    @Transactional
    public boolean updateReview(ReviewDto reviewDto) {
        int productNo = reviewDto.getProductNo();
        boolean result = reviewDao.update(reviewDto);

        if (result) {
            productService.refreshAvgRating(productNo);
        }

        return result;
    }

    // ================= 리뷰 삭제 =================
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReview(int reviewNo) {
        ReviewDto findDto = reviewDao.selectOne(reviewNo);
        if (findDto == null) return false;

        int productNo = findDto.getProductNo();
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

        // 상품 평점 갱신
        try {
            productService.refreshAvgRating(productNo);
        } catch (Exception e) {
            System.err.println("Average rating update failed for productNo: " + productNo);
            e.printStackTrace();
        }

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
}

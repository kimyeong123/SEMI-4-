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
	
    // 파일 처리 서비스
    @Autowired
    private AttachmentService attachmentService; 
	
    // 상품 평점 업데이트 서비스
    @Autowired
    private ProductService productService; 

    // 리뷰 등록 및 첨부파일 처리 (트랜잭션 적용)
    @Transactional(rollbackFor = Exception.class)
    public boolean insertReview(ReviewDto reviewDto, List<MultipartFile> attachments) throws IOException {
        // 1. 리뷰 DB 등록 및 번호 획득
        int reviewNo = reviewDao.insert(reviewDto);
        reviewDto.setReviewNo(reviewNo);
        int productNo = reviewDto.getProductNo();
		
        // 2. 첨부파일 처리
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                if (!file.isEmpty()) {
                    int attachmentNo = attachmentService.save(file);
                    attachmentService.updateReviewNo(attachmentNo, reviewNo);
                }
            }
        }
		
        // 3. 상품 평균 평점 업데이트
        productService.updateAverageRatingForProduct(productNo);
        return true;
    }
	
    // 리뷰 수정 및 평점 업데이트
    @Transactional
    public boolean updateReview(ReviewDto reviewDto) {
        int productNo = reviewDto.getProductNo();
		
        boolean result = reviewDao.update(reviewDto);
		
        if (result) {
            productService.updateAverageRatingForProduct(productNo);
        }
		
        return result;
    }
	
    // 리뷰 삭제 및 첨부파일 정리 (트랜잭션 적용)
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteReview(int reviewNo) {
        // 1. 삭제 대상 리뷰 조회
        ReviewDto findDto = reviewDao.selectOne(reviewNo);
        if (findDto == null) return false;
	    
        // 2. 첨부 파일 목록 조회
        List<Integer> deleteAttachmentNoList = attachmentService.selectAttachmentNosByReviewNo(reviewNo);
	    
        // 3. 리뷰 삭제
        boolean result = reviewDao.delete(reviewNo);
	    
        if (result) {
            // 4. 첨부파일 삭제 (DB + 실제 파일)
            for (Integer attachmentNo : deleteAttachmentNoList) {
                attachmentService.delete(attachmentNo);
            }
            // 5. 상품 평점 갱신
            productService.updateAverageRatingForProduct(findDto.getProductNo());
            return true;
        }
	    
        return false;
    }
	
    // --- 조회 및 유틸리티 로직 ---
	
    // 회원이 작성한 리뷰 상세 목록 조회
    public List<ReviewDetailVO> getReviewsDetailByMember(String memberId) {
        return reviewDao.selectDetailListByMember(memberId);
    }
	
    // 특정 상품의 리뷰 상세 목록 조회
    public List<ReviewDetailVO> getReviewsDetailByProduct(int productNo) {
        return reviewDao.selectDetailListByProduct(productNo);
    }
	
    // 리뷰 단건 조회
    public ReviewDto getReview(int reviewNo) { 
        return reviewDao.selectOne(reviewNo);
    }

    // 리뷰 작성자 ID 조회
    public String getAuthorId(int reviewNo) {
        return reviewDao.selectAuthorId(reviewNo);
    }
}

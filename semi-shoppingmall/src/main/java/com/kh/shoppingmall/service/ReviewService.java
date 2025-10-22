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
	
	// 책임 위임: 파일 처리 서비스
	@Autowired
	private AttachmentService attachmentService; 
	
	// 책임 위임: 상품 평점 업데이트 서비스
	@Autowired
	private ProductService productService; 


	// 리뷰 등록 및 첨부파일 처리 (트랜잭션 적용)
	@Transactional(rollbackFor = Exception.class)
	public boolean insertReview(ReviewDto reviewDto, List<MultipartFile> attachments) throws IOException {
		
		// 1. 리뷰 DB 등록 및 번호 획득
		int reviewNo = reviewDao.insert(reviewDto);
		reviewDto.setReviewNo(reviewNo); // DTO에 리뷰 번호 설정
		int productNo = reviewDto.getProductNo();
		
		// 2. 첨부파일 처리 (AttachmentService 위임)
		if(attachments != null && !attachments.isEmpty()) {
			for(MultipartFile file : attachments) {
				if(!file.isEmpty()) {
					// 2-1. 파일 저장 (물리적 저장 및 DB 메타데이터 기록)
					int attachmentNo = attachmentService.save(file);
					
					// 2-2. 파일과 리뷰 연결
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
		
		// 1. 리뷰 DB 수정
		boolean result = reviewDao.update(reviewDto);
		
		if(result) {
			// 2. 상품 평균 평점 업데이트
			productService.updateAverageRatingForProduct(productNo);
		}
		
		return result;
	}
	
	// 리뷰 삭제, 첨부파일 정리 및 평점 업데이트 (트랜잭션 적용)
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteReview(int reviewNo) {
	    // 1. 삭제할 리뷰 정보 조회 (productNo 획득 목적)
	    ReviewDto findDto = reviewDao.selectOne(reviewNo);
	    if (findDto == null) {
	         return false; // 리뷰가 없으면 실패
	    }
	    
	    // 2. 첨부 파일 정리: 삭제할 파일 번호 목록 조회
        List<Integer> deleteAttachmentNoList = attachmentService.selectAttachmentNosByReviewNo(reviewNo);
	    
	    // 3. 리뷰 DB에서 삭제
	    boolean result = reviewDao.delete(reviewNo);
	    
	    if(result) {
	        // 4. 첨부 파일 정리: AttachmentService에 위임하여 DB 정보와 물리 파일 모두 삭제
	    	for (Integer attachmentNo : deleteAttachmentNoList) {
	    		attachmentService.delete(attachmentNo);
	    	}

	        // 5. 상품 평균 평점 업데이트
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
	
	// 특정 상품에 대한 리뷰 상세 목록 조회
	// 상품 번호를 기준으로 해당 상품의 모든 리뷰 상세 정보를 조회합니다.
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
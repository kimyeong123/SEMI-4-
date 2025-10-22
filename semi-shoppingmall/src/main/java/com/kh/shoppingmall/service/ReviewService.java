package com.kh.shoppingmall.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.AttachmentDao;
import com.kh.shoppingmall.dao.ReviewDao;
import com.kh.shoppingmall.dto.AttachmentDto;
import com.kh.shoppingmall.dto.ReviewDto;
import com.kh.shoppingmall.vo.ReviewDetailVO;

@Service
public class ReviewService {
	@Autowired
	private ReviewDao reviewDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private ProductService productService;

	// 임시 업로드 경로 설정 (실제 환경에서는 설정 파일에서 값을 주입받아 사용해야 합니다.)
	private final String uploadPath = "C:/upload/review/";

	@Transactional
	public boolean insertReview(ReviewDto reviewDto, List<MultipartFile> attachments) throws IOException {
//		리뷰 번호 시퀀스 생성
		int reviewNo = reviewDao.sequence();
		reviewDto.setReviewNo(reviewNo);
		int productNo = reviewDto.getProductNo();

//		리뷰 DB에 등록
		reviewDao.insert(reviewDto);

//		첨부파일 처리
		if (attachments != null && !attachments.isEmpty()) {
			for (MultipartFile file : attachments) {
				if (!file.isEmpty()) {
//					파일 시스템에 파일 저장
					String originalFilename = file.getOriginalFilename();
					String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
					File targetFile = new File(uploadPath, savedFilename);

//					파일 저장 디렉토리가 없으면 생성
					if (!targetFile.getParentFile().exists()) {
						targetFile.getParentFile().mkdirs();
					}
					file.transferTo(targetFile); // 실제 파일 저장

//					Attachment 메타 정보 DB에 저장
					AttachmentDto attachmentDto = AttachmentDto.builder().attachmentName(savedFilename)
							.attachmentType(file.getContentType()).attachmentSize(file.getSize()).reviewNo(reviewNo)
							.productNo(productNo).build();

					attachmentDao.insert(attachmentDto);
				}
			}
		}

//		상품 평균 평점 업데이트 (ProductService 생긴후에 주석해제 메서드 이름은 수정할듯)
		productService.updateAverageRatingForProduct(productNo);

		return true;
	}

//	리뷰 수정 및 평점 업데이트
	@Transactional
	public boolean updateReview(ReviewDto reviewDto) {
		int productNo = reviewDto.getProductNo();
		boolean result = reviewDao.update(reviewDto);

		if (result) {
			productService.updateAverageRatingForProduct(productNo);
		}

		return result;
	}

//	리뷰 삭제 및 평점 업데이트
	@Transactional(rollbackFor = Exception.class)
	public boolean deleteReview(int reviewNo) {
		ReviewDto findDto = reviewDao.selectOne(reviewNo);
		if (findDto == null) {
			return false;
		}
		List<AttachmentDto> attachments = attachmentDao.selectListByReviewNo(reviewNo);

		boolean result = reviewDao.delete(reviewNo); // 첨부 파일 DB에서 삭제

		if (result) {
//			첨부 파일 정리 물리적 삭제
			attachmentDao.deleteByReviewNo(reviewNo);

			for (AttachmentDto attach : attachments) {
				File targetFile = new File(uploadPath, attach.getAttachmentName());
				// 파일이 존재하면 삭제
				if (targetFile.exists()) {
					targetFile.delete();
				}
			}

//			 상품 평균 평점 업데이트 (ProductService)
			 productService.updateAverageRatingForProduct(findDto.getProductNo());
			
			return true;
		}
		
//		리뷰 삭제 실패시
		return false;
	}

//	조회 및 유틸
	public List<ReviewDetailVO> getReviewsDetailByMember(String memberId) {
		return null; // 내가 쓴 리뷰목록 조회
	}

	public ReviewDto getReview(int reviewNo) { // 리뷰 수정을 위해 불러오기
		return reviewDao.selectOne(reviewNo);
	}

}

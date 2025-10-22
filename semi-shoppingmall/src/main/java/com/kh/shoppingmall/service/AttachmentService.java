package com.kh.shoppingmall.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.AttachmentDao;
import com.kh.shoppingmall.dto.AttachmentDto;
import com.kh.shoppingmall.error.TargetNotfoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AttachmentService {
    
    private static final Logger log = LoggerFactory.getLogger(AttachmentService.class);
    
	//DB 처리를 위한 도구를 주입
	@Autowired
	private AttachmentDao attachmentDao;
	
	//파일 저장을 위한 경로 설정
	private final String uploadPath = System.getProperty("user.home") + "/upload";
	private File upload = new File(uploadPath); 
	
	/**
	 * 파일 저장 (실물 저장 + DB 저장)
	 * @return 생성한 파일의 번호 (attachmentNo)
	 */
	@Transactional
	public int save(MultipartFile attach) throws IllegalStateException, IOException {
		int attachmentNo = attachmentDao.sequence(); // 번호를 미리 생성
		
		// 파일 저장을 위한 파일 인스턴스가 필요
		if(upload.exists() == false) { // 업로드할 폴더가 존재하지 않는다면
			upload.mkdirs(); // 생성하세요!
		}
		
		File target = new File(upload, String.valueOf(attachmentNo)); // 저장할 파일의 인스턴스(아직없음)
		attach.transferTo(target); // 저장하세요!
		
		// DB에 저장된 파일의 정보를 기록
		AttachmentDto attachmentDto = new AttachmentDto();
		attachmentDto.setAttachmentNo(attachmentNo); // 고유번호
		attachmentDto.setAttachmentName(attach.getOriginalFilename()); // 파일이름
		attachmentDto.setAttachmentType(attach.getContentType()); // 파일유형
		attachmentDto.setAttachmentSize(attach.getSize()); // 파일크기
		
		// productNo, reviewNo는 아직 설정되지 않은 상태로 insert합니다.
		attachmentDao.insert(attachmentDto);
		
		return attachmentNo; // 생성한 파일의 번호를 반환
	}

	/**
	 * 파일 내용 불러오기
	 */
	public ByteArrayResource load(int attachmentNo) throws IOException {
		// 파일을 찾는다
		File target = new File(upload, String.valueOf(attachmentNo));
		if(target.isFile() == false) throw new TargetNotfoundException("존재하지 않는 파일");
		
		// 파일의 내용을 읽어온다
		byte[] data = Files.readAllBytes(target.toPath());
		ByteArrayResource resource = new ByteArrayResource(data);
		
		return resource;
	}
	
	/**
	 * 파일 삭제 (DB 삭제 및 실물 파일 삭제)
	 * - 삭제 시 로그를 기록합니다.
	 */
	@Transactional
	public void delete(int attachmentNo) {
		AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
		if(attachmentDto == null) {
		    log.warn("삭제하려는 첨부 파일 번호({})가 DB에 존재하지 않습니다.", attachmentNo);
		    throw new TargetNotfoundException("존재하지 않는 파일");
		}
		
		// 1. 실제 파일 삭제
		File target = new File(upload, String.valueOf(attachmentNo));
		boolean physicalDeleted = target.delete();
		
		if (physicalDeleted) {
		    log.info("첨부 파일({})의 물리적 파일({}) 삭제 성공.", attachmentNo, target.getAbsolutePath());
		} else {
		    // 파일이 원래 존재하지 않았거나(정상), 삭제 권한 문제(오류)일 수 있음
		    log.warn("첨부 파일({})의 물리적 파일({}) 삭제 실패 또는 파일이 이미 존재하지 않음.", attachmentNo, target.getAbsolutePath());
		}
	
		// 2. DB 정보 삭제
		attachmentDao.delete(attachmentNo);
        log.info("첨부 파일({}) DB 레코드 삭제 완료.", attachmentNo);
	}

	/**
	 * 리뷰 등록 후, 저장된 첨부파일에 reviewNo를 연결합니다. (ReviewService에서 호출)
	 */
	public boolean updateReviewNo(int attachmentNo, int reviewNo) {
		return attachmentDao.updateReviewNo(attachmentNo, reviewNo);
	}
	
	/**
	 * 특정 리뷰 번호에 연결된 모든 첨부파일의 고유 번호(attachmentNo) 목록을 조회합니다. (ReviewService에서 호출)
	 */
	public List<Integer> selectAttachmentNosByReviewNo(int reviewNo) {
		return attachmentDao.selectAttachmentNosByReviewNo(reviewNo);
	}
}
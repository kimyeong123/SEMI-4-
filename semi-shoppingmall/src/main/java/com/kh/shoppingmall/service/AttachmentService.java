package com.kh.shoppingmall.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.AttachmentDao;
import com.kh.shoppingmall.dto.AttachmentDto;
import com.kh.shoppingmall.error.TargetNotfoundException;

//서비스란?
//- 각종 도구들을 주입하여 원하는 거대한 목표를 달성하기 위한 복잡한 코드를 메소드로 가지는 도구
//- (ex) 상품구매 = 회원포인트검사/차감 + 상품재고검사/차감 + 구매이력기록
@Service
public class AttachmentService {
	//DB 처리를 위한 도구를 주입
	@Autowired
	private AttachmentDao attachmentDao;
	
	//파일 저장을 위한 경로 설정
	private File home = new File(System.getProperty("user.home"));
	private File upload = new File(home, "upload");
	
	//파일 저장 (실물 저장 + DB 저장)
	@Transactional//이 메소드를 하나의 트랜잭션으로 간주하겠다! (이 안에서 실행된 DB작업은 일괄처리/취소)
	public int save(MultipartFile attach) throws IllegalStateException, IOException {
		int attachmentNo = attachmentDao.sequence();//번호를 미리 생성
		
		//파일을 저장하려면 파일 인스턴스가 필요
		if(upload.exists() == false) {//업로드할 폴더가 존재하지 않는다면
			upload.mkdirs();//생성하세요!
		}
		File target = new File(upload, String.valueOf(attachmentNo));//저장할 파일의 인스턴스(아직없음)
		attach.transferTo(target);//저장하세요!
		
		//DB에 저장된 파일의 정보를 기록
		AttachmentDto attachmentDto = new AttachmentDto();
		attachmentDto.setAttachmentNo(attachmentNo);//고유번호
		attachmentDto.setAttachmentName(attach.getOriginalFilename());//파일이름
		attachmentDto.setAttachmentType(attach.getContentType());//파일유형
		attachmentDto.setAttachmentSize(attach.getSize());//파일크기
		
		attachmentDao.insert(attachmentDto);
		
		return attachmentNo;//생성한 파일의 번호를 반환
	}

	//파일 내용 불러오기
	public ByteArrayResource load(int attachmentNo) throws IOException {
		//파일을 찾는다
		File target = new File(upload, String.valueOf(attachmentNo));
		if(target.isFile() == false) throw new TargetNotfoundException("존재하지 않는 파일");
		
		//파일의 내용을 읽어온다
		byte[] data = Files.readAllBytes(target.toPath());//한번에 다 읽기(java.nio 패키지의 명령)
		ByteArrayResource resource = new ByteArrayResource(data);//포장
		
		return resource;
	}
	
	//파일 삭제 (DB 삭제 및 실물 파일 삭제)
	//- 없는 첨부파일 번호라면 예외처리
	public void delete(int attachmentNo) {
		AttachmentDto attachmentDto = attachmentDao.selectOne(attachmentNo);
		if(attachmentDto == null) throw new TargetNotfoundException("존재하지 않는 파일");
		
		//실제 파일 삭제
		File target = new File(upload, String.valueOf(attachmentNo));
		boolean deleted = target.delete();
		if(!deleted) {
		    // 로그 남기기 등 필요시 처리
		}
	
		//DB 정보 삭제
		attachmentDao.delete(attachmentNo);
	}
	
	
}








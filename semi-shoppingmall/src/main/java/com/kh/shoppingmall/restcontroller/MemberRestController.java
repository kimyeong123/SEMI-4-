package com.kh.shoppingmall.restcontroller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.MemberDao;
import com.kh.shoppingmall.dto.MemberDto;
import com.kh.shoppingmall.service.AttachmentService;
import com.kh.shoppingmall.dto.CertDto;
import com.kh.shoppingmall.service.EmailService;
import com.kh.shoppingmall.dao.CertDao;

import jakarta.servlet.http.HttpSession;

@CrossOrigin
@RestController
@RequestMapping("/rest/member")
public class MemberRestController {
	@Autowired
	private MemberDao memberDao;
	@ Autowired
	private AttachmentService attachmentService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private CertDao certDao;

	//인증 및 이메일은 아직 구현이 안된 관계로 뺌
	
	@GetMapping("/checkMemberId")
	public boolean checkMemberId(@RequestParam String memberId) {
		MemberDto memberDto = memberDao.selectOne(memberId);
		return memberDto != null;
	}
	
	@GetMapping("/checkMemberNickname")
	public boolean checkMemberNickname(@RequestParam String memberNickname) {
		MemberDto memberDto = memberDao.selectOneByMemberNickname(memberNickname);
		return memberDto != null;
	}
	
	@PostMapping("/profile")
	public void profile(HttpSession session, @RequestParam MultipartFile attach) throws IllegalStateException, IOException {
		String loginId = (String)session.getAttribute("loginId");
		
		//기존 파일 삭제 (없을수도 있음)
		try {
			int attachmentNo = memberDao.findAttachment(loginId);
			attachmentService.delete(attachmentNo);
		} catch(Exception e) {}
		
		//신규파일 등록
		if(attach.isEmpty() == false) {
			int attachmentNo = attachmentService.save(attach);
			memberDao.connect(loginId, attachmentNo);
		}
	}
	
	@PostMapping("/delete")
	public void delete(HttpSession session) {
		String loginId = (String)session.getAttribute("loginId");
		
		//기존 파일 삭제 (없을수도 있음)
		try {
			int attachmentNo = memberDao.findAttachment(loginId);
			attachmentService.delete(attachmentNo);
		} catch(Exception e) {}
		
	}
	
	//이후는 아마 인증관련 내용이 들어갈 것

	@PostMapping("/certSend")
	public void certSend(@RequestParam String certEmail) {
		emailService.sendCertNumber(certEmail);
	}
	
	@PostMapping("/certCheck")
	public boolean certCheck(@ModelAttribute CertDto certDto) {
		//메일 보냄 검사
		CertDto findDto = certDao.selectOne(certDto.getCertEmail());
		if(findDto == null) return false;
		
		//인증시간 검사
		LocalDateTime current = LocalDateTime.now();
		LocalDateTime sent = findDto.getCertTime().toLocalDateTime();
		Duration duration = Duration.between(sent, current);
//		if(duration.toMinutes() > 10) return false; //10분 초과 (10분 59초)
		if(duration.toSeconds() > 600) return false; //10분 초과 10분 0초 초과
		
		
		//인증번호 검사
		boolean isValid = certDto.getCertNumber().equals(findDto.getCertNumber());
		if(isValid == false) return false;
				
		//인증 성공 후 인증내역 삭제
		certDao.delete(certDto.getCertEmail());
		//인증 통과
		return true;
	}
	
}





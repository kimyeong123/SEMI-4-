package com.kh.shoppingmall.restcontroller;

import com.kh.shoppingmall.error.TargetNotfoundException;
import com.kh.shoppingmall.error.UnauthorizationException;

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

import com.kh.shoppingmall.dao.CertDao;
import com.kh.shoppingmall.dao.MemberDao;
import com.kh.shoppingmall.dto.CertDto;
import com.kh.shoppingmall.dto.MemberDto;
import com.kh.shoppingmall.service.AttachmentService;
import com.kh.shoppingmall.service.EmailService;

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
	
	//세션 정보에 따른 멤버 레벨 찾기
	@GetMapping("/checkMemberLevel")
	public String checkMemberLevel(@RequestParam String memberId) {
		MemberDto memberDto = memberDao.selectOne(memberId);
		String memberLevel = memberDto.getMemberLevel();
		return memberLevel;
	}
	
	@GetMapping("/checkMemberNickname")
	public boolean checkMemberNickname(@RequestParam String memberNickname) {
		MemberDto memberDto = memberDao.selectOneByMemberNickname(memberNickname);
		return memberDto != null;
	}
	
	@PostMapping("/profile")
    public boolean updateProfileImage(HttpSession session, @RequestParam MultipartFile attach) throws IllegalStateException, IOException {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }

        // 새 파일이 있는지 확인
        if (attach == null || attach.isEmpty()) {
             throw new TargetNotfoundException("프로필 이미지 파일이 없습니다.");
        }

        Integer oldAttachmentNo = null; // 기존 번호 저장용
        Integer newAttachmentNo = null; // 새 번호 저장용

        try {
            //기존 프로필 이미지 번호 조회
            oldAttachmentNo = memberDao.findAttachment(memberId); // 수정된 DAO 메소드 (findProfileImageNo 권장)

            //새 파일 업로드
            newAttachmentNo = attachmentService.save(attach);

            // 3. Member 테이블 업데이트 (새 번호로)
            boolean updated = memberDao.updateProfileImage(memberId, newAttachmentNo);
            if (!updated) {
                 throw new RuntimeException("회원 정보 업데이트 실패");
            }

            // 4. 기존 프로필 이미지 삭제 (새 이미지 등록 성공 후, 기존 이미지가 있었을 경우)
            if (oldAttachmentNo != null && oldAttachmentNo > 0) {
                 attachmentService.delete(oldAttachmentNo);
            }

            return true; // 성공

        } catch (Exception e) {
            if (newAttachmentNo != null) {
                try {
                    attachmentService.delete(newAttachmentNo);
                } catch (Exception deleteEx) {
                }
            }
            // 예외 다시 던지기 (ControllerAdvice에서 처리하도록)
            if (e instanceof UnauthorizationException || e instanceof TargetNotfoundException) {
                throw e;
            } else {
                throw new RuntimeException("프로필 업데이트 중 오류가 발생했습니다.", e);
            }
        }
    }
	
	@PostMapping("/delete")
	public boolean deleteProfileImage(HttpSession session) {
        String memberId = (String) session.getAttribute("loginId");
        if (memberId == null) {
            throw new UnauthorizationException("로그인이 필요합니다.");
        }

        Integer oldAttachmentNo = null;
        try {
            //기존 프로필 이미지 번호 조회
            oldAttachmentNo = memberDao.findAttachment(memberId); // 수정된 DAO 메소드

            //기존 이미지가 있을 경우에만 처리
            if (oldAttachmentNo != null && oldAttachmentNo > 0) {
                //Member 테이블 업데이트
                boolean updated = memberDao.updateProfileImage(memberId, 0);
                 if (!updated) {
                     throw new RuntimeException("회원 정보 업데이트 실패 (프로필 null 설정)");
                 }

                //실제 파일 및 DB 레코드 삭제
                attachmentService.delete(oldAttachmentNo);
            }
            // else: 원래 프로필 없었으면 아무것도 안 함

            return true; // 성공

        } catch (Exception e) {
             if (e instanceof UnauthorizationException || e instanceof TargetNotfoundException) {
                throw e;
            } else {
                throw new RuntimeException("프로필 삭제 중 오류가 발생했습니다.", e);
            }
        }
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





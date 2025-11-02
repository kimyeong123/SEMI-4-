package com.kh.shoppingmall.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.shoppingmall.dao.CartDao;
import com.kh.shoppingmall.dao.CsBoardDao;
import com.kh.shoppingmall.dao.MemberDao;
import com.kh.shoppingmall.dao.OrdersDao;
import com.kh.shoppingmall.dao.ReviewDao;
import com.kh.shoppingmall.dao.WishlistDao;
import com.kh.shoppingmall.dto.MemberDto;
import com.kh.shoppingmall.error.TargetNotfoundException;

@Service
public class MemberService {
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private WishlistDao wishlistDao;
	@Autowired
	private OrdersDao ordersDao;
	@Autowired
	private CartDao cartDao;
	@Autowired
	private ReviewDao reviewDao;
	@Autowired
	private CsBoardDao csBoardDao;
	
	@Autowired
	private AttachmentService attachmentService;
	
	@Transactional
	public boolean drop(String memberId, String memberPw) {
		//1. 아이디에 맞는 회원 검색
		MemberDto memberDto = memberDao.selectOne(memberId);
		if(memberDto == null) throw new TargetNotfoundException("존재하지 않는 회원");
		
		//2. 비번이 회원과 동일한지 확인
		boolean isValid = memberDto.getMemberPw().equals(memberPw);
		if(isValid == false) return false;
		
		//3. 회원 사진 삭제
		Integer attachmentNo = memberDao.findAttachment(memberId); // Integer로 받음
		if (attachmentNo != null) { // null이 아닐 때만 삭제 시도
		    attachmentService.delete(attachmentNo);
		}

		//4-1. 주문 기록 비식별화
		ordersDao.clearMemberId(memberId);
		
		//4-2.위시리스트 데이터 삭제
		wishlistDao.deleteByMemberId(memberId);

		//4-3. 장바구니 데이터 삭제 
		cartDao.deleteByMemberId(memberId);
		
		//4-4. 리뷰 기록 삭제
		reviewDao.deleteByMemberId(memberId);
		
		//4-5. 작성글 목록의 이름 삭제된 아이디로 수정
//		csBoardDao.updateNotMember(memberId);
		
		//5. 회원 삭제
		boolean isDeleted = memberDao.delete(memberId);
		if(isDeleted == false) return false;

		return true;
	}
	
	@Transactional // DB 작업이 두 개 이상이므로 트랜잭션 처리
	public boolean updateProfile(MemberDto memberDto, MultipartFile newProfile) throws IllegalStateException, IOException {
	    // 1. 이미지 처리: 기존 이미지 삭제 및 새 이미지 연결
		updateProfileImage(memberDto.getMemberId(), newProfile);
		
	    // 2. 회원 정보 처리: DAO를 통해 METER 테이블의 나머지 정보 업데이트
	    // ----------------------------------------------------------------------------------
	    // 회원 정보 업데이트 (닉네임, 연락처, 주소 등)
	    // DAO 메소드 호출: memberDao.updateMember 또는 memberDao.updateMemberByAdmin 사용
	    boolean result = memberDao.updateMember(memberDto); 
	    // ----------------------------------------------------------------------------------
	    
	    return result; 
	}
	
	
	@Transactional
	public boolean updateProfileByAdmin(MemberDto memberDto, MultipartFile newProfile) throws IllegalStateException, IOException {
	    // 1. 이미지 처리: 기존 이미지 삭제 및 새 이미지 연결 (일반 로직과 동일)
		updateProfileImage(memberDto.getMemberId(), newProfile);
	    
	    // 2. 회원 정보 처리: 관리자용 DAO 메소드 호출
	    // memberDao에 있는 updateMemberByAdmin(MemberDto)를 호출
	    boolean result = memberDao.updateMemberByAdmin(memberDto); 
	    
	    return result; 
	}
	
	
	@Transactional
	public void deleteProfileImage(String memberId) {
	    Integer attachmentNo = memberDao.findAttachment(memberId);
	    if (attachmentNo != null) {
	        // 이 호출이 ATTACHMENT와 MEMBER_PROFILE을 정리합니다.
	        attachmentService.delete(attachmentNo);
	    }
	}
	
	@Transactional
	public boolean updateProfileImage(String memberId, MultipartFile newProfile) throws IllegalStateException, IOException {
	    // 1. 기존 이미지 삭제 (memberId만 사용)
	    Integer oldAttachmentNo = memberDao.findAttachment(memberId);
	    if (oldAttachmentNo != null) {
	        attachmentService.delete(oldAttachmentNo);
	    }
	    
	    // 2. 새 이미지 저장 및 연결 (memberId만 사용)
	    if (newProfile != null && !newProfile.isEmpty()) {
	        int newAttachmentNo = attachmentService.save(newProfile);
	        // ⭐ [수정] 이제 DAO에 존재하는 이 메소드를 호출하여 MEMBER 테이블을 업데이트
	        boolean updated = memberDao.updateProfileImage(memberId, newAttachmentNo);
	        	        
	        if (!updated) {
	            throw new RuntimeException("새 프로필 번호 연결(업데이트) 실패"); 
	        }
	    }
	    // 회원 정보(MemberDto)는 여기서 건드리지 않습니다.
	    return true; 
	}
	
	//기본배송지 설정시 업데이트
	public boolean updateMemberAddress(MemberDto memberDto) {
	    // DAO 메소드를 호출하여 DB 업데이트 수행
	    return memberDao.updateMemberAddress(memberDto);
	}
	
	//멤버 삭제 이벤트
	@Transactional
	public boolean deleteMember(String memberId) {
		
		csBoardDao.updateNotMember(memberId);
		boolean result = memberDao.delete(memberId);
		
		return result;
		
	}
	
	
	@Transactional
	public boolean adminDelete(String memberId) {
		
		//3. 회원 사진 삭제
		Integer attachmentNo = memberDao.findAttachment(memberId); // Integer로 받음
		if (attachmentNo != null) { // null이 아닐 때만 삭제 시도
		    attachmentService.delete(attachmentNo);
		}
		
		//4-1. 주문 기록 비식별화
		ordersDao.clearMemberId(memberId);
		
		//4-2.위시리스트 데이터 삭제
		wishlistDao.deleteByMemberId(memberId);

		//4-3. 장바구니 데이터 삭제 
		cartDao.deleteByMemberId(memberId);
		
		//4-4. 리뷰 기록 삭제
		reviewDao.deleteByMemberId(memberId);
		
		//4-5. 작성글 목록의 이름 삭제된 아이디로 수정
//		csBoardDao.updateNotMember(memberId);
		
		//5. 회원 삭제
		boolean isDeleted = memberDao.delete(memberId);
		if(isDeleted == false) return false;

		return true;
		
	}
	
	
}

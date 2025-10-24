package com.kh.shoppingmall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.shoppingmall.dao.CartDao;
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
		
		//5. 회원 삭제
		boolean isDeleted = memberDao.delete(memberId);
		if(isDeleted == false) return false;

		return true;
	}
	
}

package com.kh.shoppingmall.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.shoppingmall.error.NeedPermissionException;
import com.kh.shoppingmall.error.TargetNotfoundException;
//추후 문의글 작성 관련 추가
//import com.kh.shoppingmall.dao.BoardDao;
//import com.kh.shoppingmall.dto.BoardDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//목표 : 본인 글에만 접근할 수 있도록 처리(+삭제는 관리자도 접근 가능)
//@Service
//public class BoardOwnerInterceptor implements HandlerInterceptor{
//
//	@Autowired
//	private BoardDao boardDao;
//	
//	@Override
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//			throws Exception {
//		HttpSession session = request.getSession();
//		String loginId = (String) session.getAttribute("loginId");
//		String loginLevel = (String) session.getAttribute("loginLevel");
//		
//		
//		//[1] 관리자는 삭제 페이지로 가는 경우 통과
//		String uri = request.getRequestURI();
//		
//		if(loginLevel.equals("관리자") && uri.equals("/board/delete"))
//			return true;
//		
//		//[2] 자기자신이 작성한 글이 아니라면 차단
//		int boardNo = Integer.parseInt(request.getParameter("boardNo"));
//		BoardDto boardDto = boardDao.selectOne(boardNo);
//		
//		if(boardDto == null)
//			throw new TargetNotfoundException("존재하지 않는 게시글");
//		
//		if(loginId.equals(boardDto.getBoardWriter()) == false)
//			throw new NeedPermissionException("본인 게시물만 접근 가능합니다");
//		
//		return true;
//	}
	
//}







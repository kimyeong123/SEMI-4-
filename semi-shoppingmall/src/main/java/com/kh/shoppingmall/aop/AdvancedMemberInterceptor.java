package com.kh.shoppingmall.aop;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.shoppingmall.error.NeedPermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class AdvancedMemberInterceptor implements HandlerInterceptor{
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession();
		
		String loginId = (String)session.getAttribute("loginId");
		String loginLevel = (String)session.getAttribute("loginLevel");
		
		//if(loginId == null ||loginLevel == null) throw new UnAuthorizationException("로그인이 필요합니다");
		
		if(loginLevel.equals("일반회원")) throw new NeedPermissionException("권한이 부족합니다");
		
		return true;
	}
}

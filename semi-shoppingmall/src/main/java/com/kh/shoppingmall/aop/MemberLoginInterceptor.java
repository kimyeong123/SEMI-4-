package com.kh.shoppingmall.aop;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.shoppingmall.error.UnauthorizationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class MemberLoginInterceptor implements HandlerInterceptor{
	
	@Override
	public boolean preHandle(HttpServletRequest request, //요청(사용자의 정보)
			HttpServletResponse response, //응답(사용자에게 나갈 정보)
			Object handler)
			throws Exception {
		
		//사용자의 정보를 조회하고 싶을 때(ex: 파라미터, 세션, 쿠키 ...)는 HttpServletRequest 사용
		//사용자의 결과화면을 제어하고 싶을 때(ex: 에러처리, 리다이렉트)는 HttpServletResponse 사용
		//=정보는 똑같이 있는데 Spring처럼 달라는대로 주진 않는다(일반적인 Java EE의 방식)
		HttpSession session = request.getSession();
		
		String loginId = (String)session.getAttribute("loginId");
		boolean isMember = loginId != null;
		
		if(isMember) {
			return true;
		}
		else {
			//1. 로그인 페이지로 리다이렉트 - 화면을 JSP로 만들 때 유용
			//response.sendRedirect("/member/login");
			//return false;
			
			//2. HTTP 표준 상태번호를 전송 - 화면을 다른 기술로 구현할 때 유용(ex:react)
			//response.sendError(HttpStatus.UNAUTHORIZED.value());
			//response.sendError(401);
			
			//3. 예외 처리 (가장 권장하는 방식) - 예외 클래스 만들어야함
			throw new UnauthorizationException("로그인이 필요합니다");
		}
	}
}






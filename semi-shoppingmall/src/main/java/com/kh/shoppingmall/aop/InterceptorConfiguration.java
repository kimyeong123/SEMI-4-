package com.kh.shoppingmall.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//application.properties에서 할 수 없는 프로그래밍 설정을 수행하는 파일
//@Configuration //설정파일로 등록
public class InterceptorConfiguration implements WebMvcConfigurer{ //웹 기본 동작 설정을 위한
	
	@Autowired
	private MemberLoginInterceptor memberLoginInterceptor;
	@Autowired
	private AdvancedMemberInterceptor advancedMemberInterceptor;
	@Autowired
	private AdminInterceptor adminInterceptor;
	@Autowired
	private PreventAdminInterceptor preventAdminInterceptor;
	
	
	//설정하고 싶은 메소드를 재정의
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(인터셉터객체).addPathPatterns(적용시킬주소);
//		registry.addInterceptor(testInterceptor).addPathPatterns("/**");
		
		//스프링에서는 주소 패턴을 위해 다음과 같은 기호를 제공
		//*를 이용해서 지정한 대상의 엔드포인트 내의 모든 항목을 지정
		//**를 이용해서 지정한 대상과 하위 모든 항목을 지정
		registry.addInterceptor(memberLoginInterceptor)
		.addPathPatterns(
				
				)
		.excludePathPatterns(
				
				).order(1);
		
		//우수회원 검사용 인터셉터
		registry.addInterceptor(advancedMemberInterceptor)
		.addPathPatterns("").order(2);
		
		//관리자 검사용 인터셉터
		registry.addInterceptor(adminInterceptor)
		.addPathPatterns("").order(3);
		
		//관리자 제품 상세/수정/삭제 금지 인터셉터 등록
		registry.addInterceptor(preventAdminInterceptor)
		.addPathPatterns("/admin/member/detail", "/admin/member/edit","/admin/member/drop")
		.order(4);
		
		//(추후) 문의글 (수정, 삭제), 관리자(삭제)_ 인터셉터 등록
//		registry.addInterceptor(boardOwnerInterceptor)
//		.addPathPatterns("/board/edit", "/board/delete")
//		.order(5);
		
	}

}





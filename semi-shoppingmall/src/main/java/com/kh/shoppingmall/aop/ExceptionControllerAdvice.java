package com.kh.shoppingmall.aop;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.kh.shoppingmall.error.TargetNotfoundException;

import com.kh.shoppingmall.error.NeedPermissionException;
import com.kh.shoppingmall.error.UnauthorizationException;


@ControllerAdvice
public class ExceptionControllerAdvice {
	
	//catch 블럭처럼 사용할 수 있는 도구
	//catch(TargetNotfountException e) {}
//	@ExceptionHandler(TargetNotfoundException.class)
	@ExceptionHandler(value = {TargetNotfoundException.class, NoResourceFoundException.class})
	public String notFound(Exception e, Model model) {
		//컨트롤러에서 작성 가능한 코드라면 뭐든지 가능
		model.addAttribute("title", e.getMessage()); //e.getMessage를 title로
		return "/WEB-INF/views/error/notFound.jsp";
	}
	
	@ExceptionHandler(UnauthorizationException.class)
	public String unauthorize(UnauthorizationException e, Model model) {
		//컨트롤러에서 작성 가능한 코드라면 뭐든지 가능
		model.addAttribute("title", e.getMessage()); //e.getMessage를 title로
		return "/WEB-INF/views/error/unauthorize.jsp";
	}
	
	@ExceptionHandler(NeedPermissionException.class)
	public String needPermission(NeedPermissionException e, Model model) {
		model.addAttribute("title", e.getMessage());
		return "/WEB-INF/views/error/needPermission.jsp";
	}
	
	//나머지 모든 예외
	// - 사용자에게는 별거 아닌 것처럼, 개발자에게는 아주 상세한 정보를 남긴다
	@ExceptionHandler(Exception.class)
	public String all(Exception e) {
		e.printStackTrace();
		return "/WEB-INF//views/error/all.jsp";
	}
}

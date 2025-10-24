<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri= "http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>	
 
 <div class = "container w-600">
	 <div class = "cell center">
		<h1>회원 탈퇴</h1>
	 </div>
	 <div class ="cell center">
		<h2>회원 탈퇴를 하려면 비밀번호를 다시 입력하세요</h2>
	 </div>
	 <div>
		<a class = "btn btn-netural" href = "/member/mypage">마이페이지로 다시가기</a>
	 </div>
	<form action = "drop" method = "post">    
		<div class ="cell">
			<label>비밀번호 입력 <span class = "red">*</span></label>
			<input class ="field w-100" type = "password" name = "memberPw" required >
		</div>
		<button class ="btn btn-negative">탈퇴하기</button>
	</form>
	
	<c:if test = "${param.error != null}">
		<div class ="cell">
			<h2 class = "red">비밀번호가 틀렸습니다</h2>
		</div>
	</c:if>
 </div>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>	
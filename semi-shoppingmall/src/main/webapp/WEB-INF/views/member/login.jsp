<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri= "http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<script type="text/javascript">
	$(function(){
	    $("#password-show").on("click", function(){
	        if($(this).hasClass("fa-eye-slash")) {//type=password 상태라면
	            $(this).removeClass("fa-eye-slash").addClass("fa-eye");
	            $("[name=memberPw], #password-check").prop("type", "text");
	        }
	        else {//type=text 상태라면
	            $(this).removeClass("fa-eye").addClass("fa-eye-slash");
	            $("[name=memberPw], #password-check").prop("type", "password");
	        }
	    });
	});
	
</script>


<div class="container w-400">
	<div class="cell center">
		<h1>로그인</h1>
	</div>
	<form action = "login" method = post>
		<div class="cell">
			<label>아이디  <i class="fa-solid fa-asterisk red"></i></label>
			<input class="field w-100" type="text" name="memberId" required autocomplete="off">
		</div>
		<div class="cell">
			<label>
				비밀번호
				<i class="fa-solid fa-asterisk red"></i>
				<i class="fa-solid fa-eye-slash" id="password-show"></i>
			</label>
			<input class="field w-100" type="password" name="memberPw" required autocomplete="off">
		</div>
		<button class="feild w-100 btn btn-positive mt-20 mb-30">로그인</button>
		
		<%--아이디 및 비번 찾기 --%>
		<div class="cell center mt-30">
			<a href="findMemberId">아이디가 기억나지 않아요</a>
		</div>
		<div class="cell center">
			<a href="findMemberPw">비밀번호가 기억나지 않아요</a>
		</div>
		 
		
	</form>
	<%-- error라는 파라미터가 존재한다면 오류메시지 출력  --%>
	<c:if test = "${param.error != null}">
		<h2 class="center" style = "color:red ">입력하신 정보가 일치하지 않습니다</h2>
	</c:if>
</div>


<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
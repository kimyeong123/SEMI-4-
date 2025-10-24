<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<form action = "findMemberPw" method = "post" autocomplete="off">
	<div class = "container w-500">
		<div class="cell center">
			<h1>비밀번호 찾기</h1>
		</div>
		<div class="cell">
			<label>아이디</label> <br>
			<input type="text" name="memberId" class="field w-100"> <br>
		<div class="cell">
			<label>닉네임<i class="fa-solid fa-asterisk red"></i></label> <br>
			<input type = "text" name="memberNickname" class="field w-100"> <br>
		</div>
		<div class="cell">
			<label>이메일<i class="fa-solid fa-asterisk red"></i></label> <br>
			<input type = "text" name="memberEmail" class="field w-100"> <br>
		</div>
		<div class="cell mt-30">
			<button class="btn btn-negative w-100" type="submit">
				<i class="fa-solid fa-lock"></i>
				<span>변경하기</span>
			</button>
		</div>
		
		</div>
	</div>
</form>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
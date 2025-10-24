<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<form action = "changeMemberPw" method = "post" autocomplete="off">
	<input type="hidden" name="memberId" value = "${memberId}">
	<input type="hidden" name="certNumber" value = "${certNumber}">
	<div class = "container w-500">
		<div class="cell center">
			<h1>비밀번호 재설정</h1>
		</div>
		<div class="cell">
			<label>비밀번호<i class="fa-solid fa-asterisk red"></i></label> <br>
			<input type = "password" name="memberPw" class="field w-100"> <br>
		</div>
		<div class="cell">
			<label>비밀번호 확인<i class="fa-solid fa-asterisk red"></i></label> <br>
			<input type = "password" id="check-password" class="field w-100"> <br>
		</div>		
		<div class="cell mt-30">
			<button class="btn btn-negative w-100" type="submit">
				<i class="fa-solid fa-lock"></i>
				<span>변경하기</span>
			</button>
		</div>
	</div>
</form>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
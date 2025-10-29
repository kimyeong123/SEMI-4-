<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<div class = "container w-600">
	<div class ="cell center">
		<h1>비밀번호 수정</h1>
	</div>
	<div class = "cell">
		<h2>기존 비밀번호와 수정할 비밀번호를 입력해주세요</h2>
	</div>
	<div class ="cell">
		<a class ="btn btn-netural" href="/member/mypage">마이페이지로 다시가기</a>
	</div>
	<form action="password" method="post">
		<div class = cell>
			<label>기존 비밀번호 입력</label>
			<input class ="field w-100" type="password" name="currentPw" required>
		</div>
		<div class = "cell">
			<label>새 비밀번호 입력</label>
			<input class ="field w-100" type="password" name = "changePw"  required>
		</div>
		<div clas = "cell">
			<button class ="btn btn-negative" type="submit">비밀번호 변경</button>
		</div>
	</form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

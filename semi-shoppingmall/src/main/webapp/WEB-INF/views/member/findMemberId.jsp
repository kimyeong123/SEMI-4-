<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<form action="findMemberId" method="post" autocomplete="off">
<div class="container w-500">
	<div class="cell center">
		<h1>아이디 찾기</h1>
	</div>	
	<div class="cell">
		<label>닉네임 <i class="fa-solid fa-asterisk red"></i></label>
		<input type="text" name="memberNickname" class="field w-100">
	</div>
	<div class="cell">
		<label>이메일 <i class="fa-solid fa-asterisk red"></i></label>
		<input type="text" name="memberEmail" class="field w-100">
	</div>
	<div class="cell mt-30">
		<button type="submit" class="btn btn-positive w-100">
			<i class="fa-solid fa-magnifying-glass"></i>
			<span>찾기</span>
		</button>
	</div>
	
	<c:if test="${param.error != null}">
	<div class="cell">
		<h3 class="red">입력하신 정보에 대한 결과가 없습니다</h3>
	</div>
	</c:if>
	
</div>

</form>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
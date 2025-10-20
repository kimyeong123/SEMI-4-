<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    

<!-- 기존 메뉴(한줄짜리) -->
<div class="menu">
            
<%-- 템플릿 페이지에서 작성하는 경로는 반드시 절대경로여야 한다 --%>
<%-- 로그인 여부에 따라 다른 메뉴들을 표시 --%>
<c:choose>
	<c:when test="${sessionScope.loginId != null && sessionScope.loginLevel == '일반회원'}">
		<a href="/">
			<i class="fa-solid fa-house"></i>
			<span>홈</span>
		</a>
		<a href="/pokemon/list">
                  <i class="fa-solid fa-ghost"></i>
			<span>포켓몬</span>
		</a>
		<a href="/student/list">
			<i class="fa-solid fa-graduation-cap"></i>
			<span>학생정보</span>
		</a>
		<a href="/board/list">
			<i class="fa-solid fa-comments"></i>
			<span>게시판</span>
		</a>
		
		<div class="divider"></div>
		
		<a href="/giftcard/list">
			<i class="fa-solid fa-sack-dollar"></i>
			<span>충전</span>	
		</a>
		<a href="/member/mypage">
			<i class="fa-solid fa-user"></i>
			<span>내정보</span>
		</a>
		<a href="/member/logout">
			<i class="fa-solid fa-right-from-bracket"></i>
			<span>로그아웃</span>
		</a>
	</c:when>
	<c:when test="${sessionScope.loginId != null && sessionScope.loginLevel == '우수회원'}">
		<a href="/">
			<i class="fa-solid fa-house"></i>
			<span>홈</span>
		</a>
		<a href="/pokemon/list">
                  <i class="fa-solid fa-ghost"></i>
			<span>포켓몬</span>
		</a>
		<a href="/student/list">
			<i class="fa-solid fa-graduation-cap"></i>
			<span>학생정보</span>
		</a>
		<a href="/book/list">
			<i class="fa-solid fa-book"></i>
			<span>도서정보</span>
		</a>
		<a href="/board/list">
			<i class="fa-solid fa-comments"></i>
			<span>게시판</span>
		</a>
		
		<div class="divider"></div>
		
		<a href="/giftcard/list">
			<i class="fa-solid fa-sack-dollar"></i>
			<span>충전</span>	
		</a>
		<a href="/member/mypage">
			<i class="fa-solid fa-user"></i>
			<span>내정보</span>
		</a>
		<a href="/member/logout">
			<i class="fa-solid fa-right-from-bracket"></i>
			<span>로그아웃</span>
		</a>
	</c:when>
	<c:when test="${sessionScope.loginId != null && sessionScope.loginLevel == '관리자'}">
		<a href="/">
			<i class="fa-solid fa-house"></i>
			<span>홈</span>
		</a>
		<a href="/pokemon/list">
                  <i class="fa-solid fa-ghost"></i>
			<span>포켓몬</span>
		</a>
		<a href="/student/list">
			<i class="fa-solid fa-graduation-cap"></i>
			<span>학생정보</span>
		</a>
		<a href="/book/list">
			<i class="fa-solid fa-book"></i>
			<span>도서정보</span>
		</a>
		<a href="/board/list">
			<i class="fa-solid fa-comments"></i>
			<span>게시판</span>
		</a>
		
		<div class="divider"></div>
		
		<a href="/admin/home" class="red">
			<i class="fa-solid fa-wrench"></i>
			<span>관리메뉴</span>
		</a>
		<a href="/member/logout">
			<i class="fa-solid fa-right-from-bracket"></i>
			<span>로그아웃</span>
		</a>
	</c:when>
    <c:otherwise>
   		<a href="/">
			<i class="fa-solid fa-house"></i>
			<span>홈</span>
		</a>
		<a href="/pokemon/list">
                  <i class="fa-solid fa-ghost"></i>
			<span>포켓몬</span>
		</a>
		<a href="/board/list">
			<i class="fa-solid fa-comments"></i>
			<span>게시판</span>
		</a>

		<div class="divider"></div>
						
		<a href="/giftcard/list">
			<i class="fa-solid fa-sack-dollar"></i>
			<span>충전</span>	
		</a>
		<a href="/member/login">
			<i class="fa-solid fa-right-to-bracket"></i>
			<span>로그인</span>
		</a>
		<a href="/member/join">
			<i class="fa-solid fa-user-plus"></i>
			<span>회원가입</span>
		</a>
		</c:otherwise>
	</c:choose>
</div>
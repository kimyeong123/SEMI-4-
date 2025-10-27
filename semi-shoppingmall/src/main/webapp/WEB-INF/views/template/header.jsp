<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="/css/commons.css">
<link rel="stylesheet" type="text/css"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css">
<style>
/* ... (기존 style 태그 내용 유지) ... */
.container {
    width: 90%; 
    max-width: 1280px; 
    margin-left: auto;
    margin-right: auto;
}
.image-profile {
	border-radius: 50%;
	box-shadow: 0 0 3px 1px #636e72;
	opacity: 0.95;
	transition-property: opacity, box-shadow;
	transition-duration: 0.1s;
	transition-timing-function: ease-out;
}

.image-profile:hover {
	opacity: 1;
}
/* 2. 헤더 오른쪽 메뉴 디자인

/* 헤더 오른쪽 메뉴 컨테이너 정렬 및 간격 */
.header-menu-right {
    display: flex; /* 메뉴 항목들을 Flexbox로 처리 */
    align-items: center;
    justify-content: flex-end; /* 오른쪽 끝으로 정렬 */
    gap: 8px; /* 메뉴 항목 사이의 간격 */
    height: 100%;
    white-space: nowrap;
}

/* 모든 메뉴 링크에 공통 스타일 적용 */
.header-menu-right a {
    text-decoration: none;
    padding: 5px 8px;
    border-radius: 4px;
    font-size: 14px;
    color: #333; /* 기본 글자색 */
    transition: background-color 0.2s, color 0.2s;
    font-weight: 500;
}

/* 아이콘과 텍스트 사이 간격 조정 */
.header-menu-right a i {
    margin-right: 4px;
}

/* 일반 링크 호버 효과 */
.header-menu-right a:hover {
    background-color: #f0f0f0;
    color: #000;
}

/* 로그인/회원가입 버튼 (비로그인 상태) 강조 */
.header-menu-right a.btn-primary {
    background-color: #007bff; /* 파란색 배경 */
    color: white; /* 흰색 글자 */
    border: 1px solid #007bff;
    font-weight: bold;
}
.header-menu-right a.btn-primary:hover {
    background-color: #0056b3;
    color: white;
}

/* 관리자 메뉴 강조 */
.header-menu-right a.admin-link {
    font-weight: bold;
    border: 1px solid #dc3545;
}
.header-menu-right a.admin-link:hover {
    background-color: #c82333;
}
.content-area {
	width: 90%; /* 화면이 좁을 때 유동적으로 줄어듦 */
	max-width: 1280px; /* 화면이 너무 넓을 때 퍼지는 것 방지 */
	margin-left: auto; /* 좌우 마진 자동으로 중앙 정렬 */
	margin-right: auto;
}
</style>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.30.1/moment.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.30.1/locale/ko.min.js"></script>

<script src="/js/confirm.js"></script>
</head>
<body>
	<div class="container w-1100 flex-box flex-vertical">
    
    </div>

		<div class="flex-box" style="height: 50px; align-items: center;">
				
			<div class="w-25 flex-box flex-center">
				<a href="/"> <img
					src="https://dummyimage.com/200x50/000/fff&text=KH+Shop" alt="KH Shop 로고">
				</a>
			</div>
			
			<div class="w-50">
        	</div>
			
			<div class="w-25 header-menu-right">
				<c:choose>
					<%-- 일반/우수회원 (로그인 상태) --%>
					<c:when
						test="${sessionScope.loginId != null && (sessionScope.loginLevel == '일반회원' || sessionScope.loginLevel == '우수회원')}">
						<a href="/"> <i class="fa-solid fa-house"></i>
						</a>
						<a href="/orders/cart"> <i class="fa-solid fa-cart-shopping"></i>
						</a>
						<a href="/member/wishlist"> <i class="fa-regular fa-heart"></i>
						</a>
						<a href="/member/mypage"> <i class="fa-solid fa-user"></i> <span>내정보</span>
						</a>
						<a href="/member/logout" class="btn-logout"> <i
							class="fa-solid fa-right-from-bracket"></i> <span>로그아웃</span>
						</a>

					</c:when>
					<%-- 관리자 (로그인 상태) --%>
					<c:when
						test="${sessionScope.loginId != null && sessionScope.loginLevel == '관리자'}">
						<a href="/"> <i class="fa-solid fa-house"></i>
						</a>
						<a href="/admin/home" class="admin-link"> 
							<i class="fa-solid fa-gear"></i>
							<span>관리메뉴</span>
						</a>
						<a href="/member/logout" class="btn-logout"> <i
							class="fa-solid fa-right-from-bracket"></i> <span>로그아웃</span>
						</a>
					</c:when>
					<%-- 비회원 --%>
					<c:otherwise>
						<a href="/"> <i class="fa-solid fa-house"></i>
						</a>
						<a href="/orders/cart"> <i class="fa-solid fa-cart-shopping"></i>
						</a>
						<a href="/wishlist"> <i class="fa-regular fa-heart"></i>
						</a>
						<a href="/member/login" class="btn-primary"> <span>로그인</span>
						</a>
						<a href="/member/join" class="btn-primary"> <span>회원가입</span>
						</a>
					</c:otherwise>
				</c:choose>
                
                <a href="/csBoard/list"> 
                    <i class="fa-solid fa-headset"></i> 
                    <span>고객센터</span>
                </a>
			</div>
		</div>

		<div>
			<jsp:include page="/WEB-INF/views/template/menu.jsp"></jsp:include>
			<%--             <jsp:include page="/WEB-INF/views/template/dropdown-menu.jsp"></jsp:include> --%>
		</div>
		<div class="flex-box" style="min-height: 400px;">

			<div class="w-200">

				<c:choose>
					<c:when test="${sessionScope.loginId != null}">
						<div class="cell center">
							<img src="/member/profile?memberId=${sessionScope.loginId}"
								width="150" height="150" class="image-profile">
						</div>
						<div class="cell center">
							<h3>${sessionScope.loginId} (${sessionScope.loginLevel})</h3>
						</div>
						<div class="cell center">
							<a href="/member/mypage"> 
							<i class="fa-solid fa-user"></i> <span>내 정보 보기</span>
							</a>
						</div>
					</c:when>
					<c:otherwise>
						<div class="cell center">
							<h3>비회원 상태</h3>
						</div>
						<div class="cell center">
							<a href="/member/login"> <i
								class="fa-solid fa-right-to-bracket fa-fade"></i> <span>로그인</span>
							</a>
						</div>
						<div class="cell center">
							<a href="/member/join"> <i
								class="fa-solid fa-user-plus fa-fade"></i> <span>회원가입</span>
							</a>
						</div>
					</c:otherwise>
				</c:choose>

			</div>
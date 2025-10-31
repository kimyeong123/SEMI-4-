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
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"> 
<style>
.image-profile {
	border-radius: 50%;
	box-shadow: 0 0 3px 1px #636e72;
	opacity: 0.95;
	transition-property: opacity, box-shadow;
	transition-duration: 0.1s;
	transition-timing-function: ease-out;
}
.image-profile:hover { opacity: 1; }

.menu-toggle-button {
    font-size: 1.5em; 
    cursor: pointer;
    color: #555; 
    padding: 5px; 
    margin-right: 15px; 
    transition: color 0.2s;
}
.menu-toggle-button:hover { color: #000; }

.header-menu-right {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 8px;
    height: 100%;
    white-space: nowrap;
}
.header-menu-right a {
    text-decoration: none;
    padding: 5px 8px;
    border-radius: 4px;
    font-size: 14px;
    color: #333;
    transition: background-color 0.2s, color 0.2s;
    font-weight: 500;
}
.header-menu-right a i { margin-right: 4px; }
.header-menu-right a:hover { background-color: #f0f0f0; color: #000; }
.header-menu-right a.btn-primary {
    background-color: #000; 
    color: white;
    border: 1px solid #000;
    font-weight: bold;
}
.header-menu-right a.btn-primary:hover {
    background-color: #000080; 
    color: white;
}
.header-menu-right a.admin-link {
    font-weight: bold; border: 1px solid #dc3545;
}
.header-menu-right a.admin-link:hover { background-color: #c82333; color: white; }

.content-area {
	width: 90%;
	max-width: 1400px;
	margin-left: auto;
	margin-right: auto;
}

.sidebar-buttons { padding: 20px 0 0 0; }
.menu-link-item { margin-bottom: 5px; }
.menu-link-item a {
    display: block;
    padding: 12px 15px;
    color: #333; 
    text-decoration: none;
    font-weight: 500;
    border: 1px solid #eee; 
    background-color: #f8f8f8; 
    transition: background-color 0.2s, color 0.2s, border-color 0.2s;
    border-radius: 0; 
}
.menu-link-item a:hover { background-color: #e5e5e5; color: #000; border-color: #ccc; }
.menu-link-item i { margin-right: 8px; font-size: 1.1em; }

.main-layout { display: flex; gap: 30px; min-height: 400px; padding-top: 20px; }
.sidebar-area { width: 250px; min-width: 250px; max-width: 250px; }
.sidebar-area.hidden { max-width: 0; min-width: 0; padding: 0; margin: 0; opacity: 0; overflow: hidden; }
.main-content { flex-grow: 1; }

.sidebar-profile { display: flex; flex-direction: column; align-items: center; padding: 10px 0 20px 0; margin-bottom: 20px; border-bottom: 1px solid #eee; }
.sidebar-profile h3 { margin: 10px 0 5px 0; font-size: 1.1em; color: #555; }
.sidebar-profile a { color: #007bff; text-decoration: none; font-size: 0.9em; }

.logo-img { width: 150px; height: 50px; object-fit: contain; display: block; }
.w-25 .logo-img { width: 100%; height: auto; object-fit: contain; }
.w-50.logo { display: flex; justify-content: center; align-items: center; }
.w-50 .logo-img { width: 180px; height: auto; object-fit: contain; margin-bottom: }
</style>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script>
$(function() {
    $('#sidebarToggle').on('click', function() {
        $('#sidebarArea').toggleClass('hidden');
    });
});
</script>

</head>
<body>

	<!-- 헤더 -->
	<div class="container w-1300 flex-box flex-vertical">
		<!-- ✅ 여기만 수정 (50px → 80px) -->
		<div class="flex-box" style="height: 80px; align-items: center;">
			<div class="w-25 flex-box flex-center">
				<a href="/"> 
                    <img src="${pageContext.request.contextPath}/images/KING HEART.png" class="logo-img">
				</a>
			</div>
			<div class="w-50 logo mt-10">
				<a href="/">
					<img src="${pageContext.request.contextPath}/images/KHLOGO.png" class="logo-img">
				</a>
			</div>
			<div class="w-25 header-menu-right">
				<c:choose>
					<c:when test="${sessionScope.loginId != null && (sessionScope.loginLevel == '일반회원' || sessionScope.loginLevel == '우수회원')}">
						<a href="/member/wishlist"><i class="fa-regular fa-heart"></i></a>
						<a href="/orders/cart"><i class="fa-solid fa-cart-shopping"></i></a>
						<a href="/orders/list"><i class="fa-solid fa-receipt"></i></a>
						<a href="/member/mypage"><i class="fa-solid fa-user"></i><span>내정보</span></a>
						<a href="/member/logout" class="btn-logout"><i class="fa-solid fa-right-from-bracket"></i><span>로그아웃</span></a>
					</c:when>
					<c:when test="${sessionScope.loginId != null && sessionScope.loginLevel == '관리자'}">
						<a href="/admin/home" class="admin-link"><i class="fa-solid fa-gear"></i><span>관리메뉴</span></a>
						<a href="/member/logout" class="btn-logout"><i class="fa-solid fa-right-from-bracket"></i><span>로그아웃</span></a>
					</c:when>
					<c:otherwise>
						<a href="/orders/cart"><i class="fa-solid fa-cart-shopping"></i></a>
						<a href="/wishlist"><i class="fa-regular fa-heart"></i></a>
						<a href="/member/login" class="btn-primary"><span>로그인</span></a>
						<a href="/member/join" class="btn-primary"><span>회원가입</span></a>
					</c:otherwise>
				</c:choose>
				<a href="/csBoard/list"><i class="fa-solid fa-headset"></i><span>고객센터</span></a>
			</div>
		</div>
	</div>

	<!-- 주 메뉴 -->
	<div class="container">
		<jsp:include page="/WEB-INF/views/template/menu.jsp"></jsp:include>
	</div>

	<!-- 메인 레이아웃 -->
	<div class="container main-layout">
		<div id="sidebarArea" class="sidebar-area hidden">
			<c:choose>
				<c:when test="${sessionScope.loginId != null}">
					<div class="sidebar-profile">
						<img src="/member/profile?memberId=${sessionScope.loginId}" width="100" height="100" class="image-profile">
						<h3>${sessionScope.loginId} (${sessionScope.loginLevel})</h3>
					</div>
					<div class="sidebar-buttons">
						<div class="menu-link-item">
							<a href="/member/mypage"><i class="fa-solid fa-user"></i><span>내 정보 보기</span></a>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="sidebar-profile"><h3>비회원 상태</h3></div>
					<div class="sidebar-buttons">
						<div class="menu-link-item">
							<a href="/member/login"><i class="fa-solid fa-right-to-bracket fa-fade"></i><span>로그인</span></a>
						</div>
						<div class="menu-link-item">
							<a href="/member/join"><i class="fa-solid fa-user-plus fa-fade"></i><span>회원가입</span></a>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
</body>
</html>

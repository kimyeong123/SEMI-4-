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
/* --- 로고 스타일 --- */
.logo-img {
    width: 160px;
    height: auto;
    object-fit: contain;
    display: block;
}

/* 헤더 전체 */
.flex-box {
    display: flex;
    align-items: center;
    position: relative;
}

/* 왼쪽 로고 */
.w-25.flex-center {
    justify-content: flex-start;
}

/* 가운데 로고 - 진짜 중앙 정렬 */
.w-50.logo {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    justify-content: center;
    align-items: center;
    height: 50px;
}

/* 오른쪽 메뉴 */
.header-menu-right {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 8px;
}

/* 오른쪽 메뉴 링크 스타일 */
.header-menu-right a {
    text-decoration: none;
    padding: 5px 8px;
    border-radius: 4px;
    font-size: 14px;
    color: #333;
    transition: background-color 0.2s, color 0.2s;
    font-weight: 500;
}
.header-menu-right a:hover {
    background-color: #f0f0f0;
    color: #000;
}

/* 로그인 버튼 */
.header-menu-right a.btn-primary {
    background-color: #007bff;
    color: white;
    border: 1px solid #007bff;
    font-weight: bold;
}
.header-menu-right a.btn-primary:hover {
    background-color: #0056b3;
}

/* 관리자 메뉴 강조 */
.header-menu-right a.admin-link {
    font-weight: bold;
    border: 1px solid #dc3545;
}
.header-menu-right a.admin-link:hover {
    background-color: #c82333;
    color: white;
}
</style>

</head>

<body>

<div class="container">
    <div class="flex-box" style="height: 50px;">
        
        <!-- 왼쪽 로고 -->
        <div class="w-25 flex-center">
            <a href="/"> 
                <img src="https://dummyimage.com/150x50/000/fff&text=KH+Shop" alt="KH Shop 로고" class="logo-img">
            </a>
        </div>

        <!-- 가운데 로고 (정중앙) -->
        <div class="w-50 logo">
            <a href="/">
                <img src="${pageContext.request.contextPath}/images/KHLOGO.png" class="logo-img" alt="KH Shop 중앙 로고">
            </a>
        </div>

        <!-- 오른쪽 메뉴 -->
        <div class="w-25 header-menu-right">
            <c:choose>
                <c:when test="${sessionScope.loginId != null && (sessionScope.loginLevel == '일반회원' || sessionScope.loginLevel == '우수회원')}">
                    <a href="/member/wishlist"><i class="fa-regular fa-heart"></i></a>
                    <a href="/orders/cart"><i class="fa-solid fa-cart-shopping"></i></a>
                    <a href="/orders/list"><i class="fa-solid fa-receipt"></i></a>
                    <a href="/member/mypage"><i class="fa-solid fa-user"></i> 내정보</a>
                    <a href="/member/logout" class="btn-logout"><i class="fa-solid fa-right-from-bracket"></i> 로그아웃</a>
                </c:when>
                <c:when test="${sessionScope.loginId != null && sessionScope.loginLevel == '관리자'}">
                    <a href="/admin/home" class="admin-link"><i class="fa-solid fa-gear"></i> 관리메뉴</a>
                    <a href="/member/logout" class="btn-logout"><i class="fa-solid fa-right-from-bracket"></i> 로그아웃</a>
                </c:when>
                <c:otherwise>
                    <a href="/orders/cart"><i class="fa-solid fa-cart-shopping"></i></a>
                    <a href="/wishlist"><i class="fa-regular fa-heart"></i></a>
                    <a href="/member/login" class="btn-primary">로그인</a>
                    <a href="/member/join" class="btn-primary">회원가입</a>
                </c:otherwise>
            </c:choose>

            <a href="/csBoard/list"><i class="fa-solid fa-headset"></i> 고객센터</a>
        </div>
    </div>
</div>

</body>
</html>

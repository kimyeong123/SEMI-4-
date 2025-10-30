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
<!-- Font Awesome 6 버전으로 변경하여 최신 아이콘을 사용합니다. -->
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

.image-profile:hover {
	opacity: 1;
}

/* 새로운 메뉴 토글 버튼 스타일 (menu.jsp에 삽입된 요소) */
.menu-toggle-button {
    font-size: 1.5em; 
    cursor: pointer;
    color: #555; 
    padding: 5px; 
    margin-right: 15px; 
    transition: color 0.2s;
}
.menu-toggle-button:hover {
    color: #000;
}


/* --- 1. 헤더 오른쪽 메뉴 디자인 --- */
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
    background-color: rgba(53, 59, 72,1.0); /* 파란색 배경 */
    color: white; /* 흰색 글자 */
    border: 1px solid;
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
    color: white; /* 관리자 링크 호버 시 글자색 변경 */
}
.content-area {
	width: 90%; /* 화면이 좁을 때 유동적으로 줄어듦 */
	max-width: 1400px; /* 화면이 너무 넓을 때 퍼지는 것 방지 */
	margin-left: auto; /* 좌우 마진 자동으로 중앙 정렬 */
	margin-right: auto;
}

/* --- 2. 사이드바 메뉴 링크 스타일 --- */
.sidebar-buttons {
    padding: 20px 0 0 0; /* 상단 여백 추가 */
}

.menu-link-item {
    margin-bottom: 5px;
}

.menu-link-item a {
    display: block;
    padding: 12px 15px;
    color: #333; 
    text-decoration: none;
    font-weight: 500;
    border: 1px solid #eee; 
    background-color: #f8f8f8; 
    transition: background-color 0.2s, color 0.2, border-color 0.2s;
    border-radius: 0; 
}

.menu-link-item a:hover {
    background-color: #e5e5e5; 
    color: #000;
    border-color: #ccc;
}

.menu-link-item i {
    margin-right: 8px;
    font-size: 1.1em;
}

/* --- 3. 메인 콘텐츠 영역 레이아웃 조정 (트랜지션 제거) --- */
/* 전체 레이아웃 (좌:사이드바, 우:본문) */
.main-layout {
    display: flex;
    gap: 30px; /* 사이드바와 본문 사이의 간격 */
    min-height: 400px; /* 최소 높이 */
    padding-top: 20px;
}

/* 사이드바 영역 (토글 대상) */
.sidebar-area {
    width: 250px; 
    min-width: 250px;
    max-width: 250px;
    /* 트랜지션 제거 (바로 닫힘) */
}

/* 사이드바 숨김 상태 */
.sidebar-area.hidden {
    max-width: 0;
    min-width: 0;
    padding: 0;
    margin: 0;
    opacity: 0;
    overflow: hidden; /* 내용 숨김 */
}

/* 메인 콘텐츠 영역 (사이드바를 제외한 나머지 공간 사용) */
.main-content {
    flex-grow: 1; 
    /* 트랜지션 제거 (바로 확장) */
}

/* 사이드바 프로필 정보 */
.sidebar-profile {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 10px 0 20px 0;
    margin-bottom: 20px;
    border-bottom: 1px solid #eee;
}
.sidebar-profile h3 {
    margin: 10px 0 5px 0;
    font-size: 1.1em;
    color: #555;
}
.sidebar-profile a {
    color: #007bff;
    text-decoration: none;
    font-size: 0.9em;
}
.logo-img {
    width: 150px;      /* 원하는 가로 크기 */
    height: 50px;      /* 원하는 세로 크기 */
    object-fit: contain; /* 비율 유지하면서 영역 맞춤 */
    display: block;    /* 불필요한 inline 공간 제거 */
}
/* 왼쪽 로고 */
.w-25 .logo-img {
    width: 100%;        /* 영역 가득 채움 */
    height: auto;       /* 비율 유지 */
    object-fit: contain; 
}

/* 가운데 로고 */
.w-50 {
    display: flex;
    justify-content: center;  /* 수평 중앙 */
    align-items: center;      /* 수직 중앙 */
}

.w-50 .logo-img {
    width: 180px;       /* 조금 크게 */
    height: auto;       /* 비율 유지 */
    object-fit: contain;
}



</style>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.30.1/moment.min.js"></script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.30.1/locale/ko.min.js"></script>

<script src="/js/confirm.js"></script>

<script>
$(function() {
    // 사이드바 토글 버튼 클릭 이벤트
    $('#sidebarToggle').on('click', function() {
        // 1. 사이드바 영역의 hidden 클래스를 토글
        $('#sidebarArea').toggleClass('hidden');
    });
});
</script>

</head>
<body>
	<div class="container w-1100 flex-box flex-vertical">
    
    </div>

	<!-- 1. 헤더 영역 (로고 + 메뉴) -->
	<div class="container">
		<div class="flex-box" style="height: 50px; align-items: center;">
				
			<div class="w-25 flex-box flex-center">
				<%-- **menu.jsp로 이동된 사이드바 토글 버튼 자리** --%>
				
				<a href="/">
    				<img src="${pageContext.request.contextPath}/images/KING HEART.png" alt="로고이미지" class="logo-img">
				</a>
			</div>
			
			<div class="w-50">
				<a href="/">
					<img src="${pageContext.request.contextPath}/images/KHLOGO.png" class="logo-img">
				</a>
        	</div>
			
			<div class="w-25 header-menu-right">
				<c:choose>
					<%-- 일반/우수회원 (로그인 상태) --%>
					<c:when
						test="${sessionScope.loginId != null && (sessionScope.loginLevel == '일반회원' || sessionScope.loginLevel == '우수회원')}">
						<a href="/member/wishlist"> <i class="fa-regular fa-heart"></i>
						</a>
						<a href="/orders/cart"> <i class="fa-solid fa-cart-shopping"></i>
						</a>
						<a href="/orders/list"><i class="fa-solid fa-receipt"></i>
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
	</div>

	<!-- 2. 주 메뉴 (Template) 영역 -->
	<div class="container">
		<div>
			<jsp:include page="/WEB-INF/views/template/menu.jsp"></jsp:include>
		</div>
	</div>

	<!-- 3. 메인 레이아웃 영역 (사이드바 + 본문) -->
	<div class="container main-layout">
        
        <!-- 왼쪽: 사이드바 및 버튼 (ID 부여) -->
        <%-- 💡 [수정] 기본적으로 숨기기 위해 'hidden' 클래스 추가 --%>
        <div id="sidebarArea" class="sidebar-area hidden">
            
            <c:choose>
                <c:when test="${sessionScope.loginId != null}">
                    <!-- 로그인 상태: 프로필 및 버튼 -->
                    <div class="sidebar-profile">
                        <img src="/member/profile?memberId=${sessionScope.loginId}"
                            width="100" height="100" class="image-profile">
                        <h3>${sessionScope.loginId} (${sessionScope.loginLevel})</h3>
                    </div>
                    
                    <div class="sidebar-buttons">
                        <div class="menu-link-item">
                            <a href="/member/mypage"> 
                                <i class="fa-solid fa-user"></i> 
                                <span>내 정보 보기</span>
                            </a>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- 비회원 상태: 버튼 -->
                    <div class="sidebar-profile">
                        <h3>비회원 상태</h3>
                    </div>
                    <div class="sidebar-buttons">
                        <div class="menu-link-item">
                            <a href="/member/login"> 
                                <i class="fa-solid fa-right-to-bracket fa-fade"></i> 
                                <span>로그인</span>
                            </a>
                        </div>
                        <div class="menu-link-item">
                            <a href="/member/join"> 
                                <i class="fa-solid fa-user-plus fa-fade"></i> 
                                <span>회원가입</span>
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
        
        <!-- 오른쪽: 페이지 본문이 들어갈 공간 -->
        <div class="main-content" id="mainContentArea">
            <!-- 여기에 각 JSP 파일의 내용이 포함됩니다. -->

</body>
</html>

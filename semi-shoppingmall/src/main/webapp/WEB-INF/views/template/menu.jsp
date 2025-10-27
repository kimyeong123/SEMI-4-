<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style>
.menu.flex-box {
    display: flex;
    justify-content: center; /* 가운데 정렬 */
    width: 100%;
    border-bottom: 2px solid #333; /* 메뉴 아래쪽에 굵은 선 추가 */
    margin-bottom: 20px;
}

.category-menu {
    width: auto; 
    white-space: nowrap; 
}

.category-menu ul {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    width: 100%;
}

/* ⭐수정: li display 유지 및 flex-shrink: 0 설정 (줄어듦 방지)⭐ */
.category-menu > ul > li {
    position: relative;
    display: inline-block;
    font-weight: bold; 
    text-transform: uppercase;
    flex-shrink: 0; 
}

/* 최상위 메뉴 항목 스타일 */
.category-menu a {
    text-decoration: none;
    padding: 10px 20px; 
    display: block;
    color: #333; /* 글씨 색상 */
    letter-spacing: 0.5px; /* 글자 간격 약간 추가 */
    transition: background-color 0.2s; 
}

.category-menu a:hover {
    background-color: #ebebeb; /* 호버 배경색 */
}

/* 서브 메뉴 숨기기 */
.sub-menu {
    display: none !important; 
    position: absolute;
    top: 100%;
    left: 0;
    z-index: 20; /* 헤더 메뉴보다 높게 설정 */
    min-width: 180px; 
    background-color: #f7f7f7; 
    border: 1px solid #ddd;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15); 
}

.sub-menu li {
    display: block;
}

.sub-menu a {
    padding: 12px 15px; 
    display: block;
    white-space: nowrap;
    color: #333;
    font-weight: normal; /* 서브 메뉴는 기본 굵기로 */
}

/* 마우스 호버 시 서브 메뉴 표시 */
.category-menu > ul > li:hover > .sub-menu {
    /* ⭐수정: !important로 표시 강제⭐ */
    display: block !important;
}
.category-menu a.admin-menu-link {
    color: #dc3545; /* 빨간색 글씨 */
    font-weight: bold;
}
.category-menu a.admin-menu-link:hover {
    background-color: #f8d7da; /* 살짝 옅은 빨간색 배경 */
    color: #dc3545;
}
</style>
<div class="menu flex-box">
	<nav class="category-menu">
		<ul>
			<li>
			    <a href="/product/list">
			        전체
			    </a>
			</li>
			
			<%-- 2. 최상위 카테고리 반복 (가운데) --%>
			<c:forEach var="topCategory" items="${categoryTree}">
				<li><a
					href="/product/list?categoryNo=${topCategory.categoryNo}">
						${topCategory.categoryName} </a> <c:if
						test="${not empty topCategory.children}">
						<ul class="sub-menu">
							<c:forEach var="subCategory" items="${topCategory.children}">
								<li><a
									href="/product/list?categoryNo=${subCategory.categoryNo}">
										${subCategory.categoryName} </a></li>
							</c:forEach>
						</ul>
					</c:if></li>
			</c:forEach>
			
			<c:if test="${sessionScope.loginId != null && sessionScope.loginLevel == '관리자'}">
			<li>
			    <a href="/admin/product/list" class="admin-menu-link">
			        상품 관리
			    </a>
			</li>
			</c:if>
		</ul>
	</nav>
</div>
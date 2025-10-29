<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style>
/* --- 1. 메인 메뉴 (카테고리) 스타일 --- */
.menu-wrapper { 
    display: flex;
    justify-content: center; /* 내부 카테고리 메뉴를 중앙에 배치 */
    align-items: center; /* 수직 중앙 정렬 */
    position: relative; /* 토글 버튼의 absolute 포지셔닝을 위한 기준점 */
    width: 100%;
    border-bottom: 2px solid #333; 
    margin-bottom: 20px;
    height: 50px; /* 메뉴 높이 지정 */
    background-color: #dcdcdc; /* 조금 더 진한 연한 회색 적용 */
}
.category-menu { 
    width: auto; 
    white-space: nowrap; 
    /* 토글 버튼이 차지하는 공간만큼 왼쪽 패딩을 주어 메뉴가 버튼과 겹치지 않게 합니다. */
    padding-left: 50px; 
}

/* --- 2. 토글 버튼 스타일 (왼쪽 정렬) --- */
.menu-toggle-button {
    position: absolute; /* .menu-wrapper 기준으로 위치 설정 */
    left: 0; /* 왼쪽 끝에 배치 */
    font-size: 1.5em; 
    cursor: pointer;
    color: #555; 
    padding: 10px 15px; /* 클릭 영역 확보 및 중앙 정렬 보조 */
    transition: color 0.2s;
}
.menu-toggle-button:hover {
    color: #000;
}


/* --- 3. 기존 카테고리 메뉴 스타일 --- */
.category-menu ul { list-style: none; padding: 0; margin: 0; display: flex; width: 100%; }
.category-menu > ul > li { position: relative; display: inline-block; font-weight: bold; text-transform: uppercase; flex-shrink: 0; }
.category-menu a { text-decoration: none; padding: 10px 20px; display: block; color: #333; letter-spacing: 0.5px; transition: background-color 0.2s; }
.category-menu a:hover { background-color: #ebebeb; }
.sub-menu { display: none !important; position: absolute; top: 100%; left: 0; z-index: 20; min-width: 180px; background-color: #f7f7f7; border: 1px solid #ddd; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15); }
.sub-menu li { display: block; }
.sub-menu a { padding: 12px 15px; display: block; white-space: nowrap; color: #333; font-weight: normal; }
.category-menu > ul > li:hover > .sub-menu { display: block !important; }

/* 관리자 메뉴 링크에만 스타일 적용 (필요하다면) */
.category-menu a.admin-mode {
    color: #333;
    font-weight: bold;
}
.category-menu a.admin-mode:hover {
    background-color: #f8d7da;
    color: #dc3545;
}
</style>

<!-- 메뉴 구조 변경: 토글 버튼을 menu-wrapper 내부의 absolute로 위치 지정 -->
<div class="menu-wrapper"> 
	<span id="sidebarToggle" class="menu-toggle-button" title="사이드바 열기/닫기">
    	<i class="fa-solid fa-list-ul"></i> 
	</span>
    
	<nav class="category-menu">
		<ul>
			<%-- 1. URL 경로 설정: 관리자 여부에 따라 접두사(prefix)와 스타일 클래스를 설정합니다. --%>
			<c:set var="urlPrefix" value=""/>
			<c:set var="linkClass" value=""/>
			<c:if test="${sessionScope.loginLevel == '관리자'}">
				<c:set var="urlPrefix" value="/admin"/>
				<c:set var="linkClass" value="admin-mode"/>
			</c:if>
			<%-- 2. 전체 상품 목록 링크 --%>
			<li>
    			<a href="${urlPrefix}/product/list" class="${linkClass}">
        			전체
    			</a>
			</li>
			
			<%-- 3. 카테고리 반복: 설정된 접두사(${urlPrefix})와 클래스(${linkClass})를 사용하여 링크 생성 --%>
			<c:forEach var="topCategory" items="${categoryTree}">
				<li>
					<a href="${urlPrefix}/product/list?categoryNo=${topCategory.categoryNo}" class="${linkClass}">
						${topCategory.categoryName} 
					</a> 
					<c:if test="${not empty topCategory.children}">
						<ul class="sub-menu">
							<c:forEach var="subCategory" items="${topCategory.children}">
								<li>
									<a href="${urlPrefix}/product/list?categoryNo=${subCategory.categoryNo}" class="${linkClass}">
										${subCategory.categoryName} 
									</a>
								</li>
							</c:forEach>
						</ul>
					</c:if>
				</li>
			</c:forEach>
		</ul>
	</nav>
</div>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style>
/* ... (CSS 스타일은 생략하고 기존과 동일하게 유지됩니다) ... */
.menu.flex-box {
    display: flex;
    justify-content: center; 
    width: 100%;
    border-bottom: 2px solid #333; 
    margin-bottom: 20px;
}
.category-menu { width: auto; white-space: nowrap; }
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
<div class="menu flex-box">
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
			
			<%-- ⭐ 추가 관리자 메뉴가 필요하면 여기에 추가 ⭐
			<c:if test="${sessionScope.loginLevel == '관리자'}">
				<li><a href="/admin/order/list" class="admin-mode">주문 관리</a></li>
			</c:if>
			--%>
		</ul>
	</nav>
</div>
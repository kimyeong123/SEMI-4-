<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<style>
/* 관리 메뉴 항목 스타일 */
.admin-menu h2 {
    /* ⭐상하 마진을 넉넉히 주어 항목 간 간격을 확보합니다.⭐ */
    margin: 15px 0; 
    /* 링크 텍스트의 밑줄을 제거하여 깔끔하게 표시합니다. */
    text-align: center; /* 메뉴를 중앙 정렬하고 싶다면 추가 */
}
.admin-menu h2 a {
    text-decoration: none;
    color: #333;
    font-size: 1.2em;
}
.admin-menu h2 a:hover {
    color: red; /* 호버 시 색상 변경 (선택 사항) */
}
</style>

<div class="cell center">
	<h1>관리 페이지</h1>
</div>

<div class="cell admin-menu">
    <h2><a href="/admin/member/list">회원 관리</a></h2>
    <h2><a href="/admin/product/list">상품 관리</a></h2>
    <h2><a href="/admin/category/list">카테고리 관리</a></h2>
    <h2><a href="/admin/stat/all">데이터 현황</a></h2>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
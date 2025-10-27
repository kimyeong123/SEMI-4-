<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<link rel="stylesheet" href="css/error.css">

<div class="error-page-wrap">
    <h1>${title}</h1>

    <img src="/images/error/403.png" class="error-image">

    <a href="/">홈으로</a>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
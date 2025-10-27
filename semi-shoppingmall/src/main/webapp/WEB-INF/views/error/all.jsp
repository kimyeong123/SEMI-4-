<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<link rel="stylesheet" href="css/error.css">

<div class="error-page-wrap">
    
    <h1>일시적인 오류가 발생했습니다</h1>
    <p>잠시 후에도 같은 증상 발생시 관리자 호출</p>

    <img src="/images/error/500.png" class="error-image"> 

    <a href="/">홈으로</a>
    
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
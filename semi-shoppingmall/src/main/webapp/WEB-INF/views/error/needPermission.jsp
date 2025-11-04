<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<link rel="stylesheet" href="css/error.css">

<div class="error-page-wrap w-100">
	<div class="cell center">
    <h1>${title}</h1>
    <img src="${pageContext.request.contextPath}/images/error/403.png" class="error-image" width="500">
    </div>
    <div class="cell center mt-20">
    	<button type="button" class="btn btn-neutral" onclick="location.href='/'">홈으로</button>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
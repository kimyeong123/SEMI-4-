<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<head>
    <link rel="stylesheet" href="css/error.css">
    
</head>
<div class="error-page-wrap w-100">
	<div class="cell center">
		<img src="/images/error/401.png" width="500">
	</div>
	    <div class="cell center mt-20">
    	<button type="button" class="btn btn-neutral" onclick="location.href='/member/login'">로그인하기</button>
    </div>
</div>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
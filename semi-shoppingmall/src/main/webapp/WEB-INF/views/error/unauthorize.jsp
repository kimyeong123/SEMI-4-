<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<head>
    <link rel="stylesheet" href="css/error.css">
    
</head>
	
<h1>${title}</h1>
<h2><a href="/member/login">로그인하기</a></h2>

<img src="/images/error/401.png" width="300">

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
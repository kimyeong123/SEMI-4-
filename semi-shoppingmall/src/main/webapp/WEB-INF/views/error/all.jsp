<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<div class="cell w-100">
	<div class="cell center">
		<h1>일시적인 오류가 발생했습니다</h1>
		<p>잠시 후에도 같은 증상이 발생하면 관리자에게 문의하세요</p>
		<img src="${pageContext.request.contextPath}/images/error/500.png" width="500">
	</div>
	<div class="cell center mt-20">
		<button type="button" class="btn btn-neutral" onclick="location.href='/'">홈으로</button>
	</div>
</div>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

    

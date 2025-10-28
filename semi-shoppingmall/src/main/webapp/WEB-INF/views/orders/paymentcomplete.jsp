<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<div class="cell center w-100">
	<h2>결제가 완료되었습니다</h2>
	<h3>상품을 구매해주셔서 감사합니다</h3>
	<div class="cell center">
		<button type="button" class="btn btn-neutral" onclick="location.href='/orders/list'">주문 내역 보기</button>
	</div>
    
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
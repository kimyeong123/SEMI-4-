<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>	

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>위시리스트</title>
<link rel="stylesheet" href="/css/commons.css">
<style>
.wishlist-container {
	display: flex; /* 가로로 나열 */
	flex-wrap: wrap; /* 화면 크기 넘으면 다음 줄로 */
	gap: 20px; /* 카드 사이 간격 */
	justify-content: flex-start; /* 왼쪽 정렬 */
}

.wishlist-card {
	display: flex;
	flex-direction: row; /* 이미지와 텍스트를 가로로 */
	align-items: center;
	border: 1px solid #ccc;
	padding: 10px;
	width: 300px; /* 카드 너비 */
	box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.1);
	border-radius: 8px;
}

.wishlist-card img {
	width: 120px;
	height: 120px;
	object-fit: cover;
	margin-right: 15px; /* 이미지와 텍스트 사이 간격 */
}

.wishlist-card .text-container {
	display: flex;
	flex-direction: column;
}
</style>
</head>
<body>
	<h2 style="text-align: center;">내 위시리스트</h2>
	<div class="wishlist-container">
		<c:if test="${wishlist == null}">
			<p style="text-align: center; color: gray;">위시리스트에 등록된 상품이 없습니다.</p>
		</c:if>
		<c:forEach var="item" items="${wishlist}">
			<div class="wishlist-card">
				<%-- 				<img src="${item.productImage != null ? item.productImage : '/images/default.png'}" > --%>
				<img src="https://dummyimage.com/250x200" alt="${item.productName}">
				<h3>${item.productName}</h3>
				<p class="price">${item.productPrice != null ? item.productPrice : 0}원</p>

				<!-- 장바구니 담기 -->
				<form action="/cart/add" method="post">
					<input type="hidden" name="productNo" value="${item.productNo}">
					<button type="submit" class="btn-cart">장바구니 담기</button>
				</form>

				<!-- 위시리스트에서 삭제 -->
				<form action="/member/wishlist/delete" method="post">
					<input type="hidden" name="productNo" value="${item.productNo}">
					<button type="submit" class="btn-delete">삭제</button>
				</form>
			</div>
		</c:forEach>
	</div>
</body>
</html>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<head>
<script type="text/javascript">
$(document).on("click", ".wishlistIcon", function() {
    var icon = $(this);
    var productNo = icon.data("product-no");

    $.ajax({
        url: "${pageContext.request.contextPath}/rest/wishlist/toggle",
        method: "post",
        data: { productNo: productNo },
        success: function(response) {
            if(response.wishlisted) {
                icon.removeClass("fa-regular").addClass("fa-solid");
            } else {
                icon.removeClass("fa-solid").addClass("fa-regular");
            }
            icon.siblings(".wishlist-count").text(response.count);
        },
        error: function() {
            alert("로그인이 필요합니다.");
        }
    });
});
</script>
</head>
<div class="container w-800">
	<h1>상품 목록</h1>

	<!-- 신규 등록 버튼 -->
	<h2>
		<a href="add" class="btn btn-positive">신규 등록</a>
	</h2>

	<!-- 상품 수 표시 -->
	<h2>상품 수 : ${productList.size()}</h2>

	<!-- 검색창 -->
	<form action="list" method="get" style="margin-bottom: 20px;">
		<select name="column">
			<option value="product_name"
				${column == 'product_name' ? 'selected' : ''}>상품명</option>
			<option value="product_content"
				${column == 'product_content' ? 'selected' : ''}>상품내용</option>
		</select> <input type="search" name="keyword" value="${keyword}"
			placeholder="검색어 입력">
		<button type="submit">검색</button>
	</form>

	<!-- 상품 테이블 -->
	<table border="1" width="100%">
		<thead>
			<tr>
				<th>썸네일</th>
				<th>번호</th>
				<th>상품명</th>
				<th>가격</th>
				<th>평균 평점</th>
				<th>작업</th>
			</tr>
		</thead>
		<tbody align="center">
			<c:forEach var="p" items="${productList}">
				<tr>
					<!-- 썸네일 표시 -->
					<td><c:if test="${p.productThumbnailNo != null}">
							<img
								src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${p.productThumbnailNo}"
								width="50" height="50" style="object-fit: cover;">
						</c:if></td>

					<td>${p.productNo}</td>
					<td><a href="detail?productNo=${p.productNo}">${p.productName}</a>
						<i class="wishlistIcon ${wishlistStatus[p.productNo] ? 'fa-solid' : 'fa-regular'} fa-heart red" data-product-no="${p.productNo}"></i>
						<span class="wishlist-count">${wishlistCounts[p.productNo]}</span>
					<td>${p.productPrice}</td>
					<td>${p.productAvgRating}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

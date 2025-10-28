<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
<style>
	table {
		border-collapse: collapse; /* 테이블 경계선 겹침 제거 */
		width: 100%;
		box-shadow: 0 1px 3px rgba(0,0,0,0.1); /* 테이블 전체에 은은한 그림자 */
	}
	
	th, td {
		padding: 12px 15px; /* 패딩 증가로 가독성 향상 */
		text-align: left;
		border: 1px solid #ddd; /* 모든 셀에 얇은 경계선 */
	}
	
	/* 헤더 스타일 */
	thead tr {
		background-color: #f2f2f2; /* 헤더 배경색 */
		color: #333;
		font-weight: bold;
		text-align: center; /* 헤더 텍스트 중앙 정렬 */
	}
	th {
    text-align: center !important; /* 모든 th를 중앙 정렬 */
	}
	
	/* 바디 셀 정렬 */
	tbody td {
    text-align: center; 
}
</style>
</head>
<div class="container w-800">
	<h1>상품 목록</h1>


	<h2>상품 수 : ${productList.size()}</h2>

	<form action="list" method="get" style="margin-bottom: 20px;">
		<select name="column">
			<option value="product_name"
				${column == 'product_name' ? 'selected' : ''}>상품명</option>
			<option value="product_content"
				${column == 'product_content' ? 'selected' : ''}>상품내용</option>
		</select> <input type="search" name="keyword" value="${keyword}"
			placeholder="검색어 입력">
		<input type="hidden" name="order" value="${order}">
		<input type="hidden" name="categoryNo" value="${categoryNo}">
		<button type="submit">검색</button>
	</form>

	<table border="1" width="100%">
		<thead>
			<tr>
				<th>이미지</th>
				<th>상품명</th>
				
				<%--  가격 정렬 추가  --%>
				<th>
					<a href="list?column=product_price&order=${column == 'product_price' && order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&categoryNo=${categoryNo}">
						가격
						<c:if test="${column == 'product_price'}">
							${order == 'asc' ? '▲' : '▼'}
						</c:if>
					</a>
				</th>
				
				<%--  평균 평점 정렬 추가  --%>
				<th>
					<a href="list?column=product_avg_rating&order=${column == 'product_avg_rating' && order == 'desc' ? 'asc' : 'desc'}&keyword=${keyword}&categoryNo=${categoryNo}">
						평균 평점
						<c:if test="${column == 'product_avg_rating'}">
							${order == 'desc' ? '▼' : '▲'}
						</c:if>
					</a>
				</th>
			</tr>
		</thead>
		<tbody align="center">
			<c:forEach var="p" items="${productList}">
				<tr>
					<td><c:if test="${p.productThumbnailNo != null}">
							<img
								src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${p.productThumbnailNo}"
								width="50" height="50" style="object-fit: cover;">
						</c:if></td>

					<td><a href="detail?productNo=${p.productNo}">${p.productName}</a>
						<i class="wishlistIcon ${wishlistStatus[p.productNo] ? 'fa-solid' : 'fa-regular'} fa-heart red" data-product-no="${p.productNo}"></i>
						<span class="wishlist-count">${wishlistCounts[p.productNo]}</span>
					</td>
					<td><fmt:formatNumber value="${p.productPrice}" pattern="#,##0"/>원</td>
					<td><fmt:formatNumber value="${p.productAvgRating}" pattern="0.00"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

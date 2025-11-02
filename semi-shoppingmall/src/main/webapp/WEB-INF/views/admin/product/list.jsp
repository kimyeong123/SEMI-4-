<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<style>
	/* 기본 스타일 */
	.btn {
		border: none;
		padding: 6px 10px;
		border-radius: 5px;
		cursor: pointer;
		text-decoration: none;
		text-align: center;
	}
	.btn-positive { background: #4CAF50; color: white; }
	.btn-secondary { background: #2196F3; color: white; }

	table {
		border-collapse: collapse;
		width: 100%;
		box-shadow: 0 1px 3px rgba(0,0,0,0.1);
	}
	th, td {
		padding: 12px 15px;
		border: 1px solid #ddd;
		text-align: center;
	}
	thead tr {
		background-color: #f2f2f2;
		color: #333;
		font-weight: bold;
	}
	tbody tr:nth-child(odd) { background-color: #f9f9f9; }
	tbody tr:hover { background-color: #f0f0f0; cursor: pointer; }
	tbody img { border-radius: 3px; vertical-align: middle; }
	tbody td a { color: #2196F3; text-decoration: none; }
	tbody td a:hover { text-decoration: underline; }
	.fa-heart.red { color: #dc3545; margin-left: 5px; }

	.action-group {
		display: flex;
		justify-content: center;
		gap: 8px;
	}
	.btn-action {
		background: #f0f0f0;
		color: #333;
		padding: 4px 8px;
		border-radius: 4px;
		text-decoration: none;
		font-size: 13px;
		border: 1px solid #ddd;
		transition: background-color 0.2s;
		white-space: nowrap;
	}
	.btn-action:hover { background-color: #e0e0e0; }
	.btn-delete {
		background: #dc3545;
		color: white;
		border: 1px solid #dc3545;
	}
	.btn-delete:hover { background-color: #c82333; }
</style>


<div class="container w-800 ">
	<div class="cell ">
		<h1>상품 관리</h1>
	<div>
	<div class="cell mb-20">
    	<a href="add" class="btn btn-positive">+ 상품 신규 등록</a>
    </div>
	<h2>상품 수 : ${productList.size()}</h2>
	
	<form action="list" method="get" style="margin-bottom: 20px;">
		<select name="column">
			<option value="product_name" ${column == 'product_name' ? 'selected' : ''}>상품명</option>
		</select>
		<input type="search" name="keyword" value="${keyword}" placeholder="검색어 입력">
		<button type="submit">검색</button>
	</form>

	<table>
		<thead>
			<tr>
				<th>이미지</th>
				<th>
					<a href="list?column=product_no&order=${column == 'product_no' && order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&categoryNo=${categoryNo}">
						상품 번호
						<c:if test="${column == 'product_no'}">${order == 'asc' ? '▲' : '▼'}</c:if>
					</a>
				</th>
				<th>상품명</th>
				<th>
					<a href="list?column=product_price&order=${column == 'product_price' && order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&categoryNo=${categoryNo}">
						가격
						<c:if test="${column == 'product_price'}">${order == 'asc' ? '▲' : '▼'}</c:if>
					</a>
				</th>
				<th>
					<a href="list?column=product_avg_rating&order=${column == 'product_avg_rating' && order == 'desc' ? 'asc' : 'desc'}&keyword=${keyword}&categoryNo=${categoryNo}">
						평균 평점
						<c:if test="${column == 'product_avg_rating'}">${order == 'asc' ? '▲' : '▼'}</c:if>
					</a>
				</th>
				<th>작업</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="p" items="${productList}">
				<tr>
					<td>
						<c:if test="${p.productThumbnailNo != null}">
							<img src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${p.productThumbnailNo}" width="50" height="50" style="object-fit: cover;">
						</c:if>
					</td>
					<td>${p.productNo}</td>
					<td>
						<a href="detail?productNo=${p.productNo}">${p.productName}</a>
						<i class="fa-solid fa-heart red"></i>
						<span>${wishlistCounts[p.productNo]}</span>
					</td>
					<td><fmt:formatNumber value="${p.productPrice}" pattern="#,##0"/>원</td>
					<td><fmt:formatNumber value="${p.productAvgRating}" pattern="0.00"/></td>
					<td>
						<div class="action-group">
							<a href="edit?productNo=${p.productNo}" class="btn-action">수정</a>
							<form action="${pageContext.request.contextPath}/admin/product/delete" method="post" style="display:inline;">
								<input type="hidden" name="productNo" value="${p.productNo}" />
								<button type="submit" onclick="return confirm('정말 삭제하시겠습니까?');" class="btn-action btn-delete">삭제</button>
							</form>
						</div>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    // 테이블 tr 클릭 시 상세페이지 이동
    document.querySelectorAll("tbody tr").forEach(function(row) {
        row.addEventListener("click", function(e) {
            if(e.target.tagName === "A" || e.target.tagName === "BUTTON" || e.target.closest("form") || e.target.classList.contains("fa-heart")) {
                return; // 링크, 버튼, 하트 클릭 시 이동 방지
            }
            const productNo = row.querySelector("td:nth-child(2)").textContent.trim();
            window.location.href = "detail?productNo=" + productNo;
        });
    });
});
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

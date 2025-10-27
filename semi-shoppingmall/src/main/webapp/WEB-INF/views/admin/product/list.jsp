<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style>
	/* (스타일 시트 내용은 변경 없이 유지합니다) */
	.btn {
		border: none;
		padding: 6px 10px;
		border-radius: 5px;
		cursor: pointer;
		text-decoration: none;
	}
	.btn-positive { background: #4CAF50; color: white; }
	.btn-secondary { background: #2196F3; color: white; }
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

/* ⭐ 작업 링크/버튼 일관화 스타일 추가 ⭐ */
.action-group {
    display: flex;
    justify-content: center; /* 작업 그룹 중앙 정렬 */
    gap: 8px; /* 버튼 간격 */
}

.btn-action {
    background: #f0f0f0; /* 배경색으로 버튼처럼 보이게 함 */
    color: #333;
    padding: 4px 8px;
    border-radius: 4px;
    text-decoration: none;
    font-size: 13px; /* 작은 크기로 통일 */
    border: 1px solid #ddd;
    transition: background-color 0.2s;
    white-space: nowrap; /* 줄바꿈 방지 */
}

.btn-action:hover {
    background-color: #e0e0e0;
}

/* 삭제 버튼 스타일 (삭제 버튼은 빨간색으로 강조) */
.btn-delete {
    background: #dc3545; /* 빨간색 배경 */
    color: white;
    border: 1px solid #dc3545;
}

.btn-delete:hover {
    background-color: #c82333;
}

/* ⭐ 작업 링크/버튼 일관화 스타일 추가 ⭐ */
.action-group {
    display: flex;
    justify-content: center; /* 작업 그룹 중앙 정렬 */
    gap: 8px; /* 버튼 간격 */
	}

.btn-action {
    background: #f0f0f0; /* 배경색으로 버튼처럼 보이게 함 */
    color: #333;
    padding: 4px 8px;
    border-radius: 4px;
    text-decoration: none;
    font-size: 13px; /* 작은 크기로 통일 */
    border: 1px solid #ddd;
    transition: background-color 0.2s;
    white-space: nowrap; /* 줄바꿈 방지 */
}

.btn-action:hover {
    background-color: #e0e0e0;
}

/* 삭제 버튼 스타일 (삭제 버튼은 빨간색으로 강조) */
.btn-delete {
    background: #dc3545; /* 빨간색 배경 */
    color: white;
    border: 1px solid #dc3545;
}

.btn-delete:hover {
    background-color: #c82333;
}
	
	/* 홀수 행 배경색 (Zebra Striping) */
	tbody tr:nth-child(odd) {
		background-color: #f9f9f9;
	}
	
	/* 마우스 오버 시 강조 효과 */
	tbody tr:hover {
		background-color: #f0f0f0;
		cursor: pointer;
	}
	
	/* 썸네일 스타일 */
	tbody img {
		border-radius: 3px;
		vertical-align: middle;
	}

	/* 링크 스타일 */
	tbody td a {
		color: #2196F3;
		text-decoration: none;
	}

	tbody td a:hover {
		text-decoration: underline;
	}

	/* 위시리스트 카운트 스타일 */
	.fa-heart.red {
		color: #dc3545;
		margin-left: 5px;
	}
</style>
<div class="container w-800">
	<h1>상품 목록</h1>

	<div class="cell" style="display:flex; gap:10px; align-items:center;">
		<a href="add" class="btn btn-positive">+ 상품 신규 등록</a>
		<a href="${pageContext.request.contextPath}/admin/product/option/manage"
		   class="btn btn-secondary">+ 옵션 신규 등록</a>
	</div>

	<h2>상품 수 : ${productList.size()}</h2>

	<form action="list" method="get" style="margin-bottom: 20px;">
		<select name="column">
			<option value="product_name"
				${column == 'product_name' ? 'selected' : ''}>상품명</option>
			<option value="product_content"
				${column == 'product_content' ? 'selected' : ''}>상품내용</option>
		</select>
		<input type="search" name="keyword" value="${keyword}" placeholder="검색어 입력">
		<button type="submit">검색</button>
	</form>

	<table>
		<thead>
			<tr>
				<th>이미지</th> 
				
				<th>
					<a href="list?column=product_no&order=${column == 'product_no' && order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}">
						상품 번호
						<c:if test="${column == 'product_no'}">
							${order == 'asc' ? '▲' : '▼'}
						</c:if>
					</a>
				</th>
				
				<th>
					<a href="list?column=product_name&order=${column == 'product_name' && order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}">
						상품명
						<c:if test="${column == 'product_name'}">
							${order == 'asc' ? '▲' : '▼'}
						</c:if>
					</a>
				</th>
				
				<th>
					<a href="list?column=product_price&order=${column == 'product_price' && order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}">
						가격
						<c:if test="${column == 'product_price'}">
							${order == 'asc' ? '▲' : '▼'}
						</c:if>
					</a>
				</th>
				
				<th>
					<a href="list?column=product_avg_rating&order=${column == 'product_avg_rating' && order == 'desc' ? 'asc' : 'desc'}&keyword=${keyword}">
						평균 평점
						<c:if test="${column == 'product_avg_rating'}">
							${order == 'asc' ? '▲' : '▼'}
						</c:if>
					</a>
				</th>
				
				<th>작업</th>
			</tr>
		</thead>
		<tbody align="center">
			<c:forEach var="p" items="${productList}">
				<tr>
					<td>
						<c:if test="${p.productThumbnailNo != null}">
							<img src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${p.productThumbnailNo}"
								width="50" height="50" style="object-fit: cover;">
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
							<form action="${pageContext.request.contextPath}/admin/product/delete"
								method="post" style="display:inline;">
								<input type="hidden" name="productNo" value="${p.productNo}" />
								<button type="submit" 
										onclick="return confirm('정말 삭제하시겠습니까?');" 
										class="btn-action btn-delete">삭제</button>
							</form>
						</div>
					</td>
					
				</tr>
			</c:forEach>
		</tbody>
	</table>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
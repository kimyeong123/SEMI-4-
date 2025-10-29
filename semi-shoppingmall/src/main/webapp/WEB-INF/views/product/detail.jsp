<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<link href="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>
<link rel="stylesheet" type="text/css" href="/summernote/custom-summernote.css">
<script src="/summernote/custom-summernote.js"></script>

<style>
h1 {font-size: 1.8em;padding-bottom: 10px;margin-bottom: 30px;color: #333;border-bottom: 1px solid #ddd;}
h2 { color: #222; font-size: 1.8em; margin-bottom: 10px;}
h3 { color: #444; font-size: 1.5em; margin-top: 30px; margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
.field, .form-control {padding: 10px;border: 1px solid #ccc;border-radius: 0;box-sizing: border-box;width: 100%;}
.w-100 { width: 100%; }
.gray { color: #666; }
.blue { color: #3498db; }
.btn {padding: 10px 20px;border-radius: 0;cursor: pointer;font-weight: normal;transition: background-color 0.2s, color 0.2s, border-color 0.2s;text-decoration: none;display: inline-block;text-align: center;border: 1px solid;font-size: 1em;}
.btn-cart { border-color: #666; color: #333; background-color: #ddd; font-size: 1.1em; flex-grow: 1; }	
.btn-cart:hover { background-color: #bbb; border-color: #555; }
.btn-cart:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-secondary {border-color: #aaa;color: #555;background-color: #f5f5f5;padding: 10px 20px;}
.btn-secondary:hover {background-color: #eee;border-color: #888;}
.review-table-fixed {table-layout: fixed;border-collapse: collapse;width: 100%;margin-top: 10px;}
.review-table-fixed th, .review-table-fixed td {border: 1px solid #ccc;padding: 12px 10px;vertical-align: middle;}
.review-table-fixed th {background-color: #f8f8f8;text-align: center;color: #495057;}
.review-table-fixed tbody tr td:last-child {width: 140px;text-align: center;white-space: nowrap;}
.review-table-fixed tbody tr td.review-content {width: auto;word-break: break-all;white-space: pre-wrap;}
.review-table-fixed thead tr th:nth-child(1) { width: 12%; }
.review-table-fixed thead tr th:nth-child(2) { width: 15%; }
.review-table-fixed thead tr th:nth-child(4) { width: 12%; }
.review-table-fixed thead tr th:nth-child(5) { width: 11%; }
.review-table-fixed thead tr th:nth-child(3) { width: 50%; }
</style>

<c:if test="${not empty product}">
<script type="text/javascript">
var productNo = ${product.productNo};

$(function() {

    // 위시리스트 토글
    $("#wishlist-heart").on("click", function() {
        $.ajax({
            url : "${pageContext.request.contextPath}/rest/wishlist/toggle",
            method : "post",
            data : { productNo : productNo },
            success : function(response) {
                if (response.wishlisted) {
                    $("#wishlist-heart i").removeClass("fa-regular").addClass("fa-solid").css("color", "red");
                } else {
                    $("#wishlist-heart i").removeClass("fa-solid").addClass("fa-regular").css("color", "gray");
                }
                $("#wishlist-count").text(response.count);
            },
            error : function() { alert("로그인이 필요합니다."); }
        });
    });

    // ✅ 장바구니 담기 (색상+사이즈 기반)
    $("#addToCartBtn").on("click", function() {
        var color = $("#color-selector").val();
        var size = $("#size-selector").val();
        var quantity = $("#cartQuantity").val();

        if (!color || !size) {
            alert("색상과 사이즈를 모두 선택해주세요.");
            return;
        }
        if (quantity < 1 || isNaN(quantity)) {
            alert("수량을 1개 이상 입력해주세요.");
            $("#cartQuantity").val(1);
            return;
        }

        $.ajax({
            url: "${pageContext.request.contextPath}/rest/cart/add",
            method: "post",
            data: {
                productNo: productNo,
                color: color,
                size: size,
                cartAmount: quantity
            },
            success: function() {
                alert("장바구니에 상품을 담았습니다!");
            },
            error: function(xhr) {
                if (xhr.status === 401) alert("로그인이 필요합니다.");
                else alert("장바구니 담기 중 오류 발생");
            }
        });
    });
});
</script>
</c:if>

<c:if test="${empty product}">
<div class="container" style="text-align:center; padding:50px;">
	<h1>상품 정보를 찾을 수 없습니다.</h1>
	<p>요청하신 상품 번호에 해당하는 상품이 존재하지 않거나, 잘못된 접근입니다.</p>
	<a href="list" class="btn btn-secondary" style="margin-top:20px;">상품 목록으로 돌아가기</a>
</div>
</c:if>

<c:if test="${not empty product}">
<div class="container">
	<h1>상품 상세정보</h1>
	<div style="display:flex; gap:30px;">
		<div style="flex-shrink:0; width:300px;">
			<c:choose>
				<c:when test="${product.productThumbnailNo != null}">
					<img src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${product.productThumbnailNo}"
					width="300" height="300" style="object-fit:cover; border:1px solid #ddd;">
				</c:when>
				<c:otherwise>
					<div style="width:300px; height:300px; background:#f0f0f0; display:flex; justify-content:center; align-items:center; color:#999;">
						상품 이미지 없음
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		
		<div style="flex-grow:1;">
			<h2>${product.productName}</h2>
			<h3><fmt:formatNumber value="${product.productPrice}" type="number"/>원</h3>
			<p>평점: 
				<c:if test="${product.productAvgRating > 0}">
					<fmt:formatNumber value="${product.productAvgRating}" pattern="0.0"/> / 5.0
				</c:if>
				<c:if test="${product.productAvgRating == 0}">평점 없음</c:if>
				(<span id="review-count">${reviewList.size()}</span>개 리뷰)
			</p>

			<div><span class="gray">예상 도착일:</span> <strong class="blue">${estimatedDeliveryDate}</strong></div>

			<p id="wishlist-heart" style="margin-top:5px;">
				<i class="fa-heart ${wishlisted ? 'fa-solid' : 'fa-regular'}" style="color:${wishlisted ? 'red' : 'gray'}; cursor:pointer;"></i>
				<span id="wishlist-count">${wishlistCount}</span>
			</p>

			<hr style="border-top:1px solid #ddd; margin:20px 0;">

			<!-- ✅ 옵션 선택 (색상 + 사이즈) -->
			<div class="option-select">
				<p><strong>옵션 1: 색상 선택</strong></p>
				<select id="color-selector" class="field w-100" required>
					<option value="">색상을 선택하세요</option>
					<c:forEach var="color" items="${colorList}">
						<option value="${color}">${color}</option>
					</c:forEach>
				</select>

				<p style="margin-top:15px;"><strong>옵션 2: 사이즈 선택</strong></p>
				<select id="size-selector" class="field w-100" required>
					<option value="">사이즈를 선택하세요</option>
					<c:forEach var="size" items="${sizeList}">
						<option value="${size}">${size}</option>
					</c:forEach>
				</select>
			</div>

			<div style="margin-top:15px;">
				<label for="cartQuantity">수량</label>
			</div>
			<div style="margin-top:5px;">
				<input type="number" id="cartQuantity" value="1" min="1" class="field" style="width:80px;">
			</div>

			<div id="purchase-buttons" style="display:flex; gap:10px; margin-top:25px;">
				<button id="addToCartBtn" class="btn btn-cart">장바구니 담기</button>
			</div>
		</div>
	</div>

	<hr style="border-top:3px solid #eee; margin:40px 0;">

	<h3>상품 상세 설명</h3>
	<div class="content-display" style="min-height:200px; padding:20px 0; line-height:1.8;">
		${product.productContent}
	</div>

	<hr style="border-top:3px solid #eee; margin:40px 0;">

	<h3>리뷰 작성</h3>
	<form id="reviewForm" method="post" style="padding:20px; border:1px solid #eee; margin-bottom:30px; background:white;">
		<input type="hidden" name="productNo" value="${product.productNo}">
		<input type="hidden" id="reviewRatingInput" name="reviewRating" value="0">

		<div id="reviewRatingStars" style="font-size:28px; margin-bottom:10px;">
			<i class="fa-regular fa-star star-input" data-rating="1" style="color:#ccc;"></i>
			<i class="fa-regular fa-star star-input" data-rating="2" style="color:#ccc;"></i>
			<i class="fa-regular fa-star star-input" data-rating="3" style="color:#ccc;"></i>
			<i class="fa-regular fa-star star-input" data-rating="4" style="color:#ccc;"></i>
			<i class="fa-regular fa-star star-input" data-rating="5" style="color:#ccc;"></i>
		</div>

		<textarea name="reviewContent" class="form-control summernote-editor" rows="4"
		placeholder="상품에 대한 솔직한 리뷰를 입력해주세요." style="width:100%; margin-bottom:10px;"></textarea>

		<button type="button" id="submitReviewBtn" class="btn btn-black">리뷰 등록</button>
	</form>

	<hr style="margin:40px 0;">

	<h3>리뷰 목록</h3>
	<table class="review-table-fixed">
		<thead>
			<tr><th>작성자</th><th>평점</th><th>내용</th><th>작성일</th><th>관리</th></tr>
		</thead>
		<tbody>
			<c:forEach var="review" items="${reviewList}">
				<tr id="review-${review.reviewNo}">
					<td style="text-align:center;">${review.memberNickname}</td>
					<td style="text-align:center;">
						<c:forEach begin="1" end="${review.reviewRating}">
							<i class="fa-solid fa-star" style="color:gold;"></i>
						</c:forEach>
						<c:forEach begin="${review.reviewRating + 1}" end="5">
							<i class="fa-regular fa-star" style="color:#ccc;"></i>
						</c:forEach>
					</td>
					<td class="review-content">${review.reviewContent}</td>
					<td style="text-align:center;"><fmt:formatDate value="${review.reviewCreatedAt}" pattern="yyyy-MM-dd"/></td>
					<td>
						<c:if test="${sessionScope.loginId eq review.memberId}">
							<button class="btn btn-edit" data-review-no="${review.reviewNo}">수정</button>
							<button class="btn btn-review-delete" data-review-no="${review.reviewNo}">삭제</button>
						</c:if>
					</td>
				</tr>
			</c:forEach>
			<c:if test="${empty reviewList}">
				<tr><td colspan="5" style="text-align:center; color:#999; padding:30px;">아직 등록된 리뷰가 없습니다.</td></tr>
			</c:if>
		</tbody>
	</table>

	<div style="margin-top:30px; text-align:right;">
		<a href="list" class="btn btn-secondary">목록으로 이동</a>
	</div>
</div>
</c:if>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />

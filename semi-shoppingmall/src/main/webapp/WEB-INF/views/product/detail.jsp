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

<%-- (스타일 태그 내용은 동일) --%>
<style>
/* ... (기존 스타일 동일) ... */
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

    // 위시리스트 토글 (동일)
    $("#wishlist-heart").on("click", function() {
        // ... (기존 위시리스트 AJAX 코드)
    });

    // ✨✨✨ [수정] 장바구니 담기 (SKU 방식) ✨✨✨
    $("#addToCartBtn").on("click", function() {
        // 1. SKU(옵션 조합)의 optionNo를 가져옴
        var optionNo = $("#sku-selector").val(); 
        var quantity = $("#cartQuantity").val();

        // 2. optionNo가 선택되었는지 확인 (빈 값 "" 체크)
        if (!optionNo || optionNo === "") { 
            alert("옵션을 선택해주세요.");
            return;
        }
        if (quantity < 1 || isNaN(quantity)) {
            alert("수량을 1개 이상 입력해주세요.");
            $("#cartQuantity").val(1);
            return;
        }

        // 3. AJAX 요청 (optionNo 전송)
        $.ajax({
            url: "${pageContext.request.contextPath}/rest/cart/add",
            method: "post",
            data: {
                productNo: productNo,
                optionNo: optionNo, // ✨ color, size 대신 optionNo 전달
                cartAmount: quantity
            },
            success: function(response) {
                // (CartRestController가 Map<String, Object>를 반환한다고 가정)
                if (response.result === true) {
                    alert("장바구니에 상품을 담았습니다!");
                } else {
                    alert("장바구니 추가 실패: " + (response.error || ""));
                }
            },
            error: function(xhr) {
                if (xhr.status === 401) alert("로그인이 필요합니다.");
                else alert("장바구니 담기 중 오류 발생");
            }
        });
    });
    
    // ... (리뷰 관련 JavaScript 코드는 동일) ...
    // 리뷰 수정
    $(document).on("click", ".btn-edit", function() {
        // ...
    });
    // 수정 완료
    $(document).on("click", ".btn-update", function() {
        // ...
    });
    // 리뷰 삭제
    $(document).on("click", ".btn-delete", function() { // (클래스명 .btn-review-delete 와 일치시키세요)
        // ...
    });
    // 리뷰 등록
    $("#submitReviewBtn").click(function() {
        // ...
    });
});
</script>
</c:if>

<%-- ... (c:if test="${empty product}" 부분은 동일) ... --%>
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
		<%-- ... (상품 썸네일 이미지 표시 부분은 동일) ... --%>
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
			<%-- ... (상품명, 가격, 평점, 예상 도착일, 위시리스트 부분은 동일) ... --%>
            <h2>${product.productName}</h2>
			<h3><fmt:formatNumber value="${product.productPrice}" type="number"/>원</h3>
			<p>평점: 
				<c:if test="${avgRating > 0}"> <%-- ${avgRating} 사용 --%>
					<fmt:formatNumber value="${avgRating}" pattern="0.0"/> / 5.0
				</c:if>
				<c:if test="${avgRating == 0}">평점 없음</c:if>
				(<span id="review-count">${reviewList.size()}</span>개 리뷰)
			</p>
			<div><span class="gray">예상 도착일:</span> <strong class="blue">${estimatedDeliveryDate}</strong></div>
			<p id="wishlist-heart" style="margin-top:5px;">
				<i class="fa-heart ${wishlisted ? 'fa-solid' : 'fa-regular'}" style="color:${wishlisted ? 'red' : 'gray'}; cursor:pointer;"></i>
				<span id="wishlist-count">${wishlistCount}</span>
			</p>
			<hr style="border-top:1px solid #ddd; margin:20px 0;">


			<!-- ✨✨✨ [수정] 옵션 선택 (SKU 방식) ✨✨✨ -->
			<div class="option-select">
				<p><strong>옵션 선택</strong></p>
                <%-- 1. 두 개의 select 대신 하나의 select로 변경 --%>
				<select id="sku-selector" class="field w-100" required>
					<option value="">-- 옵션 조합을 선택하세요 --</option>
                    <%-- 2. colorList/sizeList 대신 ${optionList} (SKU 목록) 반복 --%>
					<c:forEach var="option" items="${optionList}">
                        <c:if test="${option.optionStock > 0}">
    						<option value="${option.optionNo}"> <%-- 3. value에 SKU의 optionNo --%>
                                ${option.optionName} <%-- 4. 표시에 SKU의 optionName (예: "S / 치즈") --%>
                                (재고: ${option.optionStock}개)
                            </option>
                        </c:if>
                        <c:if test="${option.optionStock <= 0}">
    						<option value="${option.optionNo}" disabled>
                                ${option.optionName} (품절)
                            </option>
                        </c:if>
					</c:forEach>
				</select>
			</div>
            <%-- ✨✨✨ 옵션 선택 수정 끝 ✨✨✨ --%>

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

	<%-- ... (상품 상세 설명, 리뷰 작성 폼, 리뷰 목록, 목록으로 이동 버튼 등은 동일) ... --%>
	<hr style="border-top:3px solid #eee; margin:40px 0;">
	<h3>상품 상세 설명</h3>
	<div class="content-display" style="min-height:200px; padding:20px 0; line-height:1.8;">
		${product.productContent}
	</div>
    <%-- ... (리뷰 폼, 리뷰 목록 등) ... --%>

</div>
</c:if>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />


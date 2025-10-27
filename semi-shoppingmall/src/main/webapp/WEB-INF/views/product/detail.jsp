<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<style>
/* 리뷰 테이블의 테두리를 명확하게 표시하고 너비 비율을 고정 */
.review-table-fixed {
	table-layout: fixed;
	border-collapse: collapse;
}

/* 모든 셀에 테두리 적용 */
.review-table-fixed th, .review-table-fixed td {
	border: 1px solid #ccc;
	padding: 8px;
	vertical-align: top;
}

/* 버튼이 들어가는 마지막 열의 너비를 고정 */
.review-table-fixed tbody tr td:last-child {
	width: 120px;
	text-align: center;
	white-space: nowrap;
}

/* 리뷰 내용 셀이 남은 공간을 모두 차지하도록 설정 */
.review-table-fixed tbody tr td.review-content {
	width: auto;
}
</style>

<c:if test="${not empty product}">
<script type="text/javascript">
	// productNo를 스크립트 전역 변수로 선언
	var productNo = ${product.productNo};

	$(function() {
		
		// 1. 위시리스트 토글 기능
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
				error : function() {
					alert("로그인이 필요합니다.");
				}
			});
		});

		// 2. 리뷰 수정 모드 진입
		$(document).on("click", ".btn-edit", function() {
			var btn = $(this);
			var tr = btn.closest("tr");
			var contentTd = tr.find("td.review-content");
			var original = contentTd.text().trim();

			if (contentTd.find("textarea").length > 0) return;

			contentTd.html('<textarea class="edit-content form-control" rows="3">' + original + '</textarea>');
			btn.text("완료").removeClass("btn-edit").addClass("btn-update btn-success");
		});

		// 3. 리뷰 수정 완료 (AJAX 전송)
		$(document).on("click", ".btn-update", function() {
			var btn = $(this);
			var tr = btn.closest("tr");
			var reviewNo = btn.data("review-no");
			var newContent = tr.find("textarea.edit-content").val().trim();

			if (!newContent) {
				alert("내용을 입력해주세요.");
				return;
			}

			$.ajax({
				url : "${pageContext.request.contextPath}/rest/review/update",
				type : "post",
				data : {
					reviewNo : reviewNo,
					reviewContent : newContent
				},
				success : function(result) {
					if (result) {
						tr.find("td.review-content").text(newContent);
						btn.text("수정").removeClass("btn-update btn-success").addClass("btn-edit");
					} else {
						alert("리뷰 수정 실패 (서버 문제)");
					}
				},
				error : function(xhr) {
					if (xhr.status === 401) {
						alert("로그인이 필요합니다.");
					} else if (xhr.status === 403) {
						alert("수정 권한이 없습니다.");
					} else if (xhr.status === 404) {
						alert("존재하지 않는 리뷰입니다.");
					} else {
						alert("수정 중 알 수 없는 오류 발생");
					}
				}
			});
		});

		// 4. 리뷰 삭제
		$(document).on("click", ".btn-delete", function() {
			var btn = $(this);
			var reviewNo = btn.data("review-no");
			if (!confirm("정말 삭제하시겠습니까?")) return;

			$.ajax({
				url : "${pageContext.request.contextPath}/rest/review/delete",
				type : "post",
				data : { reviewNo : reviewNo },
				success : function(result) {
					if (result) {
						$('#review-' + reviewNo).remove();
						alert("삭제 완료!");
					} else {
						alert("리뷰 삭제 실패 (서버 문제)");
					}
				},
				error : function(xhr) {
					if (xhr.status === 401) {
						alert("로그인이 필요합니다.");
					} else if (xhr.status === 403) {
						alert("삭제 권한이 없습니다.");
					} else if (xhr.status === 404) {
						alert("해당 리뷰를 찾을 수 없습니다.");
					} else {
						alert("삭제 중 알 수 없는 오류 발생");
					}
				}
			});
		});

		// 5. 리뷰 등록
		$("#submitReviewBtn").click(function() {
			var formData = new FormData($("#reviewForm")[0]);
			formData.append("productNo", productNo);

			if (formData.get("reviewContent").trim() === "") {
				alert("리뷰 내용을 입력해주세요.");
				return;
			}
			if ($("#reviewRatingInput").val() === "0") {
				alert("평점을 선택해주세요.");
				return;
			}

			$.ajax({
				url : "${pageContext.request.contextPath}/rest/review/add",
				type : "post",
				data : formData,
				processData : false,
				contentType : false,
				success : function(result) {
					if (result) {
						alert("리뷰가 등록되었습니다!");
						location.reload();
					} else {
						alert("리뷰 등록 실패");
					}
				},
				error : function() {
					alert("리뷰 등록 중 오류가 발생했습니다.");
				}
			});
		});
		
		// 6. 리뷰 별점 기능 (Star Rating)
		// 마우스 오버 시 채우기
		$("#reviewRatingStars").on("mouseover", ".star-input", function() {
			var currentRating = $(this).data('rating');

			$("#reviewRatingStars .star-input").each(function(index) {
				if ($(this).data('rating') <= currentRating) {
					$(this).removeClass("fa-regular").addClass("fa-solid").css("color", "orange");
				} else {
					$(this).removeClass("fa-solid").addClass("fa-regular").css("color", "#ccc");
				}
			});
		});

		// 마우스 아웃 시 선택된 점수만 유지
		$("#reviewRatingStars").on("mouseout", function() {
			var selectedRating = $("#reviewRatingInput").val();

			$("#reviewRatingStars .star-input").each(function() {
				if ($(this).data('rating') <= selectedRating) {
					$(this).removeClass("fa-regular").addClass("fa-solid").css("color", "orange");
				} else {
					$(this).removeClass("fa-solid").addClass("fa-regular").css("color", "#ccc");
				}
			});
		});

		// 클릭 시 점수 확정 및 Hidden Input 업데이트
		$("#reviewRatingStars").on("click", ".star-input", function() {
			var selectedRating = $(this).data('rating');
			$("#reviewRatingInput").val(selectedRating);

			$("#reviewRatingStars .star-input").each(function() {
				if ($(this).data('rating') <= selectedRating) {
					$(this).removeClass("fa-regular").addClass("fa-solid").css("color", "orange");
				} else {
					$(this).removeClass("fa-solid").addClass("fa-regular").css("color", "#ccc");
				}
			});
		});
		
		// 7. 장바구니 담기 기능
        $("#addToCartBtn").on("click", function() {
            var quantity = $("#cartQuantity").val();
             
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
                    cartAmount: quantity 
                },
                success: function(response) {
                    alert("장바구니에 상품을 담았습니다.");
                },
                error: function(xhr) {
                    if (xhr.status === 401) {
                        alert("로그인이 필요합니다.");
                    } else {
                        alert("장바구니 담기 중 오류 발생");
                    }
                }
            });
        });
	});
</script>
</c:if>

<c:if test="${empty product}">
    <div class="container w-800" style="text-align: center; padding: 50px;">
        <h1>상품 정보를 찾을 수 없습니다.</h1>
        <p>요청하신 상품 번호에 해당하는 상품이 존재하지 않거나, 잘못된 접근입니다.</p>
        <a href="list" class="btn btn-secondary">상품 목록으로 돌아가기</a>
    </div>
</c:if>

<%--  product 정보 표시 영역: product가 있을 때만 표시되어야 함  --%>
<c:if test="${not empty product}">
<div class="container w-1100">
	<h1>상품 상세정보</h1>

	<div style="display: flex; gap: 30px;">
        <div style="flex-shrink: 0;">
            <c:choose>
                <c:when test="${product.productThumbnailNo != null}">
                    <img src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${product.productThumbnailNo}"
                         width="300" height="300" style="object-fit: cover; border: 1px solid #ddd;">
                </c:when>
                <c:otherwise>
                    <div style="width:300px; height:300px; background:#f0f0f0; display:flex; justify-content:center; align-items:center;">
                        이미지 없음
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <div style="flex-grow: 1;">
            <h2>${product.productName}</h2>
            <h3><fmt:formatNumber value="${product.productPrice}" type="number"/>원</h3>
            
            <p>
                평점: 
                <c:if test="${product.productAvgRating > 0}">
                    <fmt:formatNumber value="${product.productAvgRating}" pattern="0.0" /> / 5.0
                </c:if>
                <c:if test="${product.productAvgRating == 0}">평점 없음</c:if>
                (<span id="review-count">${reviewList.size()}</span>개 리뷰)
            </p>
            
            <p id="wishlist-heart">
                위시리스트: 
                <i class="fa-heart fa-2x ${wishlisted ? 'fa-solid' : 'fa-regular'}" style="color: ${wishlisted ? 'red' : 'gray'}; cursor: pointer;"></i>
                (<span id="wishlist-count">${wishlistCount}</span>명)
            </p>

            <hr>

            <div class="option-select">
                <p><strong>옵션 선택</strong></p>
                <select id="option-selector" class="field w-100" required>
                    <option value="" disabled selected>옵션을 선택하세요</option>
                    <c:forEach var="opt" items="${optionList}">
                        <option value="${opt.optionNo}" data-stock="${opt.optionStock}">
                            ${opt.optionName} : ${opt.optionValue}
                        </option>
                    </c:forEach>
                </select>
            </div>
            
            <div style="margin-top: 15px;">
                <label for="cartQuantity">수량 입력</label>
            </div>
            <div style="margin-top: 15px;">
                <input type="number" id="cartQuantity" value="1" min="1" class="field" style="width: 80px;">
            </div>

            <div id="purchase-buttons" style="display:flex; gap: 10px; margin-top: 20px;">
                <button id="addToCartBtn" class="btn btn-cart">장바구니 담기</button>
            </div>
            
        </div>
    </div>
    
	<hr>

	<h3>상품 상세 설명</h3>
	<div class="content-display">
	    ${product.productContent}
	</div>
	
	<hr>

	<h3>리뷰 작성</h3>
	<form id="reviewForm" method="post" style="margin-bottom: 30px;">
	    <input type="hidden" name="productNo" value="${product.productNo}">
	    <input type="hidden" id="reviewRatingInput" name="reviewRating" value="0">
	    
	    <div id="reviewRatingStars" style="font-size: 24px; margin-bottom: 10px;">
	        <i class="fa-regular fa-star star-input" data-rating="1" style="color:#ccc;"></i>
	        <i class="fa-regular fa-star star-input" data-rating="2" style="color:#ccc;"></i>
	        <i class="fa-regular fa-star star-input" data-rating="3" style="color:#ccc;"></i>
	        <i class="fa-regular fa-star star-input" data-rating="4" style="color:#ccc;"></i>
	        <i class="fa-regular fa-star star-input" data-rating="5" style="color:#ccc;"></i>
	    </div>
	    
	    <textarea name="reviewContent" class="form-control" rows="3" placeholder="리뷰 내용을 입력해주세요." style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 5px;"></textarea>
	    <button type="button" id="submitReviewBtn" class="btn btn-positive" style="margin-top: 10px;">리뷰 등록</button>
	</form>

	<hr>

	<h3>리뷰 목록</h3>
	<table class="w-100 review-table-fixed">
		<thead>
			<tr>
				<th width="15%">작성자</th>
				<th width="15%">평점</th>
				<th width="55%">내용</th>
				<th width="10%">작성일</th>
				<th width="10%">관리</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="review" items="${reviewList}">
				<tr id="review-${review.reviewNo}">
					<td>${review.memberNickname}</td>
					<td>
						<c:forEach begin="1" end="${review.reviewRating}">
							<i class="fa-solid fa-star" style="color: gold;"></i>
						</c:forEach>
						<c:forEach begin="${review.reviewRating + 1}" end="5">
							<i class="fa-regular fa-star"></i>
						</c:forEach>
					</td>
					<td class="review-content">${review.reviewContent}</td>
					<td><fmt:formatDate value="${review.reviewCreatedAt}" pattern="yyyy-MM-dd" /></td>
					<td>
					    <%-- 리뷰 작성자만 보이도록 권한 처리 필요 --%>
					    <c:if test="${sessionScope.loginId eq review.memberId}"> 
					        <button class="btn btn-edit" data-review-no="${review.reviewNo}">수정</button>
						    <button class="btn btn-delete" data-review-no="${review.reviewNo}">삭제</button>
					    </c:if>
					</td>
				</tr>
			</c:forEach>
			<c:if test="${empty reviewList}">
                <tr><td colspan="5" style="text-align:center;">등록된 리뷰가 없습니다.</td></tr>
            </c:if>
		</tbody>
	</table>

	<div style="margin-top: 20px;">
		<a href="list" class="btn btn-secondary">목록으로 이동</a>
	</div>
</div>
</c:if>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
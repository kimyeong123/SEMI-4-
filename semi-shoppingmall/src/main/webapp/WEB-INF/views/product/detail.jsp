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
h1 {
	font-size: 1.8em;
	padding-bottom: 10px;
	margin-bottom: 30px;
	color: #333;
	border-bottom: 1px solid #ddd;
}
h2 { color: #222; font-size: 1.8em; margin-bottom: 10px;}
h3 { color: #444; font-size: 1.5em; margin-top: 30px; margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 5px; }


/* === 입력 필드 및 폼 컨트롤 === */
.field, .form-control {
	padding: 10px;
	border: 1px solid #ccc;
	border-radius: 0; /* 네모난 형태로 변경 */
	box-sizing: border-box;
	width: 100%; /* form-control 대체 */
}
.w-100 { width: 100%; }
.gray { color: #666; }
.blue { color: #3498db; }


.btn {
	padding: 10px 20px;
	border-radius: 0; /* 네모난 형태로 변경 */
	cursor: pointer;
	font-weight: normal;	
	transition: background-color 0.2s, color 0.2s, border-color 0.2s;
	text-decoration: none;
	display: inline-block;
	text-align: center;
	border: 1px solid;
	font-size: 1em;
}
.btn-edit, .btn-success {	
	padding: 5px 10px;	
	font-size: 0.85em;	
	border-color: #888;	
	color: #555;	
	background-color: transparent;	
}	
.btn-edit:hover, .btn-success:hover { background-color: #f0f0f0; color: #333; }

.btn-review-delete {	
	padding: 5px 10px;	
	font-size: 0.85em;	
	border: 1px solid #c00; 
	color: white;	
	background-color: #d9534f; /* 밝은 빨강 */
    transition: background-color 0.2s, color 0.2s, border-color 0.2s, filter 0.2s;
}	
.btn-review-delete:hover { 
    background-color: #c9302c; /* 호버 시 진한 빨강 */
    filter: none; /* filter: brightness(0.9)가 적용되지 않도록 함 */
}

/* 2. Secondary Action (장바구니) - 중간 톤 */
.btn-cart { border-color: #666; color: #333; background-color: #ddd; font-size: 1.1em; flex-grow: 1; }	
.btn-cart:hover { background-color: #bbb; border-color: #555; }
.btn-cart:disabled { opacity: 0.6; cursor: not-allowed; }

/* 3. Neutral/Secondary (목록으로 이동) - 밝은 톤 */
.btn-secondary {	
	border-color: #aaa;	
	color: #555;	
	background-color: #f5f5f5;	
	padding: 10px 20px;
}
.btn-secondary:hover {	
	background-color: #eee;	
	border-color: #888;	
}

/* 4. Small Utility Buttons (리뷰 수정/삭제) - 테두리만 */
.btn-edit, .btn-success {	
	padding: 5px 10px;	
	font-size: 0.85em;	
	border-color: #888;	
	color: #555;	
	background-color: transparent;	
}	
.btn-edit:hover, .btn-success:hover { background-color: #f0f0f0; color: #333; }

.btn-delete {	
	padding: 5px 10px;	
	font-size: 0.85em;	
	border-color: #888;	
	color: #888;	
	background-color: transparent;	
}	
.btn-delete:hover { background-color: #f0f0f0; color: #333; }


/* === 리뷰 테이블 스타일 === */
.review-table-fixed {
	table-layout: fixed;
	border-collapse: collapse;
	width: 100%;
	margin-top: 10px;
}
.review-table-fixed th, .review-table-fixed td {
	border: 1px solid #ccc; 
	padding: 12px 10px;
	vertical-align: middle;
}
.review-table-fixed th {
	background-color: #f8f8f8; 
	text-align: center;
	color: #495057;
}

/* 버튼이 들어가는 관리 열 너비 고정 */
.review-table-fixed tbody tr td:last-child {
	width: 140px;	
	text-align: center;
	white-space: nowrap;
}

/* 리뷰 내용 셀이 남은 공간을 모두 차지 */
.review-table-fixed tbody tr td.review-content {
	width: auto;
	word-break: break-all;
	white-space: pre-wrap;
}

/* 평점, 작성일, 작성자 열 너비 지정 */
.review-table-fixed thead tr th:nth-child(1) { width: 12%; } /* 작성자 */
.review-table-fixed thead tr th:nth-child(2) { width: 15%; } /* 평점 */
.review-table-fixed thead tr th:nth-child(4) { width: 12%; } /* 작성일 */
.review-table-fixed thead tr th:nth-child(5) { width: 11%; } /* 관리 */
.review-table-fixed thead tr th:nth-child(3) { width: 50%; } /* 내용 (남은 공간) */
</style>

<c:if test="${not empty product}">
<script type="text/javascript">
	// productNo를 스크립트 전역 변수로 선언
	var productNo = ${product.productNo};

	$(function() {
        
        // ===================================================
        // 0. Summernote 에디터 초기화 및 이미지 업로드 (게시판 코드 병합)
        // ===================================================
        $(".summernote-editor").summernote({
            height: 250,
            minHeight: 200,
            maxHeight: 400,

            placeholder: "상품에 대한 솔직한 리뷰를 입력해주세요. (타인에 대한 무분별한 비방 시 예고 없이 삭제될 수 있습니다)",

            toolbar: [
                ["font", ["style", "fontname", "fontsize", "forecolor", "backcolor"]],
                ["style", ["bold", "italic", "underline", "strikethrough"]],
                ["attach", ["picture"]],
                ["tool", ["ol", "ul", "table", "hr", "fullscreen"]]
            ],
            
            callbacks : {
                onImageUpload : function(files) {
                    console.log("리뷰 이미지 파일 업로드 시도중...");
                    
                    var form = new FormData();
                    for(var i=0; i < files.length; i++) {
                        form.append("attach", files[i]);
                    }
                    
                    $.ajax({
                        processData:false,
                        contentType:false,
                        url:"/rest/csBoard/temps", // ★ 리뷰 이미지 저장 REST URL로 변경 권장 ★
                        method:"post",
                        data:form,
                        success:function(response){ // response == List<Integer>
                            for(var i=0; i < response.length; i++) {
                                var img = $("<img>").attr("src", "${pageContext.request.contextPath}/attachment/view?attachmentNo="+response[i])
                                                    .attr("data-pk", response[i])
                                                    .addClass("custom-image");
                                $(".summernote-editor").summernote("insertNode", img[0]);
                            }
                        },
                        error: function() {
                             alert("이미지 업로드에 실패했습니다. (URL 확인 필요)");
                        }
                    });
                }
            }
        });
        // ===================================================

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
					// 응답에서 받은 count를 업데이트
					$("#wishlist-count").text(response.count);
				},
				error : function() {
					alert("로그인이 필요합니다.");
				}
			});
		});
		
		// 초기 위시리스트 상태 설정 (페이지 로드 시)
		if ('${wishlisted}' === 'true') {
		    $("#wishlist-heart i").css("color", "red");
		} else {
		    $("#wishlist-heart i").css("color", "gray");
		}


		// 2. 리뷰 수정 모드 진입
		$(document).on("click", ".btn-edit", function() {
			var btn = $(this);
			var tr = btn.closest("tr");
			var contentTd = tr.find("td.review-content");
			var original = contentTd.text().trim();

			if (contentTd.find("textarea").length > 0) return;

			contentTd.html('<textarea class="edit-content form-control" rows="3" style="width:100%;">' + original + '</textarea>');
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
						alert("리뷰 수정 완료!");
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
		$(document).on("click", ".btn-review-delete", function() {
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
						location.reload(); // 삭제 후 목록 새로고침
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

		// 5. 리뷰 등록 (Summernote 내용을 FormData에 담아 전송하도록 수정)
		$("#submitReviewBtn").click(function() {
            
            // Summernote 에디터의 HTML 내용을 가져와 <textarea>에 업데이트
            var reviewContent = $('.summernote-editor').summernote('code').trim();
            // 폼 데이터에 최종적으로 포함될 수 있도록 <textarea>의 값을 갱신
            $("#reviewForm").find("textarea[name='reviewContent']").val(reviewContent);

			var formData = new FormData($("#reviewForm")[0]);
			formData.append("productNo", productNo); // productNo는 이미 폼에 hidden으로 있지만, 혹시 몰라 한 번 더 추가
            
            // Summernote 공백/태그만 있는 경우 체크
			if (reviewContent === "" || reviewContent === "<p><br></p>" || reviewContent.replace(/<[^>]*>/g, '').trim() === "") {
				alert("리뷰 내용을 입력해주세요.");
				return;
			}
			if ($("#reviewRatingInput").val() === "0") {
				alert("평점을 선택해주세요.");
				return;
			}
			
			// 옵션 미선택 시 경고
			var optionNo = $("#option-selector").val();
			if (!optionNo) {
				alert("구매한 옵션을 선택해주세요.");
				return;
			}
			formData.append("optionNo", optionNo); // 폼에 optionNo 추가

			$.ajax({
				url : "${pageContext.request.contextPath}/rest/review/add",
				type : "post",
				data : formData,
				processData : false, // FormData 사용 시 필수
				contentType : false, // FormData 사용 시 필수
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
			var optionNo = $("#option-selector").val();
			
			if(!optionNo) {
				alert("옵션을 선택해주세요.");
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
					optionNo: optionNo, /* 옵션 번호 추가 */
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
		
		// 8. 옵션 선택에 따른 수량/재고 제한 및 버튼 활성화
		$("#option-selector").on("change", function() {
			const selectedOption = $(this).find('option:selected');
			const stock = selectedOption.data('stock');
			
			if (stock === undefined || selectedOption.val() === "") { // "옵션을 선택하세요"를 선택한 경우
				$("#cartQuantity").val(1).attr("max", 999);
				$("#addToCartBtn").prop('disabled', true);
				return;
			}
			
			$("#cartQuantity").attr({
				"max": stock,
				"value": 1
			});
			
			if (stock <= 0) {
				 alert("선택하신 옵션은 재고가 부족합니다.");
				 $("#addToCartBtn").prop('disabled', true);
			} else {
				 $("#addToCartBtn").prop('disabled', false);
			}
		}).trigger('change'); // 페이지 로드 시 초기 옵션 재고 확인 (기본값 '옵션을 선택하세요'에 맞게 버튼 비활성화)
	});
</script>
</c:if>

<c:if test="${empty product}">
	<div class="container" style="text-align: center; padding: 50px;">
		<h1>상품 정보를 찾을 수 없습니다.</h1>
		<p>요청하신 상품 번호에 해당하는 상품이 존재하지 않거나, 잘못된 접근입니다.</p>
		<a href="list" class="btn btn-secondary" style="margin-top: 20px;">상품 목록으로 돌아가기</a>
	</div>
</c:if>

<c:if test="${not empty product}">
<div class="container">
	<h1>상품 상세정보</h1>

	<div style="display: flex; gap: 30px;">
		<div style="flex-shrink: 0; width: 300px;">
			<c:choose>
				<c:when test="${product.productThumbnailNo != null}">
					<img src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${product.productThumbnailNo}"
						width="300" height="300" style="object-fit: cover; border: 1px solid #ddd;">
				</c:when>
				<c:otherwise>
					<div style="width:300px; height:300px; background:#f0f0f0; display:flex; justify-content:center; align-items:center; color:#999;">
						상품 이미지 없음
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
			
			<div>
				<span class="gray">예상 도착일:</span>
				<strong class="blue">${estimatedDeliveryDate}</strong>
			</div>
			
			<p id="wishlist-heart" style="margin-top: 5px;">
				<i class="fa-heart ${wishlisted ? 'fa-solid' : 'fa-regular'}" style="color: ${wishlisted ? 'red' : 'gray'}; cursor: pointer;"></i>
				<span id="wishlist-count">${wishlistCount}</span>
			</p>

			<hr style="border-top: 1px solid #ddd; margin: 20px 0;">

			<div class="option-select">
				<p><strong>옵션 선택</strong></p>
				<select id="option-selector" class="field w-100" required>
					<option value="" disabled selected>옵션을 선택하세요</option>
					<c:forEach var="opt" items="${optionList}">
						<option value="${opt.optionNo}" data-stock="${opt.optionStock}">
							${opt.optionName} : ${opt.optionValue} (재고: ${opt.optionStock})
						</option>
					</c:forEach>
				</select>
			</div>
			
			<div style="margin-top: 15px;">
				<label for="cartQuantity">수량</label>
			</div>
			<div style="margin-top: 5px;">
				<input type="number" id="cartQuantity" value="1" min="1" class="field" style="width: 80px;">
			</div>

			<div id="purchase-buttons" style="display:flex; gap: 10px; margin-top: 25px;">
				<button id="addToCartBtn" class="btn btn-cart" disabled>장바구니 담기</button>
			</div>
			
		</div>
	</div>
	
	<hr style="border-top: 3px solid #eee; margin: 40px 0;">

	<h3>상품 상세 설명</h3>
	<div class="content-display" style="min-height: 200px; padding: 20px 0; line-height: 1.8;">
		${product.productContent}
	</div>
	
	<hr style="border-top: 3px solid #eee; margin: 40px 0;">

	<h3>리뷰 작성</h3>
	<form id="reviewForm" method="post" style="padding: 20px; border: 1px solid #eee; margin-bottom: 30px; background: white;">
		<input type="hidden" name="productNo" value="${product.productNo}">
		<input type="hidden" id="reviewRatingInput" name="reviewRating" value="0">
		
		<div id="reviewRatingStars" style="font-size: 28px; margin-bottom: 10px;">
			<i class="fa-regular fa-star star-input" data-rating="1" style="color:#ccc;"></i>
			<i class="fa-regular fa-star star-input" data-rating="2" style="color:#ccc;"></i>
			<i class="fa-regular fa-star star-input" data-rating="3" style="color:#ccc;"></i>
			<i class="fa-regular fa-star star-input" data-rating="4" style="color:#ccc;"></i>
			<i class="fa-regular fa-star star-input" data-rating="5" style="color:#ccc;"></i>
		</div>
		<textarea name="reviewContent" class="form-control summernote-editor" rows="4" placeholder="상품에 대한 솔직한 리뷰를 입력해주세요. (옵션을 선택해야 등록 가능)" style="width: 100%; margin-bottom: 10px;"></textarea>
		<button type="button" id="submitReviewBtn" class="btn btn-black">리뷰 등록</button>
	</form>

	<hr style="margin: 40px 0;">

	<h3>리뷰 목록</h3>
	<table class="review-table-fixed">
		<thead>
			<tr>
				<th>작성자</th>
				<th>평점</th>
				<th>내용</th>
				<th>작성일</th>
				<th>관리</th>
			</tr>
		</thead>
		<tbody>
    <c:forEach var="review" items="${reviewList}">
        <tr id="review-${review.reviewNo}">
            <td style="text-align: center;">
            	${review.memberNickname}
            </td> 
            <td style="text-align: center;"> 
                <c:forEach begin="1" end="${review.reviewRating}">
                    <i class="fa-solid fa-star" style="color: gold;"></i>
                </c:forEach>
                <c:forEach begin="${review.reviewRating + 1}" end="5">
                    <i class="fa-regular fa-star" style="color: #ccc;"></i>
                </c:forEach>
            </td>
            <td class="review-content">${review.reviewContent}</td> 
            <td style="text-align: center;"><fmt:formatDate value="${review.reviewCreatedAt}" pattern="yyyy-MM-dd" /></td>
            <td>
				<c:if test="${sessionScope.loginId eq review.memberId}">	
					<button class="btn btn-edit" data-review-no="${review.reviewNo}">수정</button>
					<button class="btn btn-review-delete" data-review-no="${review.reviewNo}">삭제</button>
				</c:if>
			</td>
        </tr>
    </c:forEach>
    <c:if test="${empty reviewList}">
        <tr><td colspan="5" style="text-align:center; color:#999; padding: 30px;">아직 등록된 리뷰가 없습니다. 첫 리뷰를 작성해보세요!</td></tr>
    </c:if>
</tbody>
	</table>

	<div style="margin-top: 30px; text-align: right;">
		<a href="list" class="btn btn-secondary">목록으로 이동</a>
	</div>
</div>
</c:if>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
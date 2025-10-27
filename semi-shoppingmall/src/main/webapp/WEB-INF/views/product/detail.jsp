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
	table-layout: fixed; /* 테이블 너비 고정 */
	border-collapse: collapse; /* 셀 경계선을 하나로 합침 */
}

/* 모든 셀에 테두리 적용 (CSS 클래스 로드 실패 시 대체) */
.review-table-fixed th, .review-table-fixed td {
	border: 1px solid #ccc;
	padding: 8px;
	vertical-align: top; /* 내용이 위쪽으로 정렬되도록 */
}

/* 버튼이 들어가는 마지막 열의 너비를 고정 (수정/삭제 버튼 열) */
.review-table-fixed tbody tr td:last-child {
	width: 120px; /* 버튼 2개 + 패딩에 충분한 너비 */
	text-align: center;
	white-space: nowrap; /* 버튼이 줄바꿈되지 않도록 */
}

/* 리뷰 내용 셀이 남은 공간을 모두 차지하도록 설정 */
.review-table-fixed tbody tr td.review-content {
	width: auto;
}
</style>
<script type="text/javascript">
	// productNo를 스크립트 전역 변수로 선언
	var productNo = ${product.productNo};

	$(function() {
		
		// ===================================
		// 1. 위시리스트 토글 기능
		// ===================================
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

		// ===================================
		// 2. 리뷰 수정 모드 진입
		// ===================================
		$(document).on("click", ".btn-edit", function() {
			var btn = $(this);
			var tr = btn.closest("tr");
			var contentTd = tr.find("td.review-content");
			var original = contentTd.text().trim();

			if (contentTd.find("textarea").length > 0) return; // 이미 수정 중이면 패스

			// textarea로 변경하고 기존 내용 삽입
			contentTd.html('<textarea class="edit-content form-control" rows="3">' + original + '</textarea>');
			// 버튼을 '완료'로 변경
			btn.text("완료").removeClass("btn-edit").addClass("btn-update btn-success");
		});

		// ===================================
		// 3. 리뷰 수정 완료 (AJAX 전송)
		// ===================================
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
						// 성공 시: textarea를 텍스트로 복구
						tr.find("td.review-content").text(newContent);
						// 버튼을 '수정'으로 복구
						btn.text("수정").removeClass("btn-update btn-success").addClass("btn-edit");
					} else {
						alert("리뷰 수정 실패 (서버 문제)");
					}
				},
				error : function(xhr) {
					// RestController의 예외 처리 확인
					if (xhr.status === 401) { // UnauthorizationException
						alert("로그인이 필요합니다.");
					} else if (xhr.status === 403) { // NeedPermissionException
						alert("수정 권한이 없습니다.");
					} else if (xhr.status === 404) { // TargetNotfoundException
						alert("존재하지 않는 리뷰입니다.");
					} else {
						alert("수정 중 알 수 없는 오류 발생");
					}
				}
			});
		});

		// ===================================
		// 4. 리뷰 삭제
		// ===================================
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
						// 성공 시: 해당 리뷰 행을 제거
						$('#review-' + reviewNo).remove();
						alert("삭제 완료!");
					} else {
						alert("리뷰 삭제 실패 (서버 문제)");
					}
				},
				error : function(xhr) {
					// RestController의 예외 처리 확인
					if (xhr.status === 401) { // UnauthorizationException
						alert("로그인이 필요합니다.");
					} else if (xhr.status === 403) { // NeedPermissionException
						alert("삭제 권한이 없습니다.");
					} else if (xhr.status === 404) { // TargetNotfoundException
						alert("해당 리뷰를 찾을 수 없습니다.");
					} else {
						alert("삭제 중 알 수 없는 오류 발생");
					}
				}
			});
		});

		// ===================================
		// 5. 리뷰 등록
		// ===================================
		$("#submitReviewBtn").click(function() {
			var formData = new FormData($("#reviewForm")[0]);

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
				processData : false, // FormData 전송 시 필수
				contentType : false, // FormData 전송 시 필수
				success : function(result) {
					if (result) {
						alert("리뷰가 등록되었습니다!");
						location.reload(); // 새로고침해서 목록 반영
					} else {
						alert("리뷰 등록 실패");
					}
				},
				error : function() {
					// xhr 객체와 상태 코드(status) 체크 없이 단순히 오류 메시지만 표시
					alert("리뷰 등록 중 오류가 발생했습니다.");
				}
			});
		});
		
		// ===================================
		// 6. 리뷰 별점 기능 (Star Rating)
		// ===================================

		// 6-1. 마우스 오버 시 채우기
		$("#reviewRatingStars").on("mouseover", ".star-input", function() {
			var currentRating = $(this).data('rating');

			// 현재 별까지 채우기
			$("#reviewRatingStars .star-input").each(function(index) {
				if ($(this).data('rating') <= currentRating) {
					$(this).removeClass("fa-regular").addClass("fa-solid").css("color", "orange");
				} else {
					$(this).removeClass("fa-solid").addClass("fa-regular").css("color", "#ccc");
				}
			});
		});

		// 6-2. 마우스 아웃 시 선택된 점수만 유지
		$("#reviewRatingStars").on("mouseout", function() {
			var selectedRating = $("#reviewRatingInput").val();

			// 선택된 점수까지 다시 채우기
			$("#reviewRatingStars .star-input").each(function() {
				if ($(this).data('rating') <= selectedRating) {
					$(this).removeClass("fa-regular").addClass("fa-solid").css("color", "orange");
				} else {
					$(this).removeClass("fa-solid").addClass("fa-regular").css("color", "#ccc");
				}
			});
		});

		// 6-3. 클릭 시 점수 확정 및 Hidden Input 업데이트
		$("#reviewRatingStars").on("click", ".star-input", function() {
			var selectedRating = $(this).data('rating');
			$("#reviewRatingInput").val(selectedRating); // hidden input에 평점 저장

			// 클릭한 점수까지 채우기 (확정 색상)
			$("#reviewRatingStars .star-input").each(function() {
				if ($(this).data('rating') <= selectedRating) {
					$(this).removeClass("fa-regular").addClass("fa-solid").css("color", "orange");
				} else {
					$(this).removeClass("fa-solid").addClass("fa-regular").css("color", "#ccc");
				}
			});
		});
		
		// ===================================
		// 7. 장바구니 담기 기능
		// ===================================
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
                    // response는 성공/실패 여부
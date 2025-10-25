<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(function() {
    // 리뷰 수정
    $(document).on("click", ".btn-edit", function() {
        var btn = $(this);
        var tr = btn.closest("tr");
        var contentTd = tr.find("td.review-content");
        var original = contentTd.text().trim();

        if (contentTd.find("textarea").length > 0) return; // 이미 수정 중이면 패스

        contentTd.html('<textarea class="edit-content" rows="3" cols="50">' + original + '</textarea>');
        btn.text("완료").removeClass("btn-edit").addClass("btn-update");
    });

    // 수정 완료
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
            url: "${pageContext.request.contextPath}/rest/review/update",
            type: "post",
            data: {
                reviewNo: reviewNo,
                reviewContent: newContent
            },
            success: function(result) {
                if (result) {
                    tr.find("td.review-content").text(newContent);
                    btn.text("수정").removeClass("btn-update").addClass("btn-edit");
                } else {
                    alert("수정 실패");
                }
            },
            error: function() {
                alert("수정 중 오류 발생");
            }
        });
    });

    // 리뷰 삭제
    $(document).on("click", ".btn-delete", function() {
        var btn = $(this);
        var reviewNo = btn.data("review-no");
        if (!confirm("정말 삭제하시겠습니까?")) return;

        $.ajax({
            url: "${pageContext.request.contextPath}/rest/review/delete",
            type: "post",
            data: { reviewNo: reviewNo },
            success: function(result) {
                if (result) {
                    $('#review-' + reviewNo).remove();
                    alert("삭제 완료!");
                } else {
                    alert("삭제 실패");
                }
            },
            error: function() {
                alert("삭제 중 오류 발생");
            }
        });
    });

    // 리뷰 등록
    $("#submitReviewBtn").click(function() {
        var formData = new FormData($("#reviewForm")[0]);

        $.ajax({
            url: "${pageContext.request.contextPath}/rest/review/add", // ✅ 컨트롤러에 맞게 수정
            type: "post",
            data: formData,
            processData: false,
            contentType: false,
            success: function(result) {
                if (result) {
                    alert("리뷰가 등록되었습니다!");
                    location.reload(); // 새로고침해서 목록 반영
                } else {
                    alert("리뷰 등록 실패");
                }
            },
            error: function(xhr) {
                if (xhr.status === 401) {
                    alert("로그인이 필요합니다.");
                } else {
                    alert("리뷰 등록 중 오류가 발생했습니다.");
                }
            }
        });
    });
});

</script>

<div class="container w-800">
	<h1>상품 상세정보</h1>

	<c:if test="${product.productThumbnailNo != null}">
		<img
			src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${product.productThumbnailNo}"
			width="150" height="150" style="object-fit: cover;">
	</c:if>

	<div id="wishlist-heart"
		style="cursor: pointer; font-size: 24px; display: inline-block; margin-top: 10px;">
		<c:choose>
			<c:when test="${wishlisted}">
				<i class="fa-solid fa-heart" style="color: red;"></i>
			</c:when>
			<c:otherwise>
				<i class="fa-regular fa-heart" style="color: gray;"></i>
			</c:otherwise>
		</c:choose>
		<span id="wishlist-count">${wishlistCount}</span>
	</div>

	<br>
	<br>

	<table border="1" width="100%">
		<tr>
			<th width="25%">번호</th>
			<td>${product.productNo}</td>
		</tr>
		<tr>
			<th>이름</th>
			<td>${product.productName}</td>
		</tr>
		<tr>
			<th>가격</th>
			<td>${product.productPrice}</td>
		</tr>
		<tr>
			<th>설명</th>
			<td>${product.productContent}</td>
		</tr>
		<tr>
			<th>평균 평점</th>
			<td>${product.productAvgRating}</td>
		</tr>
	</table>

	<div style="margin-top: 30px;">
		<h3>리뷰 작성</h3>
		<form id="reviewForm" enctype="multipart/form-data">
			<input type="hidden" name="productNo" value="${product.productNo}">
			<label>평점:</label> <select name="reviewRating">
				<option value="1">1점</option>
				<option value="2">2점</option>
				<option value="3" selected>3점</option>
				<option value="4">4점</option>
				<option value="5">5점</option>
			</select> <br>
			<br> <label>내용:</label><br>
			<textarea name="reviewContent" rows="4" cols="50"
				placeholder="리뷰를 작성해주세요."></textarea>
			<br>
			<br>
			<button type="button" id="submitReviewBtn" class="btn btn-primary">리뷰
				작성</button>
		</form>
	</div>

	<h3>리뷰 목록</h3>
	<table class="w-100 table table-border">
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
					<td>${review.memberNickname}</td>
					<td><c:forEach begin="1" end="${review.reviewRating}">
							<i class="fa-solid fa-star gold"></i>
						</c:forEach> <c:forEach begin="${review.reviewRating + 1}" end="5">
							<i class="fa-regular fa-star"></i>
						</c:forEach></td>
					<td class="review-content">${review.reviewContent}</td>
					<td><fmt:formatDate value="${review.reviewCreatedAt}"
							pattern="yyyy-MM-dd" /></td>
					<td><c:if test="${sessionScope.loginId == review.memberId}">
							<button class="btn-edit" data-review-no="${review.reviewNo}">수정</button>
							<button class="btn-delete" data-review-no="${review.reviewNo}">삭제</button>
						</c:if></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<div style="margin-top: 20px;">
		<a href="list" class="btn btn-secondary">목록으로 이동</a>
	</div>

	<hr>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

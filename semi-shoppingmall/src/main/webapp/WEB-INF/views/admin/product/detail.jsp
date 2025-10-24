<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script type="text/javascript">
$(function() {
    var productNo = ${product.productNo};
    var loginId = '${sessionScope.loginId}'; // 로그인 사용자 ID

    function loadReviews() {
        $.ajax({
            url: '${pageContext.request.contextPath}/rest/review/list',
            method: 'get',
            data: { productNo: productNo },
            success: function(reviews) {
                var html = '';
                if(reviews.length === 0) {
                    html = '<p>아직 등록된 리뷰가 없습니다.</p>';
                } else {
                    reviews.forEach(function(r) {
                        html += '<div id="review-'+r.reviewNo+'" style="border:1px solid #ccc; padding:10px; margin-bottom:10px;">';
                        html += '<p>' + r.memberId + '님 평점: ' + r.reviewRating + '점</p>';
                        html += '<p class="review-content">' + r.reviewContent + '</p>';
                        html += '<p>' + r.createdAt + '</p>';

                        // 로그인한 사용자의 리뷰에만 수정/삭제 버튼 표시
                        if(loginId && loginId === r.memberId) {
                            html += '<button class="btn-edit" data-review-no="'+r.reviewNo+'">수정</button>';
                            html += '<button class="btn-delete" data-review-no="'+r.reviewNo+'">삭제</button>';
                        }

                        html += '</div>';
                    });
                }
                $('#reviews').html(html);
            },
            error: function() {
                $('#reviews').html('<p>리뷰를 불러오지 못했습니다.</p>');
            }
        });
    }

    loadReviews();

    // 리뷰 삭제
    $(document).on('click', '.btn-delete', function() {
        var reviewNo = $(this).data('review-no');
        if(!confirm('정말 삭제하시겠습니까?')) return;

        $.ajax({
            url: '${pageContext.request.contextPath}/rest/review/delete',
            type: 'post',
            data: { reviewNo: reviewNo },
            success: function(result) {
                if(result) {
                    $('#review-' + reviewNo).remove();
                    alert('삭제 완료!');
                } else {
                    alert('삭제 실패');
                }
            },
            error: function() {
                alert('삭제 중 오류 발생');
            }
        });
    });

    // 리뷰 수정
    $(document).on("click", ".btn-edit", function() {
    var btn = $(this);
    var tr = btn.closest("tr");
    var contentTd = tr.find("td.review-content");
    var original = contentTd.text().trim();

    if(contentTd.find("textarea").length > 0) return;

    contentTd.html('<textarea class="edit-content">' + original + '</textarea>');
    btn.text("완료").removeClass("btn-edit").addClass("btn-update");
});

$(document).on("click", ".btn-update", function() {
    var btn = $(this);
    var tr = btn.closest("tr");
    var reviewNo = btn.data("review-no");
    var newContent = tr.find("textarea.edit-content").val();

    $.ajax({
        url: "${pageContext.request.contextPath}/rest/review/update",
        type: "post",
        data: { reviewNo: reviewNo, reviewContent: newContent },
        success: function(result) {
            if(result) {
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
</script>

<div class="container w-800">
	<h1>상품 상세정보</h1>

	<c:if test="${product.productThumbnailNo != null}">
		<img
			src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${product.productThumbnailNo}"
			width="150" height="150" style="object-fit: cover;">
	</c:if>

	<p>${product.productName}를(을)위시리스트에 추가한 사람 수: ${wishlistCount}</p>
	<br> <br>

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

	<h3>리뷰 목록</h3>
	<table class="w-100 table table-border">
		<thead>
			<tr>
				<th>작성자</th>
				<th>평점</th>
				<th>내용</th>
				<th>작성일</th>
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
				</tr>
			</c:forEach>
		</tbody>
	</table>


	<div style="margin-top: 20px;">
		<a href="list" class="btn btn-secondary">목록으로 이동</a> <a
			href="edit?productNo=${product.productNo}" class="btn btn-primary">수정하기</a>

		<form action="${pageContext.request.contextPath}/admin/product/delete"
			method="post" style="display: inline;"
			onsubmit="return confirm('정말 삭제하시겠습니까?');">
			<input type="hidden" name="productNo" value="${product.productNo}">
			<button type="submit" class="btn btn-danger">삭제하기</button>
		</form>
	</div>

	<hr>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<div class="container w-800">
	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script type="text/javascript">
    const productNo = ${product.productNo}; // JS 변수 선언

    $(function() {
        $("#submitReviewBtn").on("click", function() {
            const form = $("#reviewForm")[0];
            const content = form.reviewContent.value.trim();
            if(!content) {
                alert("리뷰 내용을 입력해주세요.");
                return;
            }

            const formData = new FormData(form);

            fetch('${pageContext.request.contextPath}/rest/review/add', {
                method: 'POST',
                body: formData
            })
            .then(res => res.json())
            .then(result => {
                if(result) {
                    alert("리뷰 등록 완료!");
                    location.reload();
                } else {
                    alert("등록 실패");
                }
            })
            .catch(err => {
                console.error(err);
                alert("오류가 발생했습니다.");
            });
        });

        $("#wishlistBtn").on("click", function() {
            $.ajax({
                url: "${pageContext.request.contextPath}/rest/wishlist/toggle",
                method: "post",
                data: { productNo: productNo },
                success: function(response) {
                    if(response.wishlisted) {
                        $("#wishlistBtn i").removeClass("fa-regular").addClass("fa-solid");
                    } else {
                        $("#wishlistBtn i").removeClass("fa-solid").addClass("fa-regular");
                    }
                    $("#wishlist-count").text(response.count);
                },
                error: function() {
                    alert("로그인이 필요합니다.");
                }
            });
        });
    });
</script>
	<h1>상품 상세정보</h1>

	<!-- 썸네일 -->
	<c:if test="${product.productThumbnailNo != null}">
		<img
			src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${product.productThumbnailNo}"
			width="150" height="150" style="object-fit: cover;">
	</c:if>
	<button type="button" id="wishlistBtn" class="btn btn-outline-danger">
		<i class="fa-regular fa-heart red"></i> <span id="wishlist-count">0</span>
	</button>
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
			<td>${reviewService.selectAverageRating(product.productNo)}</td>
		</tr>
	</table>

	<!-- ================= 리뷰 작성 폼 ================= -->
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

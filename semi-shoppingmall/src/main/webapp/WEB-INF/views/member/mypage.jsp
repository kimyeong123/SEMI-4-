<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<style>
.profile-wrapper {
	width: 200px;
	height: 200px;
	position: relative;
	border-radius: 50%;
	overflow: hidden;
}

.profile-wrapper>img {
	width: 100%;
	height: 100%;
}

.profile-wrapper>label {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.3);
	color: white;
	display: none;
}

.profile-wrapper:hover>label {
	display: flex;
}
</style>

<!-- 프로필 변경 기능 코드 -->
<script type="text/javascript">
	$(function() {
		var origin = $(".image-profile").attr("src");
		$("#profile-input").on("input", function() {
			var list = $("#profile-input").prop("files");
			if (list.length == 0)
				return;
			var form = new FormData();
			form.append("attach", list[0]);
			$.ajax({
				processData : false,
				contentType : false,
				url : "/rest/member/profile",
				method : "post",
				data : form,
				success : function() {
					var newOrigin = origin + "&t=" + new Date().getTime();
					$(".image-profile").attr("src", newOrigin);
				}
			});
		});

		$(".profile-delete-btn").on("click", function() {
			if (!confirm("정말 삭제하시겠습니까?\n삭제 후 복구할 수 없습니다"))
				return;
			$.ajax({
				url : "/rest/member/delete",
				method : "post",
				success : function() {
					var newOrigin = origin + "&t=" + new Date().getTime();
					$(".image-profile").attr("src", newOrigin);
				}
			});
		});
	});
</script>

<script>
	// 리뷰 삭제/수정 기능
	$(document).on("click", ".btn-delete", function() {
		if (!confirm("정말 삭제하시겠습니까?"))
			return;

		var btn = $(this);
		var reviewNo = btn.data("review-no");

		$.ajax({
			url : "${pageContext.request.contextPath}/rest/review/delete",
			type : "post",
			data : {
				reviewNo : reviewNo
			},
			success : function(result) {
				if (result) {
					btn.closest("tr").remove();
					alert("삭제 완료!");
				} else {
					alert("삭제 실패");
				}
			},
			error : function() {
				alert("삭제 중 오류가 발생했습니다.");
			}
		});
	});
	$(document).on(
			"click",
			".btn-edit",
			function() {
				var btn = $(this);
				var tr = btn.closest("tr");
				var contentTd = tr.find("td").eq(2);
				var original = contentTd.text().trim();

				if (contentTd.find("textarea").length > 0)
					return;

				contentTd.html('<textarea class="edit-content">' + original
						+ '</textarea>');
				btn.text("완료").removeClass("btn-edit").addClass("btn-update");
			});
	$(document)
			.on(
					"click",
					".btn-update",
					function() {
						var btn = $(this);
						var tr = btn.closest("tr");
						var reviewNo = btn.data("review-no");
						var newContent = tr.find("textarea.edit-content").val();

						$
								.ajax({
									url : "${pageContext.request.contextPath}/rest/review/update",
									type : "post",
									data : {
										reviewNo : reviewNo,
										reviewContent : newContent
									},
									success : function(result) {
										if (result) {
											tr.find("td").eq(2)
													.text(newContent);
											btn.text("수정").removeClass(
													"btn-update").addClass(
													"btn-edit");
										} else {
											alert("수정 실패");
										}
									},
									error : function() {
										alert("수정 중 오류 발생");
									}
								});
					});
</script>

<div class="container w-600">
	<div class = "cell center">
		<h2>${memberDto.memberId}님의 정보</h2>
	</div>
	<div class = "cell">
		<table class="w-100 table table-border">
			<tr>
				<th>닉네임</th>
				<td>${memberDto.memberNickname}</td>
			</tr>
			<tr>
				<th>이미지</th>
				<td>
					<div class="profile-wrapper">
						<img class="image-profile"  src = "/member/profile?memberId=${memberDto.memberId }" >
						<label for="profile-input" class="flex-box flex-center">변경</label>
						<input type="file" id="profile-input" style="display:none">
						<!-- <button type ="button" class="profile-change-btn">변경</button> -->
					</div>
					<label class="profile-delete-btn red">
						<i class="fa-solid fa-xmark"></i>
						<span>삭제</span>
					</label>
					<!-- <input type = "file" id="profile-input" style="display:none"> -->
					<br><br>
				</td>
			</tr>
			<tr>
				<th>이메일</th>
				<td>${memberDto.memberEmail}</td>
			</tr>
			<tr>
				<th>생년월일</th>
				<td>${memberDto.memberBirth}</td>
			</tr>
			<tr>
				<th>연락처</th>
				<td>${memberDto.memberContact}</td>
			</tr>
			<tr>
				<th>등급</th>
				<td>${memberDto.memberLevel}</td>
			</tr>
			<tr>
				<th>포인트</th>
				<td>${memberDto.memberPoint}포인트</td>
			</tr>
			<tr>
				<th>주소</th>
				<td>
					<c:if test="${memberDto.memberPost != null}">
					[${memberDto.memberPost}] 
					${memberDto.memberAddress1} 
					${memberDto.memberAddress2}
					</c:if>
				</td>
			</tr>
			<tr>
				<th>가입일</th>
				<td>
					<fmt:formatDate value="${memberDto.memberJoin}" pattern="y년 M월 d일 H시 m분 s초"/>
				</td>
			</tr>
			<tr>
				<th>최종로그인</th>
				<td>
					<fmt:formatDate value="${memberDto.memberLogin }" pattern= "y년 M월 d일 H시 m분 s초"/>
				</td>
			</tr>
			<tr>
				<th>비밀번호 변경일</th>
				<td>
					<fmt:formatDate value="${memberDto.memberChange }" pattern= "y년 M월 d일 H시 m분 s초"/>
				</td>
			</tr>
		
			
		</table>
	</div>
	<hr>

	<!-- 3. 나의 상품 주문 내역 -->
	<div class="cell center w-100">
		<h2>나의 상품 주문 내역</h2>
		<table class="w-100 table table-border">
			<!-- 주문 내역 반복 -->
			<c:forEach var="orders" items="${ordersList}">
				<tr>
					<td>${orders.ordersNo}</td>
					<td><a href="/orders/detail?ordersNo=${orders.ordersNo}">${orders.productName}</a></td>
					<td><fmt:formatNumber value="${orders.ordersTotalPrice}" pattern="#,##0"/>원</td>
					<td><fmt:formatDate value="${orders.ordersCreatedAt}"
							pattern="yyyy-MM-dd HH:mm" /></td>
					<td>${orders.ordersStatus}</td>
					<td><a href="/orders/detail?ordersNo=${orders.ordersNo}"
						class="btn btn-primary btn-sm">상세보기</a></td>
				</tr>
			</c:forEach>
			<c:if test="${empty ordersList}">
				<tr>
					<td colspan="6" class="center">주문 내역이 없습니다.</td>
				</tr>
			</c:if>

		</table>
	</div>

	<hr>

	<!-- 4. 나의 리뷰 목록 -->
	<div class="cell center">
		<h2>나의 상품 리뷰 내역</h2>
	</div>
	<div class="cell">
		<table class="w-100 table table-border">
			<!-- 리뷰 항목 반복 -->
			<c:forEach var="review" items="${reviewList}">
				<tr>
					<td><a
						href="/product/detail?productNo=${review.productNo}">${review.productName}</a></td>
					<td><c:forEach begin="1" end="${review.reviewRating}">
							<i class="fa-solid fa-star gold"></i>
						</c:forEach> <c:forEach begin="${review.reviewRating+1}" end="5">
							<i class="fa-regular fa-star"></i>
						</c:forEach></td>
					<td>${review.reviewContent}</td>
					<td><fmt:formatDate value="${review.reviewCreatedAt}"
							pattern="yyyy-MM-dd" /></td>
					<td>
						<button class="btn-edit" data-review-no="${review.reviewNo}">수정</button>
						<button class="btn-delete" data-review-no="${review.reviewNo}">삭제</button>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>

	<h2>
		<a href="edit">내 정보 변경</a>
	</h2>
	<h2>
		<a href="password">비밀번호 변경하기</a>
	</h2>
	<h2>
		<a href="drop">회원 탈퇴하기</a>
	</h2>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

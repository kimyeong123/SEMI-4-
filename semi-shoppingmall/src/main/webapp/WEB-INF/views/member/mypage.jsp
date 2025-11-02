<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<style>
h1 {
	font-size: 1.8em;
	padding-bottom: 10px;
	margin-bottom: 30px;
	color: #333;
	border-bottom: 1px solid #ddd;
}
h2 {
	font-size: 1.8em;
	margin-bottom: 20px;
	color: #333;
	border-bottom: 1px solid #ddd;
	padding-bottom: 8px;
}

.profile-card {
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 40px;
}

.profile-wrapper {
	width: 200px;
	height: 200px;
	position: relative;
	border-radius: 50%;
	overflow: hidden;
	box-shadow: 0 4px 10px rgba(0,0,0,0.1);
	margin-bottom: 10px;
	transition: transform 0.2s;
}

.profile-wrapper:hover {
	transform: scale(1.03);
}

.profile-wrapper img {
	width: 100%;
	height: 100%;
	object-fit: cover;
	display: block;
}

.change-label {
	position: absolute;
	top: 0; left: 0; right: 0; bottom: 0;
	background-color: rgba(0,0,0,0.4);
	color: white;
	display: flex;
	justify-content: center;
	align-items: center;
	font-weight: bold;
	font-size: 16px;
	cursor: pointer;
	opacity: 0;
	transition: opacity 0.2s;
	border-radius: 50%;
}

.profile-wrapper:hover > .change-label {
	opacity: 1;
}

.profile-delete-btn {
	margin-top: 10px;
	padding: 6px 14px;
	font-size: 14px;
	font-weight: bold;
	background-color: #d9534f;
	color: white;
	border: none;
	border-radius: 5px;
	cursor: pointer;
	display: flex;
	align-items: center;
	gap: 5px;
	transition: background-color 0.2s, transform 0.2s;
}

.profile-delete-btn:hover {
	background-color: #c9302c;
	transform: translateY(-2px);
}

.member-id {
	font-size: 20px;
	font-weight: bold;
	color: #333;
	margin-top: 8px;
}

/* ========== 테이블 ========== */
table {
	width: 100%;
	border-collapse: collapse;
	margin-bottom: 30px;
}

th, td {
	padding: 10px 15px;
	border: 1px solid #ddd;
	font-size: 14px;
}

th {
	background-color: #f9f9f9;
	font-weight: 600;
	text-align: left;
}

td {
	vertical-align: middle;
}

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



.btn-delete:hover {
	background-color: #c9302c;
}

.gold { color: gold; font-size: 16px; }

.review-table-fixed {
    table-layout: fixed;
    border-collapse: collapse;
    width: 100%;
    margin-top: 10px;
    font-size: 14px; /* 글자는 조금 작게 */
}

.review-table-fixed th, .review-table-fixed td {
    border: 1px solid #ccc;
    padding: 16px 12px; /* 셀은 넓게 */
    vertical-align: middle;
}

.review-table-fixed th {
    background-color: #f8f8f8;
    text-align: center;
    color: #495057;
    font-weight: 600;
    font-size: 14px; /* 헤더 폰트도 작게 */
}

/* 관리 열 너비 고정 */
.review-table-fixed tbody tr td:last-child {
    width: 140px;    
    text-align: center;
    white-space: nowrap;
}

/* 리뷰 내용 셀 남은 공간 차지 */
.review-table-fixed tbody tr td.review-content {
    width: auto;
    word-break: break-word;
    white-space: pre-wrap;
}

/* 평점, 작성일, 작성자 열 너비 지정 */
.review-table-fixed thead tr th:nth-child(1) { width: 20%; } /* 상품명 */
.review-table-fixed thead tr th:nth-child(2) { width: 15%; } /* 평점 */
.review-table-fixed thead tr th:nth-child(4) { width: 15%; } /* 작성일 */
.review-table-fixed thead tr th:nth-child(5) { width: 15%; } /* 관리 */
.review-table-fixed thead tr th:nth-child(3) { width: 35%; } /* 내용 */

.not-link {
	text-decoration-line: none !important;
}


</style>

<!-- jQuery & Font Awesome -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

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
//여기서 바로 삭제하는거 아님
// 		$(".profile-delete-btn").on("click", function() {
// 			if (!confirm("정말 삭제하시겠습니까?\n삭제 후 복구할 수 없습니다"))
// 				return;
// 			$.ajax({
// 				url : "/rest/member/delete",
// 				method : "post",
// 				success : function() {
// 					var newOrigin = origin + "&t=" + new Date().getTime();
// 					$(".image-profile").attr("src", newOrigin);
// 				}
// 			});
// 		});
	});
</script>

<script>
//2. 리뷰 수정 모드 진입
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
</script>

<div class="container">

	<!-- 프로필 카드 -->
	<div class="profile-card">
		<div class="profile-wrapper">
			<img class="image-profile" src="${pageContext.request.contextPath}/member/profile?memberId=${memberDto.memberId}&t=<%= System.currentTimeMillis() %>" onerror="this.src='https://placehold.co/200x200/f0f0f0/888?text=Profile';">
			<label for="profile-input" class="change-label">변경</label>
			<input type="file" id="profile-input" style="display:none">
		</div>
<!-- 		그냥 탈퇴가 아니라 탈퇴 페이지로 이어져야됨 -->
<!-- 		<button type="button" class="profile-delete-btn"> -->
<!-- 		</button> -->
			<a href = "/member/drop" class="profile-delete-btn not-link">
				<i class="fa-solid fa-trash-can"></i><span>회원 탈퇴</span>
			</a>
		<div class="member-id">${memberDto.memberId}</div>
	</div>

	<!-- 회원 정보 -->
	<h2>회원 정보</h2>
	<table>
		<tr><th>닉네임</th><td>${memberDto.memberNickname}</td></tr>
		<tr><th>이메일</th><td>${memberDto.memberEmail}</td></tr>
		<tr><th>생년월일</th><td>${memberDto.memberBirth}</td></tr>
		<tr><th>연락처</th><td>${memberDto.memberContact}</td></tr>
		<tr><th>등급</th><td>${memberDto.memberLevel}</td></tr>
		<tr><th>포인트</th><td>${memberDto.memberPoint} 포인트</td></tr>
		<tr><th>주소</th>
			<td>
				<c:if test="${memberDto.memberPost != null}">
					[${memberDto.memberPost}] ${memberDto.memberAddress1} ${memberDto.memberAddress2}
				</c:if>
			</td>
		</tr>
		<tr><th>가입일</th>
			<td><fmt:formatDate value="${memberDto.memberJoin}" pattern="y년 M월 d일 H시 m분 s초"/></td>
		</tr>
		<tr><th>최종로그인</th>
			<td><fmt:formatDate value="${memberDto.memberLogin}" pattern="y년 M월 d일 H시 m분 s초"/></td>
		</tr>
		<tr><th>비밀번호 변경일</th>
			<td><fmt:formatDate value="${memberDto.memberChange}" pattern="y년 M월 d일 H시 m분 s초"/></td>
		</tr>
	</table>

	<!-- 나의 리뷰 목록 -->
<h2>나의 리뷰 내역</h2>
<table class="review-table-fixed">
    <thead>
        <tr>
            <th>상품명</th>
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
                <a href="/product/detail?productNo=${review.productNo}">${review.productName}</a>
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
                    <button type="button" class="btn btn-edit" data-review-no="${review.reviewNo}">수정</button>
                    <button type="button" class="btn btn-review-delete" data-review-no="${review.reviewNo}">삭제</button>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty reviewList}">
        <tr>
            <td colspan="5" style="text-align:center; color:#999; padding: 30px;">
                작성한 리뷰가 없습니다.
            </td>
        </tr>
    </c:if>
    </tbody>
</table>

</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

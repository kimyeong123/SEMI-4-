<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<style>
/* * product_detail.jsp의 모노크롬/네모 테마 CSS를 기반으로 적용 
 */

/* === 공통 스타일 및 레이아웃 === */
/* 최대 너비를 1000px로 확장하여 넓은 화면에서 더 시원하게 보이도록 수정 */
.container.w-1000 {
	max-width: 1000px;
	margin: 40px auto;
	padding: 20px;
}
h2 {
    font-size: 1.8em;
    padding-bottom: 10px;
    margin-top: 40px;
    margin-bottom: 30px;
    color: #333;
    border-bottom: 1px solid #ddd; /* h1 스타일과 통일 */
}
.center { text-align: center; }


/* === 입력 필드 및 폼 컨트롤 통일 === */
.field, .form-control, .info-table-card td textarea {
	padding: 10px;
	border: 1px solid #ccc;
	border-radius: 0; /* 네모난 형태로 변경 */
	box-sizing: border-box;
	width: 100%; 
}

/* === 버튼 스타일 통일 === */
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

/* Primary: 리뷰 수정 완료 버튼 */
.btn-update {	
	padding: 5px 10px;	
	font-size: 0.85em;	
	border-color: #888;	
	color: #555;	
	background-color: transparent;	
}	
.btn-update:hover { background-color: #f0f0f0; color: #333; }

/* Secondary: 내 정보 변경, 비밀번호 변경하기 등 */
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

/* Danger: 리뷰 삭제, 회원 탈퇴 */
.btn-danger {
	border: 1px solid #c00; 
	color: white;	
	background-color: #d9534f; /* 밝은 빨강 */
    transition: background-color 0.2s, color 0.2s, border-color 0.2s, filter 0.2s;
	font-size: 0.85em;	
	padding: 5px 10px;
}	
.btn-danger:hover { 
    background-color: #c9302c; /* 호버 시 진한 빨강 */
    filter: none;
}

/* Utility (수정/삭제 버튼) */
.btn-edit {	
	padding: 5px 10px;	
	font-size: 0.85em;	
	border-color: #888;	
	color: #555;	
	background-color: transparent;	
}	
.btn-edit:hover { background-color: #f0f0f0; color: #333; }
.btn-delete {	/* 리뷰 삭제 버튼은 Danger 스타일을 따름 */
	border: 1px solid #c00; 
	color: white;	
	background-color: #d9534f;
    transition: background-color 0.2s, color 0.2s, border-color 0.2s, filter 0.2s;
	font-size: 0.85em;	
	padding: 5px 10px;
}
.btn-delete:hover {
	background-color: #c9302c;
}


/* --- 1. 프로필 카드 스타일 --- */
.profile-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 30px 20px;
    margin-bottom: 40px;
    background-color: #f8f8f8; /* 연한 배경 */
    border: 1px solid #ddd; /* 테두리 추가 */
    border-radius: 0;
}
.profile-wrapper {
	width: 150px;
	height: 150px;
	position: relative;
	border-radius: 0; /* 네모난 형태로 변경 */
	overflow: hidden;
	margin-bottom: 15px;
	border: 1px solid #ccc;
    box-shadow: none;
}
.profile-wrapper>img {
	width: 100%;
	height: 100%;
	object-fit: cover;
}
.profile-wrapper>label {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.5);
	color: white;
	display: none;
	justify-content: center;
	align-items: center;
	cursor: pointer;
	font-weight: bold;
	font-size: 14px;
}
.profile-wrapper:hover>label {
	display: flex;
}
.profile-delete-btn {
    /* 버튼 스타일 통일 */
    cursor: pointer;
    font-size: 14px;
    margin-top: 10px;
    display: flex;
    align-items: center;
    gap: 5px;
    /* Danger 버튼 스타일 적용 */
	border: 1px solid #c00; 
	color: white;	
	background-color: #d9534f;
	padding: 5px 10px;
	border-radius: 0;
}
.profile-delete-btn:hover {
    background-color: #c9302c;
}
.member-id {
    font-size: 28px;
    font-weight: bold;
    color: #333;
}

/* --- 2. 정보 테이블 스타일 통일 --- */
.info-table-card {
    border-radius: 0;
    border: 1px solid #ccc; /* 테두리 통일 */
    border-collapse: collapse;
    overflow: hidden;
    margin-bottom: 30px;
}
.info-table-card th, .info-table-card td {
    padding: 15px 20px;
    font-size: 15px;
    border: none; /* 내부 테두리는 tr:not(:last-child)에서 관리 */
}
.info-table-card th {
    background-color: #f0f0f0; /* 밝은 회색으로 변경 */
    width: 120px;
    text-align: left;
    color: #555;
    font-weight: 600;
}
.info-table-card tr:not(:last-child) {
    border-bottom: 1px solid #eee;
}
.info-table-card td {
    color: #333;
}
.info-table-card td textarea {
    min-height: 80px;
    font-size: 14px;
    resize: vertical;
}

/* --- 3. 리뷰 목록 스타일 통일 --- */
.review-table-card {
    border-collapse: collapse;
    width: 100%;
    border-radius: 0;
    border: 1px solid #ccc; /* 테두리 통일 */
    box-shadow: none;
}
.review-table-card thead th {
    background-color: #f8f8f8; /* 헤더 배경 연한 회색 */
	text-align: center;
	color: #495057;
    padding: 12px 15px;
    font-weight: 600;
    border: 1px solid #ccc;
}
.review-table-card tbody td {
    padding: 15px 10px;
    border-bottom: 1px solid #eee;
    vertical-align: top;
    font-size: 14px;
    border-left: 1px solid #eee;
    border-right: 1px solid #eee;
}
.review-table-card tbody tr:last-child td {
    border-bottom: none;
}
.review-table-card a {
    font-weight: bold;
    color: #3498db; /* product_detail.jsp의 blue와 통일 */
    text-decoration: none; /* 상품명 디자인 개선을 위해 기본 밑줄 제거 */
}
/* 상품명 호버 디자인 추가 */
.review-table-card td:nth-child(1) a:hover {
    text-decoration: underline; /* 호버 시 밑줄 추가 */
    color: #2980b9; /* 약간 더 진한 파란색 */
}

.review-table-card .gold {
    color: gold;
    font-size: 18px;
}

/* 리뷰 테이블 컬럼 너비 재조정 (상품명, 평점 공간 확보) */
.review-table-card td:nth-child(1) { 
    width: 20%; 
    text-align: left; 
    /* 긴 이름 처리 */
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
} /* 상품명: 20% 유지, 왼쪽 정렬, 말줄임표 처리 */
.review-table-card td:nth-child(2) { width: 15%; text-align: center; } /* 평점: 15%로 확장 */
.review-table-card td:nth-child(3) { width: auto; } /* 내용 */
.review-table-card td:nth-child(4) { width: 10%; text-align: center; color: #777;} /* 날짜: 10% */
.review-table-card td:nth-child(5) { width: 10%; white-space: nowrap; text-align: center;} /* 버튼: 10% */


/* --- 하단 링크 스타일 통일 --- */
/* 새로운 레이아웃: 왼쪽(설정 그룹)과 오른쪽(탈퇴) 분리 */
.quick-links-container { 
    display: flex;
    justify-content: space-between; /* 양 끝 정렬 */
    align-items: center;
    padding: 30px 0;
    border-top: 1px solid #eee;
}

/* 일반 설정 링크 그룹 */
.setting-group {
    display: flex;
    gap: 20px; /* 간격 축소 */
}

.setting-group a {
    text-decoration: none;
    font-size: 1em;
    font-weight: normal;
    
    /* btn-secondary 스타일 기반으로 링크 스타일 적용 */
    border: 1px solid #aaa;	
	color: #555;	
	background-color: #f5f5f5;	
	padding: 10px 20px;
    border-radius: 0;
}
.setting-group a:hover {
    background-color: #eee;
    border-color: #888;
}

/* 탈퇴 버튼 (btn-danger 스타일은 이미 CSS 상단에 정의되어 있음) */
.quick-links-container .btn-danger {
    padding: 10px 20px; /* 크기를 일반 버튼과 동일하게 맞춤 */
    font-size: 1em;
}

/* --- 반응형 (모바일) --- */
@media (max-width: 650px) {
    .container.w-1000 { 
        padding: 10px;
        transform: none; /* 모바일에서는 중앙 정렬 유지 */
    }
    h2 { font-size: 1.5em; margin-top: 30px; margin-bottom: 20px;}
    .info-table-card th { width: 90px; padding: 10px 15px; font-size: 14px;}
    .info-table-card td { padding: 10px 15px; font-size: 14px;}
    
    .review-table-card th, .review-table-card td { padding: 10px; font-size: 13px; }
    
    /* 모바일에서는 하단 버튼을 세로로 정렬하고 전체 너비를 사용 */
    .quick-links-container {
        flex-direction: column;
        gap: 15px;
        align-items: stretch; /* 전체 너비 사용 */
    }
    .setting-group {
        flex-direction: column;
        gap: 10px;
        width: 100%;
    }
    .setting-group a, .quick-links-container .btn-danger {
        width: 100%;
        box-sizing: border-box;
        text-align: center;
    }

    /* 모바일에서 버튼 관리 열을 세로로 변경 */
    .review-table-card td:nth-child(5) { 
        width: auto;
        display: flex; 
        flex-direction: column; 
        gap: 5px; 
        align-items: center;
    }
    .btn-edit, .btn-delete, .btn-update {
        width: 100%;
        margin: 0;
    }
}
</style>

<!-- jQuery CDN -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<!-- Font Awesome (아이콘 때문에 필요) -->
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">


<!-- 프로필 변경 기능 코드 (기존과 동일) -->
<script type="text/javascript">
	$(function() {
		// profile?memberId=...의 절대 경로를 얻습니다.
		var origin = $(".image-profile").attr("src").split("&t=")[0]; 
		
		// 프로필 변경 (파일 업로드)
		$("#profile-input").on("input", function() {
			var list = $("#profile-input").prop("files");
			if (list.length == 0)
				return;
			var form = new FormData();
			form.append("attach", list[0]);
			
			// AJAX URL에 contextPath를 추가하여 절대 경로 문제를 방지합니다.
			$.ajax({
				processData : false,
				contentType : false,
				url : "${pageContext.request.contextPath}/rest/member/profile",
				method : "post",
				data : form,
				success : function() {
					// 캐시 무효화를 위해 쿼리 파라미터 업데이트
					var newOrigin = origin + "&t=" + new Date().getTime();
					$(".image-profile").attr("src", newOrigin);
				},
				error: function(xhr) {
					// alert 대신 콘솔 로그 사용
					console.error("프로필 변경 실패: 서버 오류 또는 권한 부족", xhr);
				}
			});
		});

		// 프로필 삭제
		$(".profile-delete-btn").on("click", function() {
			// [IMPORTANT] confirm() 대신 사용자 정의 Modal/UI를 사용해야 함 (이 환경의 제약사항)
            // 임시로 confirm을 유지하고, 실제 서비스에서는 사용자 정의 UI로 대체해야 합니다.
			if (!confirm("정말 삭제하시겠습니까?\n삭제 후 복구할 수 없습니다"))
				return;
				
			$.ajax({
				url : "${pageContext.request.contextPath}/rest/member/deleteProfile", // 프로필 삭제 API로 가정
				method : "post",
				success : function() {
					// 캐시 무효화를 위해 쿼리 파라미터 업데이트
					var newOrigin = origin + "&t=" + new Date().getTime();
					$(".image-profile").attr("src", newOrigin);
				},
				error: function(xhr) {
					console.error("프로필 삭제 실패: 서버 오류 또는 권한 부족", xhr);
				}
			});
		});
	});
</script>

<script>
	// 리뷰 삭제/수정 기능
	$(document).on("click", ".btn-delete", function() {
        // [IMPORTANT] confirm() 대신 사용자 정의 Modal/UI를 사용해야 함
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
					// 삭제 후 알림 대신 콘솔 로그를 사용하고 UI에서 바로 제거하여 부드럽게 처리
				} else {
					// alert 대신 콘솔 로그 사용
					console.error("리뷰 삭제 실패: 서버에서 실패 응답");
				}
			},
			error : function() {
				console.error("리뷰 삭제 중 오류가 발생했습니다.");
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

				// product_detail.jsp의 스타일을 따름
				contentTd.html('<textarea class="edit-content field" rows="3">' + original + '</textarea>');
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
                        
                        if (!newContent.trim()) {
                            // alert 대신 콘솔 로그 사용
                            console.warn("리뷰 수정: 내용을 입력해주세요.");
                            return;
                        }

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
											console.error("리뷰 수정 실패: 서버에서 실패 응답");
										}
									},
									error : function() {
										console.error("리뷰 수정 중 오류 발생");
									}
								});
					});
</script>

<div class="container w-1000">
	
    <!-- 프로필 카드 섹션 -->
	<div class="profile-card">
        <div class="profile-wrapper">
            <!-- 캐시 문제 해결을 위해 System.currentTimeMillis() 추가 -->
			<img class="image-profile"  
                 src = "${pageContext.request.contextPath}/member/profile?memberId=${memberDto.memberId }&t=<%= System.currentTimeMillis() %>" 
                 onerror="this.src='https://placehold.co/150x150/f0f0f0/888?text=Profile';" >
			<label for="profile-input" class="flex-box flex-center">변경</label>
			<input type="file" id="profile-input" style="display:none">
		</div>
		<div class="member-id">${memberDto.memberId}</div>
		
        <!-- 프로필 삭제 버튼 (btn-danger 스타일 적용) -->
		<label class="profile-delete-btn">
			<i class="fa-solid fa-trash-can"></i>
			<span>프로필 삭제</span>
		</label>
	</div>

	<!-- 회원 상세 정보 섹션 -->
	<div class = "cell center">
		<h2>${memberDto.memberId}님의 상세 정보</h2>
	</div>
	<div class = "cell">
		<!-- 테이블 스타일 통일 -->
		<table class="w-100 table info-table-card">
			<tr>
				<th>닉네임</th>
				<td>${memberDto.memberNickname}</td>
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
				<td>
                    <fmt:formatNumber value="${memberDto.memberPoint}" pattern="#,##0"/>
                    포인트
                </td>
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
					<fmt:formatDate value="${memberDto.memberJoin}" pattern="yyyy년 M월 d일 HH:mm:ss"/>
				</td>
			</tr>
			<tr>
				<th>최종로그인</th>
				<td>
					<fmt:formatDate value="${memberDto.memberLogin }" pattern= "yyyy년 M월 d일 HH:mm:ss"/>
				</td>
			</tr>
			<tr>
				<th>비밀번호 변경일</th>
				<td>
					<fmt:formatDate value="${memberDto.memberChange }" pattern= "yyyy년 M월 d일 HH:mm:ss"/>
				</td>
			</tr>
		</table>
	</div>

	<!-- 나의 리뷰 목록 섹션 -->
	<div class="cell center">
		<h2>나의 상품 리뷰 내역</h2>
	</div>
	<div class="cell">
		<c:if test="${empty reviewList}">
            <p class="center" style="color: #777; padding: 20px 0;">아직 작성하신 리뷰가 없습니다.</p>
        </c:if>
		<c:if test="${not empty reviewList}">
            <!-- 리뷰 테이블 스타일 통일 (review-table-fixed와 유사하게) -->
            <table class="review-table-card">
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
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/product/detail?productNo=${review.productNo}">
                                    ${review.productName}
                                </a>
                            </td>
                            <td class="center">
                                <c:forEach begin="1" end="${review.reviewRating}">
                                    <i class="fa-solid fa-star gold"></i>
                                </c:forEach> 
                                <c:forEach begin="${review.reviewRating+1}" end="5">
                                    <i class="fa-regular fa-star" style="color:#ccc;"></i>
                                </c:forEach>
                            </td>
                            <td>${review.reviewContent}</td>
                            <td><fmt:formatDate value="${review.reviewCreatedAt}"
                                    pattern="yyyy-MM-dd" /></td>
                            <td>
                                <button class="btn-edit" data-review-no="${review.reviewNo}">수정</button>
                                <button class="btn-delete" data-review-no="${review.reviewNo}">삭제</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
	</div>

    <!-- 하단 빠른 링크 섹션 - 디자인 변경 적용 -->
    <div class="quick-links-container">
        <!-- 설정 관련 링크 그룹 -->
        <div class="setting-group">
            <a href="edit">내 정보 변경</a>
            <a href="password">비밀번호 변경하기</a>
        </div>
        <!-- 회원 탈퇴하기는 독립적으로 오른쪽 끝에 배치 (Danger 스타일 유지) -->
        <a href="drop" class="btn-danger">회원 탈퇴하기</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

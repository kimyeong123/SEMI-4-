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
	cursor: pointer;
	justify-content: center;
	align-items: center;
}

.profile-wrapper:hover>label {
	display: flex;
}
.profile-delete-btn {
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    margin-top: 5px;
}
</style>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script type="text/javascript">
	$(function() {
		var origin = $(".image-profile").attr("src");
		var viewingId = '${memberDto.memberId}'; // 현재 페이지의 멤버 ID (타겟)
		var loginId = '${sessionScope.loginId}'; // 로그인한 사용자 ID

		var loginIdLevel = "";
		
		// ===================================================
		// 1. 프로필 변경/삭제 기능 제어 (본인만 가능)
		// ===================================================
		if (viewingId !== loginId) {
			// 타인 페이지를 볼 때 프로필 변경/삭제 기능 비활성화
			$("#profile-input").prop("disabled", true);
			$(".profile-wrapper>label").hide();
			$(".profile-delete-btn").hide();
		} else {
			// 본인 페이지일 때만 활성화 (기존 AJAX 로직 유지)
			$("#profile-input").on("input", function() {
				var list = $("#profile-input").prop("files");
				if (list.length == 0) return;
				var form = new FormData();
				form.append("attach", list[0]);
				
				$.ajax({
					processData : false, contentType : false,
					url : "${pageContext.request.contextPath}/rest/member/profile",
					method : "post", data : form,
					success : function() {
						var newOrigin = origin + "&t=" + new Date().getTime();
						$(".image-profile").attr("src", newOrigin);
					},
					error: function() { alert("프로필 변경 실패"); }
				});
			});

			$(".profile-delete-btn").on("click", function() {
				if (!confirm("정말 삭제하시겠습니까?\n삭제 후 복구할 수 없습니다")) return;
				
				$.ajax({
					url : "${pageContext.request.contextPath}/rest/member/deleteProfile",
					method : "post",
					success : function() {
						var newOrigin = origin + "&t=" + new Date().getTime();
						$(".image-profile").attr("src", newOrigin);
					},
					error: function() { alert("프로필 삭제 실패"); }
				});
			});
		}

		// ===================================================
		// 2. 리뷰 삭제 기능 (작성자 또는 관리자)
		// ===================================================
		$(document).on("click", ".btn-delete", function() {
			if (!confirm("정말 삭제하시겠습니까?")) return;

			var btn = $(this);
			var reviewNo = btn.data("review-no");

			$.ajax({
				url : "${pageContext.request.contextPath}/rest/review/delete",
				type : "post", data : { reviewNo : reviewNo },
				success : function(result) {
					if (result) {
						btn.closest("tr").remove();
						alert("삭제 완료!");
					} else {
						alert("삭제 실패");
					}
				},
				error : function() { alert("삭제 중 오류가 발생했습니다."); }
			});
		});
		
		// ===================================================
		// 3. 회원 삭제 기능 (관리자 전용)
		// ===================================================
		$("#btn-member-delete").on("click", function() {
		    var deleteId = '${memberDto.memberId}';
		    
		    if (!confirm(deleteId + " 회원을 정말 삭제하시겠습니까? 삭제 후 복구할 수 없습니다.")) { return; }

		    $.ajax({
		        url: "${pageContext.request.contextPath}/admin/member/delete",
		        method: "post",
		        data: { memberId: deleteId },
		        success: function(result) {
		            if (result) {
		                alert(deleteId + " 회원 삭제 완료.");
		                location.href = "${pageContext.request.contextPath}/admin/member/list";
		            } else {
		                alert("회원 삭제 실패: 권한이 없거나 오류 발생.");
		            }
		        },
		        error: function() { alert("회원 삭제 처리 중 오류 발생."); }
		    });
		});
		
		if(loginId) {
			$.ajax({
		        url: '${pageContext.request.contextPath}/rest/member/checkMemberLevel',
		        type: 'GET',
		        data: { memberId: loginId },
		        success: function(level) {
		            loginUserLevel = level; // 조회된 레벨 값을 저장
		            console.log("AJAX로 조회된 로그인 레벨:", loginUserLevel);
		            
		            // 🚩 레벨을 조회한 후, 권한에 따라 UI를 업데이트하는 함수 실행
		            applyAdminPermissions();
		        },
		        error: function() { 
		            console.error("사용자 레벨 조회 실패");
		            // 실패 시 일반 사용자 권한으로 간주하거나, 기본값 설정
		            loginUserLevel = "일반회원";
		            applyAdminPermissions();
		        }
		    });
		}
		else {
			loginUserLevel = "비회원"
		}
		
		// 🚩 권한에 따라 UI를 제어하는 함수
        function applyAdminPermissions() {
            // 1. 회원 삭제 버튼 표시 제어 (관리자 권한)
            if (loginUserLevel === '관리자' && viewingId !== loginId) {
                $("#member-delete-area").show();
            } else {
                $("#member-delete-area").hide();
            }
            
            // 2. 리뷰 삭제 버튼 표시 제어 (관리자 권한)
            if (loginUserLevel === '관리자') {
                // 'admin-delete-btn' 클래스를 가진 모든 버튼을 표시
                $(".admin-delete-btn").show();
            }
		}
		
	});
</script>

<div class="container w-600">
	<div class = "cell center">
		<h2>${memberDto.memberId}님의 정보</h2> </div>
	
	<div class = "cell">
		<table class="w-100 table table-border">
			<tr><th>닉네임</th><td>${memberDto.memberNickname}</td></tr>
			<tr>
				<th>이미지</th>
				<td>
					<div class="profile-wrapper">
						<img class="image-profile"  src = "${pageContext.request.contextPath}/member/profile?memberId=${memberDto.memberId }" >
						<label for="profile-input" class="flex-box flex-center">변경</label>
						<input type="file" id="profile-input" style="display:none">
					</div>
					<label class="profile-delete-btn red">
						<i class="fa-solid fa-xmark"></i>
						<span>삭제</span>
					</label>
					<br><br>
				</td>
			</tr>
			<tr><th>이메일</th><td>${memberDto.memberEmail}</td></tr>
			<tr><th>생년월일</th><td>${memberDto.memberBirth}</td></tr>
			<tr><th>연락처</th><td>${memberDto.memberContact}</td></tr>
			<tr><th>등급</th><td>${memberDto.memberLevel}</td></tr>
			<tr><th>포인트</th><td>${memberDto.memberPoint}포인트</td></tr>
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

	<div class="cell center w-100">
		<h2>${memberDto.memberId}님의 상품 주문 내역</h2>
		<table class="w-100 table table-border">
			<thead>
				<tr><th>번호</th><th>상품</th><th>총액</th><th>주문일</th><th>상태</th><th>상세</th></tr>
			</thead>
			<tbody>
				<c:forEach var="order" items="${ordersList}">
					<tr>
						<td>${order.ordersNo}</td>
						<td><a href="/order/detail?ordersNo=${order.ordersNo}">${order.productName}</a></td>
						<td>${order.ordersTotalPrice}원</td>
						<td><fmt:formatDate value="${order.ordersDate}" pattern="yyyy-MM-dd HH:mm" /></td>
						<td>${order.ordersStatus}</td>
						<td><a href="/order/detail?ordersNo=${order.ordersNo}" class="btn btn-primary btn-sm">상세보기</a></td>
					</tr>
				</c:forEach>
				<c:if test="${empty ordersList}">
					<tr><td colspan="6" class="center">주문 내역이 없습니다.</td></tr>
				</c:if>
			</tbody>
		</table>
	</div>

	<hr>

	<div class="cell center">
		<h2>${memberDto.memberId}님의 상품 리뷰 내역</h2>
	</div>
	<div class="cell">
		<table class="w-100 table table-border">
			<thead>
				<tr><th>상품</th><th>평점</th><th>내용</th><th>작성일</th><th>관리</th></tr>
			</thead>
			<tbody>
				<c:forEach var="review" items="${reviewList}">
					<tr id="review-${review.reviewNo}">
						<td><a href="/product/detail?productNo=${review.productNo}">${review.productName}</a></td>
						<td><c:forEach begin="1" end="${review.reviewRating}">
								<i class="fa-solid fa-star gold"></i>
							</c:forEach> <c:forEach begin="${review.reviewRating+1}" end="5">
								<i class="fa-regular fa-star"></i>
							</c:forEach></td>
						<td>${review.reviewContent}</td>
						<td><fmt:formatDate value="${review.reviewCreatedAt}" pattern="yyyy-MM-dd" /></td>
						<td>
							<c:if test="${sessionScope.loginId eq review.memberId}">
								<button class="btn-delete" data-review-no="${review.reviewNo}">삭제</button>
							</c:if>
							<c:if test ="${sessionScope.loginId ne review.memberId }">
								<button class ="btn-delete admin-delete-btn" data-review-no = "${review.reviewNo}"  style="display:none;">삭제(관리자)</button>
							</c:if>
						</td>
					</tr>
				</c:forEach>
				<c:if test="${empty reviewList}">
					<tr><td colspan="5" class="center">작성한 리뷰가 없습니다.</td></tr>
				</c:if>
			</tbody>
		</table>
	</div>

	<div class="cell center" style="margin-top: 20px;" id = "member-delete-area">
		<c:if test="${memberDto.memberId ne sessionScope.loginId}">
			<button id="btn-member-delete" class="btn btn-danger">회원 삭제</button>
		</c:if>
		<c:if test="${memberDto.memberId eq sessionScope.loginId}">
			<p class="red">본인 계정은 여기서 삭제할 수 없습니다. 회원 탈퇴 기능을 이용하세요.</p>
		</c:if>
	</div>
	<hr>

	<c:if test="${memberDto.memberId eq sessionScope.loginId}">
		<h2><a href="edit">내 정보 변경</a></h2>
		<h2><a href="password">비밀번호 변경하기</a></h2>
		<h2><a href="drop">회원 탈퇴하기</a></h2>
	</c:if>
	
<%-- 	<p style="color: red;">DEBUG: 로그인 레벨: [${memberDto.memberLevel}]</p> --%>
<%-- 	<p style="color: red;">DEBUG: 로그인 레벨: [${sessionScope.loginId}]</p> --%>
<%-- 	<p style="color: red;">DEBUG: 로그인 레벨: [${loginUserLevel}]</p> --%>
	
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
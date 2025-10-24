<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>	

<style>
	.profile-wrapper {
		width:200px;
		height:200px;
		position: relative;
		border-radius: 50%;
		overflow: hidden;
	}
	.profile-wrapper > img {
		width: 100%;
		height: 100%;
	}
	.profile-wrapper > label {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.3);
		color: white;
		display: none;
	}
	.profile-wrapper:hover > label {
		display: flex;
	}
</style>

<!-- 프로필 변경 기능 코드 -->
<script type="text/javascript">
	$(function(){
		var origin = $(".image-profile").attr("src");
		$("#profile-input").on("input", function(){
			var list = $("#profile-input").prop("files");
			if(list.length == 0) return;
			var form = new FormData();
			form.append("attach", list[0]);
			$.ajax({
				processData : false, 
				contentType : false, 
				url:"/rest/member/profile",
				method:"post",
				data: form,
				success:function(){
					var newOrigin = origin + "&t=" + new Date().getTime();
					$(".image-profile").attr("src", newOrigin);	
				}
			});
		});
		
		$(".profile-delete-btn").on("click", function(){
			if(!confirm("정말 삭제하시겠습니까?\n삭제 후 복구할 수 없습니다")) return;
			$.ajax({
				url:"/rest/member/delete",
				method:"post",
				success:function(){
					var newOrigin = origin + "&t=" + new Date().getTime();
					$(".image-profile").attr("src", newOrigin);
				}
			});
		});
	});
</script>

<script>
	// 리뷰 삭제/수정 기능
	$(document).on("click", ".btn-delete", function(){
	    if(!confirm("정말 삭제하시겠습니까?")) return;

	    var btn = $(this);
	    var reviewNo = btn.data("review-no");

	    $.ajax({
	        url: "${pageContext.request.contextPath}/rest/review/delete",
	        type: "post",
	        data: { reviewNo: reviewNo },
	        success: function(result) {
	            if(result){
	                btn.closest("tr").remove();
	                alert("삭제 완료!");
	            } else {
	                alert("삭제 실패");
	            }
	        },
	        error: function() {
	            alert("삭제 중 오류가 발생했습니다.");
	        }
	    });
	});
	$(document).on("click", ".btn-edit", function() {
	    var btn = $(this);
	    var tr = btn.closest("tr");
	    var contentTd = tr.find("td").eq(2);
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
	                tr.find("td").eq(2).text(newContent);
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

<div class="container w-600">
	<!-- 회원 정보 테이블 -->
	<div class="cell center">
		<h2>${memberDto.memberId}님의 정보</h2>
	</div>
	<div class="cell">
		<table class="w-100 table table-border">
			<!-- 회원 정보 각 항목 -->
		</table>
	</div>
	
<hr>

	<!-- 1. 나의 장바구니 목록 -->
	<div class="cell center w-100">
		<h2>나의 장바구니 목록</h2>
		<table class="w-100 table table-border">
			<!-- 장바구니 항목 반복 -->
		</table>
	</div>	
	
<hr>

	<!-- 2. 나의 위시리스트 -->
	<div class="cell center w-100">
		<h2>나의 위시리스트</h2>
		<table class="w-100 table table-border">
			<!-- 위시리스트 항목 반복 -->
		</table>
	</div>
	
<hr>

	<!-- 3. 나의 상품 주문 내역 -->
	<div class="cell center w-100">
		<h2>나의 상품 주문 내역</h2>
		<table class="w-100 table table-border">
			<!-- 주문 내역 반복 -->
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
		</table>
	</div>
		
	<h2><a href="edit">내 정보 변경</a></h2>
	<h2><a href="password">비밀번호 변경하기</a></h2>
	<h2><a href="drop">회원 탈퇴하기</a></h2>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

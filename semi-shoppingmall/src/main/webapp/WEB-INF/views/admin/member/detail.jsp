<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<link rel="stylesheet" href="/css/commons.css">

<style>
.container { max-width: 900px; margin: auto; }
h2 { margin-top: 30px; margin-bottom: 15px; color: #333; border-bottom: 1px solid #ddd; padding-bottom: 5px; }
table { width: 100%; border-collapse: collapse; margin-bottom: 30px; }
th, td { padding: 10px 15px; border: 1px solid #ddd; font-size: 14px; }
th { background-color: #f9f9f9; font-weight: 600; text-align: left; }
td { vertical-align: middle; }
.profile-wrapper { width: 150px; height: 150px; position: relative; border-radius: 50%; overflow: hidden; margin-bottom: 10px; }
.profile-wrapper>img { width: 100%; height: 100%; object-fit: cover; }
.profile-wrapper>label { position: absolute; top:0; left:0; right:0; bottom:0; background-color: rgba(0,0,0,0.3); color:white; display:flex; justify-content:center; align-items:center; cursor:pointer; opacity:0; transition: opacity 0.2s; border-radius:50%; }
.profile-wrapper:hover>label { opacity:1; }
.profile-delete-btn { cursor:pointer; display:inline-flex; align-items:center; gap:5px; background:#d9534f; color:white; padding:5px 10px; border:none; border-radius:5px; }
.profile-delete-btn:hover { background:#c9302c; }
.btn-delete { cursor:pointer; background:#dc3545; color:white; padding:5px 10px; border:none; border-radius:4px; }
.btn-delete:hover { background:#c82333; }
.gold { color: gold; }
.review-table-fixed th, .review-table-fixed td { text-align:center; }
</style>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<script>
$(function(){
    // 프로필 삭제
    $(".profile-delete-btn").on("click", function(){
        if(!confirm("정말 삭제하시겠습니까?\n삭제 후 복구할 수 없습니다")) return;
        $.ajax({
            url: "${pageContext.request.contextPath}/rest/member/deleteProfile",
            method: "post",
            success: function(){ 
                $(".image-profile").attr("src", "https://placehold.co/150x150/f0f0f0/888?text=Profile");
                alert("프로필 삭제 완료");
            },
            error:function(){ alert("프로필 삭제 실패"); }
        });
    });

    // 리뷰 삭제
    $(".btn-delete").on("click", function(){
        if(!confirm("정말 삭제하시겠습니까?")) return;
        var btn = $(this);
        var reviewNo = btn.data("review-no");
        $.ajax({
            url:"${pageContext.request.contextPath}/rest/review/delete",
            type:"post",
            data:{reviewNo:reviewNo},
            success:function(result){
                if(result){ btn.closest("tr").remove(); alert("삭제 완료!"); }
                else { alert("삭제 실패"); }
            },
            error:function(){ alert("삭제 중 오류 발생"); }
        });
    });

    // 회원 삭제
    $("#btn-member-delete").on("click", function(){
        if(!confirm("${memberDto.memberId} 회원을 정말 삭제하시겠습니까?\n삭제 후 복구 불가")) return;
        $.ajax({
            url:"${pageContext.request.contextPath}/admin/member/delete",
            type:"post",
            data:{ memberId: "${memberDto.memberId}" },
            success:function(result){
                if(result){ alert("회원 삭제 완료"); location.href="${pageContext.request.contextPath}/admin/member/list"; }
                else { alert("회원 삭제 실패"); }
            },
            error:function(){ alert("회원 삭제 중 오류 발생"); }
        });
    });
});
</script>

<div class="container">
    <!-- 회원 프로필 -->
    <h2>${memberDto.memberId}님의 정보</h2>
    <div style="display:flex; align-items:center; gap:20px; margin-bottom:20px;">
        <div class="profile-wrapper">
            <img class="image-profile" src="${pageContext.request.contextPath}/member/profile?memberId=${memberDto.memberId}" onerror="this.src='https://placehold.co/150x150/f0f0f0/888?text=Profile'">
            <label for="profile-input">변경</label>
            <input type="file" id="profile-input" style="display:none;">
        </div>
        <button class="profile-delete-btn"><i class="fa-solid fa-trash-can"></i> 삭제</button>
    </div>

    <!-- 회원 정보 테이블 -->
    <table>
        <tr><th>닉네임</th><td>${memberDto.memberNickname}</td></tr>
        <tr><th>이메일</th><td>${memberDto.memberEmail}</td></tr>
        <tr><th>생년월일</th><td>${memberDto.memberBirth}</td></tr>
        <tr><th>연락처</th><td>${memberDto.memberContact}</td></tr>
        <tr><th>등급</th><td>${memberDto.memberLevel}</td></tr>
        <tr><th>포인트</th><td>${memberDto.memberPoint} 포인트</td></tr>
        <tr><th>주소</th><td>[${memberDto.memberPost}] ${memberDto.memberAddress1} ${memberDto.memberAddress2}</td></tr>
        <tr><th>가입일</th><td><fmt:formatDate value="${memberDto.memberJoin}" pattern="yyyy-MM-dd HH:mm:ss"/></td></tr>
        <tr><th>최종로그인</th><td><fmt:formatDate value="${memberDto.memberLogin}" pattern="yyyy-MM-dd HH:mm:ss"/></td></tr>
        <tr><th>비밀번호 변경일</th><td><fmt:formatDate value="${memberDto.memberChange}" pattern="yyyy-MM-dd HH:mm:ss"/></td></tr>
    </table>

    <!-- 회원 리뷰 내역 -->
    <h2>${memberDto.memberId}님의 리뷰 내역</h2>
    <table class="review-table-fixed">
        <thead>
            <tr><th>상품</th><th>평점</th><th>내용</th><th>작성일</th><th>관리</th></tr>
        </thead>
        <tbody>
            <c:forEach var="review" items="${reviewList}">
                <tr id="review-${review.reviewNo}">
                    <td><a href="/product/detail?productNo=${review.productNo}">${review.productName}</a></td>
                    <td>
                        <c:forEach begin="1" end="${review.reviewRating}"><i class="fa-solid fa-star gold"></i></c:forEach>
                        <c:forEach begin="${review.reviewRating+1}" end="5"><i class="fa-regular fa-star"></i></c:forEach>
                    </td>
                    <td>${review.reviewContent}</td>
                    <td><fmt:formatDate value="${review.reviewCreatedAt}" pattern="yyyy-MM-dd" /></td>
                    <td><button class="btn-delete" data-review-no="${review.reviewNo}">삭제</button></td>
                </tr>
            </c:forEach>
            <c:if test="${empty reviewList}">
                <tr><td colspan="5" style="text-align:center; color:#999;">작성한 리뷰가 없습니다.</td></tr>
            </c:if>
        </tbody>
    </table>

    <!-- 회원 삭제 버튼 -->
    <div style="text-align:center; margin-bottom:30px;">
        <button id="btn-member-delete" class="btn-delete">회원 삭제</button>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

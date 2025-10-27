<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<!-- summernote -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>
<link rel="stylesheet" type="text/css" href="/summernote/custom-summernote.css">
<script src="/summernote/custom-summernote.js"></script>

<div class="flex-fill"></div>

<form autocomplete="off" action="write" method="post">
<%-- 답글일 경우(csBoardOrigin이 있을 경우) 이것을 전달하는 코드를 작성 --%>
<c:if test="${param.csBoardOrigin != null}">
	<input type="hidden" name="csBoardOrigin" value="${param.csBoardOrigin}">
</c:if>

<div class="container w-800 ">
    <div class="cell">
        <h1>게시글 작성</h1>
    </div>            
    <div class="cell">
        글은 자신의 인격입니다.<br>
        타인에 대한 무분별한 비방글은 예고 없이 삭제될 수 있습니다.
    </div>
    
    <c:if test="${sessionScope.loginLevel == '관리자'}">
    <div class="cell right">
        <input type="checkbox" name="csBoardNotice" value="Y">
        <span>공지사항으로 등록</span>
    </div>
    </c:if>
    
    <div class="cell">
        <label>제목 *</label>
        <input type="text" name="csBoardTitle" required class="field w-100">
    </div>
    <div class="cell">
        <label>내용 *</label>
        <textarea name="csBoardContent" class="summernote-editor"></textarea>
    </div>
    <div class="cell right">
        <a href="list" class="btn btn-neutral">목록으로</a>
        <button class="btn btn-positive">등록하기</button>
    </div>
</div>
</form>
<div class="flex-fill"></div>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
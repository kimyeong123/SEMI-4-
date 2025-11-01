<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<!-- summernote -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>
<link rel="stylesheet" type="text/css" href="/summernote/custom-summernote.css">
<script src="/summernote/custom-summernote.js"></script>

<script type="text/javascript">
// 문서가 로드된 후 실행 (jQuery의 ready 함수)
$(document).ready(function() {
    
    // 폼 제출(submit) 이벤트를 가로챕니다.
    $('#writeForm').submit(function(e) {
        
        // 1. 제목 필드 검증 (required 속성 검사는 브라우저에 맡기고, checkValidity로 확인)
        var $titleField = $('input[name="csBoardTitle"]');
        
        // 브라우저 기본 required 검증을 통과하지 못하면 JS 검증을 건너뜁니다.
        // checkValidity는 네이티브 DOM 메소드이므로 $titleField[0]를 사용해야 합니다.
        if (!$titleField[0].checkValidity()) {
            return; 
        }

        // 2. Summernote 내용 검증
        var contentHtml = $('#csBoardContent').val();
        
        // HTML 태그를 제거하고 공백을 없앤 순수 텍스트를 확인
        var pureText = contentHtml.replace(/<[^>]*>/g, '').trim();

        // 내용이 비어있을 경우
        if (pureText.length === 0) {
            e.preventDefault(); // 폼 전송 중단
            alert('게시글 내용을 입력해 주세요.');
            
            // Summernote 편집기로 포커스 이동 (이제 jQuery가 로드되었으므로 정상 작동)
            $('#csBoardContent').summernote('focus');
        }
    });
});

</script>


<!-- <div class="flex-fill"></div> -->

<div class="container w-800 ">
<form autocomplete="off" action="write" method="post" id = "writeForm">
<%-- 답글일 경우(csBoardOrigin이 있을 경우) 이것을 전달하는 코드를 작성 --%>
<c:if test="${param.csBoardOrigin != null}">
	<input type="hidden" name="csBoardOrigin" value="${param.csBoardOrigin}">
</c:if>

    <div class="cell">
        <h1>게시글 작성</h1>
    </div>            
    <div class="cell">
        글은 자신의 인격입니다.<br>
        타인에 대한 무분별한 비방글은 예고 없이 삭제될 수 있습니다.
    </div>
    
    <c:if test="${sessionScope.loginLevel == '관리자' && hasParent == 'N'}">
    <div class="cell right">
        <input type="checkbox" name="csBoardNotice" value="Y">
        <span>공지사항으로 등록</span>
    </div>
    </c:if>
    
	<%-- [수정] 관리자용 설정 (체크박스 숨김, 강제 값 설정) --%>
	<c:choose>
		<c:when test="${sessionScope.loginLevel == '관리자'}">
			<%-- 관리자일 경우: 체크박스를 보여주지 않고, hidden 필드로 값을 전달 --%>
			<%-- 부모 글의 비밀글 상태(parentSecret)를 따라 hidden 필드의 value를 설정 --%>
			<input type="hidden" name="csBoardSecret" value="${parentSecret}">
<%-- 			<c:if test="${parentSecret == 'Y'}"> --%>
<!-- 				<span class="text-positive">[답글: 원글에 따라 자동 비공개]</span> -->
<%-- 			</c:if> --%>
<%-- 			<c:if test="${parentSecret == 'N' && param.csBoardOrigin != null}"> --%>
<!-- 				<span class="text-positive">[답글: 원글에 따라 공개]</span> -->
<%-- 			</c:if> --%>
		</c:when>
            
		<c:otherwise>
			 <%-- 일반 사용자일 경우: 체크박스 표시 --%>
			<input type="checkbox" name="csBoardSecret" value="Y" 
				<c:if test="${parentSecret == 'Y'}">checked="checked"</c:if> >
			<span class="text-positive">비공개로 작성</span>
		</c:otherwise>
	</c:choose>
    
    
    <div class="cell">
        <label>제목 *</label>
        <input type="text" name="csBoardTitle" required class="field w-100">
    </div>
    <div class="cell">
        <label>내용 *</label>
        <textarea name="csBoardContent" class="summernote-editor" id = "csBoardContent"></textarea>
    </div>
    <div class="cell right">
        <a href="list" class="btn btn-neutral">목록으로</a>
        <button class="btn btn-positive" type="submit">등록하기</button>
    </div>
</form>
</div>
<!-- <div class="flex-fill"></div> -->


<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
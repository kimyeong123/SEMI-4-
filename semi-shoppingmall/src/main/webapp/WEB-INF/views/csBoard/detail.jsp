<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<!-- 컨테이너 -->
<div class="container w-800">
	<!-- 제목 -->
	<div class="cell">
		<h1>
			${csBoardDto.csBoardTitle} 
			<c:if test="${csBoardDto.csBoardEtime != null}">
			(수정됨)
			</c:if>
		</h1>
	</div>
	
	<!-- 작성자 -->
	<div class="cell">
		<c:choose>
			<c:when test="${memberDto == null}">탈퇴한사용자</c:when>
			<c:otherwise>
				<a href="/member/detail?memberId=${memberDto.memberId}">
					${memberDto.memberNickname}
				</a>  
				(${memberDto.memberLevel})
			</c:otherwise>
		</c:choose>
	</div>
	
	<!-- 작성일 및 조회수 -->
	<div class="cell">
		<fmt:formatDate value="${csBoardDto.csBoardWtime}" pattern="yyyy-MM-dd HH:mm"/>
		 
		<span class="blue ms-40">
			<i class="fa-solid fa-eye"></i> ${csBoardDto.csBoardRead}
		</span>
	</div>
	<hr/>
	
	<!-- 본문 -->
	<div class="cell" style="min-height: 200px">
		${csBoardDto.csBoardContent}
	</div>
	
	<hr>
	
	<div class="cell">
		<a href="write" class="btn btn-positive">글쓰기</a> 
		<a href="write?csBoardOrigin=${csBoardDto.csBoardNo}" class="btn btn-positive">답글쓰기</a> 
		<%-- 내 글일 경우 수정 삭제를 모두 표시 --%>
		<c:if test="${sessionScope.loginId != null}">
		<c:choose>
			<c:when test="${sessionScope.loginId == csBoardDto.csBoardWriter}">
				<a href="edit?csBoardNo=${csBoardDto.csBoardNo}" class="btn btn-negative">수정</a> 
				<a href="delete?csBoardNo=${csBoardDto.csBoardNo}" class="btn btn-negative">삭제</a>
			</c:when>
			<c:when test="${sessionScope.loginLevel == '관리자'}">
				<a href="delete?csBoardNo=${csBoardDto.csBoardNo}" class="btn btn-negative">삭제</a>
			</c:when>
		</c:choose>
		</c:if>
		<a href="list" class="btn btn-neutral">목록</a>
	</div>
		
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
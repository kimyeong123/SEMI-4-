<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page = "/WEB-INF/views/template/header.jsp"></jsp:include>

<link rel="stylesheet" type="text/css" href="/css/commons.css">
<style>
	.board-title-link
	{
		text-decoration:none;
		color: #6c5ce7;
		display: inline-block;
		transition-property: color, transform;
		transition-duration: 0.1s;
		transition-timing-function: ease-out;
	}
	.board-title-link:hover
	{
 		color: #d63031;
		transform: scale(1.01);
	}
</style>


<div class="container w-850">
	<div class="cell center" >
		<h1>문의계시판</h1>
	</div>
	<div class="cell">
		타인에 대한 무분별한 비방 또는 욕설은 제제당할 수 있습니다
	</div>	
	<c:choose>
		<c:when test = "${sessionScope.loginId != null}">
			<div class="cell">
					<a class = "btn btn-netural" href="write">글쓰기</a>
			</div>
		</c:when>
		<c:otherwise>
	<!--상대경로 		../member/login -->
			<h2 class = "btn btn-netural"><a href = "/member/join">회원 가입</a>후 <a href = "/member/login">로그인</a>해야 글을 작성할 수 있습니다</h2>
	<!-- 		<h2><a href = "/member/login">로그인</a>해야 글을 작성할 수 있습니다</h2> -->
		</c:otherwise>
	</c:choose>
	<div class = "cell">
<%-- 		<h2>글 ${ isSearch ? "검색" : "목록"}</h2> --%>
		<h3>계시된 글 개수: ${csBoardList.size()}</h3>
	</div>
	
	<%-- 페이지 네비게이터  PageVO의 내용을 토대로 작성 --%>
	<jsp:include page="/WEB-INF/views/template/pagination.jsp"></jsp:include>
		

	

		<table class="table w-100 table-border table-hover table-striped mt-30" >
			<thead>
				<tr>
					<th>번호</th>
					<th width="40%">제목</th>
					<th width = "10%">작성자</th>
					<th width = "10%">작성일</th>
					<th width = "10%">수정일</th>
					<th>조회 수</th>
					<th>댓글 수</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="csBoardListVO" items="${csBoardList}" varStatus="status">
					<tr>
						
						<td>${csBoardListVO.getCsBoardNo()}</td>
						<td style=" ">
							<div class ="flex-box" style ="width: 300px; padding-left:${csBoardListVO.csBoardDepth * 20  + 10}px">
<%-- 							<div class="flex-box" style="width:400px; padding-left:${boardListVO.boardDepth * 20  + 10}px"> --%>
								<c:if test="${csBoardListVO.csBoardDepth > 0}">
									<img src="/images/reply.png" width="16" height="16">
								</c:if>
							
								<%-- 공지사항인 경우는 제목앞에 (공지) 추가 --%>
								<c:if test="${csBoardListVO.csBoardNotice == 'Y'}">
									<span class="badge">공지</span>
								</c:if>
								
								<a class="ellipsis" href="detail?boardNo=${csBoardListVO.csBoardNo}"  class="board-title-link ">${csBoardListVO.csBoardTitle}</a>
							</div>
						</td>
						<td>${csBoardListVO.csBoardWriter == null ? '(탈퇴한사용자)' : csBoardListVO.csBoardWriter}</td>
						<td>
									${csBoardListVO.getCsBoardWriteTime() }
						</td>
						<td>${csBoardListVO.csBoardEtime }</td>
						<td>${csBoardListVO.csBoardRead}</td>
						<td>${csBoardListVO.csBoardReply}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	<c:choose>
		<c:when test = "${sessionScope.loginId != null}">
			<div class="cell">
					<a class = "btn btn-netural" href="write">글쓰기</a>
			</div>
		</c:when>
		<c:otherwise>
	<!--상대경로 		../member/login -->
				<h2><a href = "/member/join">회원 가입</a>후 <a href = "/member/login">로그인</a>해야 글을 작성할 수 있습니다</h2>
		</c:otherwise>
	</c:choose>
	
	<form action="list" method="get">
		<div class = "center mb-30">
			<select class="field" name="column">
				<option value="board_title" ${column == 'cs_board_title' ? 'selected' : ''}>글 제목</option>
				<option value="board_writer" ${column == 'cs_board_writer' ? 'selected' : '' }>작성자</option>
			</select>
			 <input class = "field" type="text" name="keyword"  placeholder = "검색어"  value = "${keyword }"  required>
			<button class ="btn btn-netural" type ="submit">검색</button>
		</div>
	</form>
	</div>	



<jsp:include page="/WEB-INF/views/template/pagination.jsp"></jsp:include>

<jsp:include page = "/WEB-INF/views/template/footer.jsp"></jsp:include>
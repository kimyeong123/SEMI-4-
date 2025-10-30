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
	
	.board-secret-title 
	{
		color: #4a4a4a;
		font-weight: bold;
		cursor: default;
		padding-left: 5px;	
	}
	
	table a:link 
	{
		color : black  !important;
		text-decoration: underline;
	}
	
	table a:visited 
	{
  		color : black !important;
		text-decoration: underline;
	}
	
	.notice-row {
    background-color: #f0f8ff !important; /* 공지사항 배경색 (예: 하늘색 계열) */
    font-weight: bold;        /* 글씨 강조 */
    }
    .btn.just-cell:hover
    {
	    filter: none !important;
	    cursor: default;
    }
    

	
	
</style>


<div class="container w-850 mb-30">
	<div class="cell center" >
		<h1>문의게시판</h1>
	</div>
	<div class="cell">
		타인에 대한 무분별한 비방 또는 욕설은 제제당할 수 있습니다
	</div>	
	<div class="cell">
		<c:choose>
			<c:when test = "${sessionScope.loginId != null}">
				<a class = "btn btn-netural" href="write">글쓰기</a>	
			</c:when>
			<c:otherwise>
				<span class="btn just-cell"><a href = "/member/join">회원 가입</a>후 <a href = "/member/login">로그인</a>해야 글을 작성할 수 있습니다</span>
			</c:otherwise>
		</c:choose>
	</div>

	<div class = "cell">
<%-- 		<h2>글 ${ isSearch ? "검색" : "목록"}</h2> --%>
		<h3>게시된 글 개수: ${csBoardList.size()}</h3>
	</div>
	
		<table class="table w-100 table-border table-hover table-striped mt-30" >
			<thead>
				<tr>
					<th width="10%">번호</th>
					<th width="40%">제목</th>
					<th width = "10%">작성자</th>
					<th width = "10%">작성일</th>
					<th width = "10%">수정일</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="csBoardListVO" items="${csBoardList}" varStatus="status">
					<c:set var="isNotice"  value = "${status.index < noticeCount }"/>
					<tr class = "${isNotice ? 'notice-row' : '' }">
						
						<td>${csBoardListVO.csBoardNo}</td>
						
						<td>
							<c:choose>
								<c:when test="${isNotice}">
									<div class ="flex-box" style ="width: 300px; padding-left:${csBoardListVO.csBoardDepth * 20  + 10}px">
										<c:if test="${csBoardListVO.csBoardDepth > 0}">
											<img src="/images/reply.png" width="16" height="16">
										</c:if>
									
										<%-- 공지사항인 경우는 제목앞에 (공지) 추가 --%>
										<div class ="flex-box flex-center">
											<i class="fa-solid fa-bullhorn me-10"></i>
										</div>
										<span>공지</span>
										
										<%--비공개 여부에 따라 링크를  조건부 처리 --%>
										<c:set var ="isSecret" value = "${csBoardListVO.csBoardSecret == 'Y' }" />
										<c:set var ="canAccessSecret" value = "${sessionScope.loginLevel == '관리자' || sessionScope.loginId == csBoardListVO.csBoardWriter || sessionScope.loginId == csBoardListVO.csBoardOriginWriter }" />								
										<c:choose>
											<c:when test = 	"${ isSecret && !canAccessSecret}">
												<%--비공개 글은 일반 사용자라면 링크 무효 --%>
												<span class="board-secret-title ellipsis">
													<i class="fa-solid fa-lock"></i> 
												</span>
												${csBoardListVO.csBoardTitle}
											</c:when>
											<c:otherwise>
												<%--공개 글 또는 비공개글의 작성자와 관리자의 경우 --%>
												<a class="ellipsis" href="detail?csBoardNo=${csBoardListVO.csBoardNo}"  class="board-title-link ">
													<c:if test="${isSecret }">
														<i class="fa-solid fa-lock-open"></i>												
													</c:if>
													${csBoardListVO.csBoardTitle}										
												</a>
											</c:otherwise>
										</c:choose>
										
									</div>								
								</c:when>
								<c:otherwise>
									<div class ="flex-box" style ="width: 300px; padding-left:${csBoardListVO.csBoardDepth * 20  + 10}px">
										<c:if test="${csBoardListVO.csBoardDepth > 0}">
											<img src="/images/reply.png" width="16" height="16">
										</c:if>
									
										<%-- 공지사항인 경우는 제목앞에 (공지) 추가 --%>
										<c:if test="${csBoardListVO.csBoardNotice == 'Y'}">
											<div class ="flex-box flex-center">
												<i class="fa-solid fa-bullhorn me-10"></i>
											</div>
											<span>공지</span>
										</c:if>
										
										<%--비공개 여부에 따라 링크를  조건부 처리 --%>
										<c:set var ="isSecret" value = "${csBoardListVO.csBoardSecret == 'Y' }" />
										<c:set var ="canAccessSecret" value = "${sessionScope.loginLevel == '관리자' || sessionScope.loginId == csBoardListVO.csBoardWriter || sessionScope.loginId == csBoardListVO.csBoardOriginWriter }" />								
										<c:choose>
											<c:when test = 	"${ isSecret && !canAccessSecret}">
												<%--비공개 글은 일반 사용자라면 링크 무효 --%>
												<span class="board-secret-title ellipsis">
													<i class="fa-solid fa-lock"></i> 
												</span>
												${csBoardListVO.csBoardTitle}
											</c:when>
											<c:otherwise>
												<%--공개 글 또는 비공개글의 작성자와 관리자의 경우 --%>
												<a class="ellipsis" href="detail?csBoardNo=${csBoardListVO.csBoardNo}"  class="board-title-link ">
													<c:if test="${isSecret }">
														<i class="fa-solid fa-lock-open"></i>												
													</c:if>
													${csBoardListVO.csBoardTitle}										
												</a>
											</c:otherwise>
										</c:choose>
										
									</div>
								</c:otherwise>
								
							</c:choose>
						</td>
<%-- 						<td>${csBoardListVO.csBoardWriter == null ? '(탈퇴한사용자)' : csBoardListVO.csBoardWriter}</td> --%>
						<td>${csBoardListVO.csBoardWriter == null ? '(탈퇴한사용자)' : csBoardListVO.csBoardMemberNickname}</td>
						<td>
							<c:choose>
								<c:when test="${csBoardListVO.wtimeRecent}">
									<fmt:formatDate value = "${csBoardListVO.csBoardWtime}" pattern="HH:mm"/>
								</c:when>
								<c:otherwise>
									<fmt:formatDate value = "${csBoardListVO.csBoardWtime}" pattern="yy.MM.dd"/>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
								<c:when test="${csBoardListVO.etimeRecent}">								
									<fmt:formatDate value = "${csBoardListVO.csBoardEtime }" pattern="HH:mm"/>
								</c:when>
								<c:otherwise>									
									<fmt:formatDate value = "${csBoardListVO.csBoardEtime }" pattern="yy.MM.dd"/>
								</c:otherwise>
							</c:choose>
						</td>
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
				<option value="cs_board_title" ${column == 'cs_board_title' ? 'selected' : ''}>글 제목</option>
				<option value="cs_board_writer" ${column == 'cs_board_writer' ? 'selected' : '' }>작성자</option>
			</select>
			 <input class = "field" type="text" name="keyword"  placeholder = "검색어"  value = "${keyword }"  required>
			<button class ="btn btn-netural" type ="submit">검색</button>
		</div>
	</form>
	<jsp:include page="/WEB-INF/views/template/pagination.jsp"></jsp:include>
</div>	




<jsp:include page = "/WEB-INF/views/template/footer.jsp"></jsp:include>
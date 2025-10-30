<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page = "/WEB-INF/views/template/header.jsp"></jsp:include>

<link rel="stylesheet" type="text/css" href="/css/commons.css">
<script type="text/javascript">
	$(function () {
		$("#all-show").on("click", function () {
			$(this).prop("disabled", true);
			$("#notice-list-show").prop("disabled", false);
			$("#inquiry-list-show").prop("disabled", false);
			$("#csBoardAll").show();
			$("#csBoardNotice").hide();
			$("#csBoardInquiry").hide();
			$("#number-hide").show();
		});
		$("#notice-list-show").on("click", function () {
			$(this).prop("disabled", true);
			$("#all-show").prop("disabled", false);
			$("#inquiry-list-show").prop("disabled", false);
			$("#csBoardAll").hide();
			$("#csBoardNotice").show();
			$("#csBoardInquiry").hide();
			$("#number-hide").show();
		});
		$("#inquiry-list-show").on("click", function () {
			$(this).prop("disabled", true);
			$("#all-show").prop("disabled", false);
			$("#notice-list-show").prop("disabled", false);
			$("#csBoardAll").hide();
			$("#csBoardNotice").hide();
			$("#csBoardInquiry").show();
			$("#number-hide").show();
		});
	
	});
</script>

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
    
	#csBoardNotice 
	{
		display: none;
	}
	
	#csBoardInquiry 
	{
		display: none;
	}

	/* 기본 버튼 스타일 */
    .tab-button {
        display: inline-block;
        padding: 8px 16px;
        margin-right: 8px;
        border-radius: 8px;
        transition: all 0.2s;
        border: 1px solid #A0AEC0; 
        
        background-color: var(--tab-color-light);
        color: var(--text-color-default);
        font-weight: 500;
        cursor: pointer;
    }

    /* disabled=true (즉, 선택되어 활성화된 탭) 스타일 */
    /* 볼드 처리 및 색상 진하게 */
    .tab-button:disabled {
        background-color: var(--tab-color-active);
        color: var(--text-color-active);
        font-weight: bold; 
        cursor: default;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); 
    }

    /* 비활성 버튼 호버 효과 */
    .tab-button:not(:disabled):hover {
        background-color: #A0AEC0; 
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

	<div class="cell flex-box">
	    <button id="all-show" class="tab-button" disabled>전체</button>
	    <button id="notice-list-show" class="tab-button">공지</button>
	    <button id="inquiry-list-show" class="tab-button">문의</button>
	</div>

<%--여기부터 전체테이블 --%>
	<div class = "cell" id="csBoardAll">
<%-- 		<h2>글 ${ isSearch ? "검색" : "목록"}</h2> --%>
		<h3>게시된 글 개수: ${csBoardList.size()}</h3>
	
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
		    
		    <%-- 1. 공지글 포함 전체 리스트는 무조건 출력 (csBoardList는 공지+일반글) --%>
            <c:forEach var="csBoardListVO" items="${csBoardList}" varStatus="status">
                <c:set var="isNotice"  value = "${status.index < noticeCount }"/>
                <tr class = "${isNotice ? 'notice-row' : '' }">
                    
                    <td>${csBoardListVO.csBoardNo}</td>
                    
                    <td>
                        <%-- 1. 공통 DIV 시작: 들여쓰기 및 답글 이미지 처리 --%>
                        <div class ="flex-box" style ="width: 300px; padding-left:${csBoardListVO.csBoardDepth * 20  + 10}px">
                            <c:if test="${csBoardListVO.csBoardDepth > 0}">
                                <img src="/images/reply.png" width="16" height="16">
                            </c:if>
    
                            <%-- 2. 공지사항 마크업 처리 (isNotice 조건 사용) --%>
                            <c:if test="${isNotice}"> 
                                <div class ="flex-box flex-center">
                                    <i class="fa-solid fa-bullhorn me-10"></i>
                                </div>
                                <span>공지</span>
                            </c:if>
                            
                            <%-- 3. 비공개 여부/접근 권한 변수 설정 (공통) --%>
                            <c:set var ="isSecret" value = "${csBoardListVO.csBoardSecret == 'Y' }" />
                            <c:set var ="canAccessSecret" value = "${sessionScope.loginLevel == '관리자' || sessionScope.loginId == csBoardListVO.csBoardWriter || sessionScope.loginId == csBoardListVO.csBoardOriginWriter }" />								
                            
                            <%-- 4. 제목 출력 및 링크 처리 (공통) --%>
                            <c:choose>
                                <c:when test = 	"${ isSecret && !canAccessSecret}">
                                    <%-- 비공개 글이고 접근 권한이 없을 경우: 링크 무효 처리 --%>
                                    <span class="board-secret-title ellipsis">
                                        <i class="fa-solid fa-lock"></i>
                                        <span>비공개 글입니다</span>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <%-- 공개 글 또는 비공개 글의 작성자/관리자의 경우: 링크 활성화 --%>
                                    <a class="ellipsis" href="detail?csBoardNo=${csBoardListVO.csBoardNo}"  class="board-title-link ">
                                        <c:if test="${isSecret}">
                                            <i class="fa-solid fa-lock-open"></i>												
                                        </c:if>
                                        <span>${csBoardListVO.csBoardTitle}</span>										
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>								
                    </td>
                    
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
		    
		    <%--  2. 검색 결과가 없을 때 메시지 출력 (totalListCount 활용)  --%>
            <c:if test="${totalListCount == 0}">
				<tr>
					<td colspan="5" class="center p-30" style="color: #777;">
						<i class="fa-solid fa-triangle-exclamation me-5"></i>
						검색 결과가 없습니다. 다른 검색어로 다시 시도해 주세요.
					</td>
				</tr>		        
            </c:if>

		    </tbody>
		</table>
	
	</div>
<%--여기까지 전체 테이블 --%>

<%--여기부터 공지테이블 --%>
	<div class = "cell" id="csBoardNotice">
<%-- 		<h2>글 ${ isSearch ? "검색" : "목록"}</h2> --%>
		<h3>게시된 글 개수: ${noticeListResult.size()}</h3>
	
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
		    
		    <c:choose>
		    <%-- 1. noticeListResult에 데이터가 있을 경우 (<tbody> 내용 출력) --%>
            <c:when test="${not empty noticeListResult}">
                <c:forEach var="csBoardListVO" items="${noticeListResult}" varStatus="status">
                    <c:set var="isNotice"  value = "${status.index < noticeCount }"/>
                    <tr class = "${isNotice ? 'notice-row' : '' }">
                        
                        <td>${csBoardListVO.csBoardNo}</td>
                        
                        <td>
                            <%-- 1. 공통 DIV 시작: 들여쓰기 및 답글 이미지 처리 --%>
                            <div class ="flex-box" style ="width: 300px; padding-left:${csBoardListVO.csBoardDepth * 20  + 10}px">
                                <c:if test="${csBoardListVO.csBoardDepth > 0}">
                                    <img src="/images/reply.png" width="16" height="16">
                                </c:if>
        
                                <%-- 2. 공지사항 마크업 처리 (isNotice 조건 사용) --%>
                                <c:if test="${isNotice}"> 
                                    <div class ="flex-box flex-center">
                                        <i class="fa-solid fa-bullhorn me-10"></i>
                                    </div>
                                    <span>공지</span>
                                </c:if>
                                
                                <%-- 3. 비공개 여부/접근 권한 변수 설정 (공통) --%>
                                <c:set var ="isSecret" value = "${csBoardListVO.csBoardSecret == 'Y' }" />
                                <c:set var ="canAccessSecret" value = "${sessionScope.loginLevel == '관리자' || sessionScope.loginId == csBoardListVO.csBoardWriter || sessionScope.loginId == csBoardListVO.csBoardOriginWriter }" />								
                                
                                <%-- 4. 제목 출력 및 링크 처리 (공통) --%>
                                <c:choose>
                                    <c:when test = 	"${ isSecret && !canAccessSecret}">
                                        <%-- 비공개 글이고 접근 권한이 없을 경우: 링크 무효 처리 --%>
                                        <span class="board-secret-title ellipsis">
                                            <i class="fa-solid fa-lock"></i>
                                            <span>비공개 글입니다</span>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <%-- 공개 글 또는 비공개 글의 작성자/관리자의 경우: 링크 활성화 --%>
                                        <a class="ellipsis" href="detail?csBoardNo=${csBoardListVO.csBoardNo}"  class="board-title-link ">
                                            <c:if test="${isSecret}">
                                                <i class="fa-solid fa-lock-open"></i>												
                                            </c:if>
                                            <span>${csBoardListVO.csBoardTitle}</span>										
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>								
                        </td>
                        
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
            </c:when>
		    
		    <%-- 2. noticeListResult가 비어있을 경우 (메시지 출력) --%>
            <c:otherwise>
				<tr>
					<td colspan="5" class="center p-30" style="color: #777 !important;">
						<i class="fa-solid fa-triangle-exclamation me-5"></i>
						공지글 검색 결과가 없습니다.
					</td>
				</tr>		        
            </c:otherwise>
            </c:choose>
            
		    </tbody>
		</table>
	
	</div>
<%--여기까지 공지 테이블 --%>

<%--여기부터 문의 테이블 --%>
	<div class = "cell" id="csBoardInquiry">
<%-- 		<h2>글 ${ isSearch ? "검색" : "목록"}</h2> --%>
		<h3>게시된 글 개수: ${inquiryListResult.size()}</h3>
	
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
		    
            <c:forEach var="csBoardListVO" items="${inquiryListResult}" varStatus="status">
                <tr >
                    
                    <td>${csBoardListVO.csBoardNo}</td>
                    
                    <td>
                        <%-- 1. 공통 DIV 시작: 들여쓰기 및 답글 이미지 처리 (공지 마크업 제거) --%>
                        <div class ="flex-box" style ="width: 300px; padding-left:${csBoardListVO.csBoardDepth * 20  + 10}px">
                            <c:if test="${csBoardListVO.csBoardDepth > 0}">
                                <img src="/images/reply.png" width="16" height="16">
                            </c:if>
    
                            <%-- 2. 공지사항 마크업 처리 (제거됨) --%>
                            
                            <%-- 3. 비공개 여부/접근 권한 변수 설정 (공통) --%>
                            <c:set var ="isSecret" value = "${csBoardListVO.csBoardSecret == 'Y' }" />
                            <c:set var ="canAccessSecret" value = "${sessionScope.loginLevel == '관리자' || sessionScope.loginId == csBoardListVO.csBoardWriter || sessionScope.loginId == csBoardListVO.csBoardOriginWriter }" />								
                            
                            <%-- 4. 제목 출력 및 링크 처리 (공통) --%>
                            <c:choose>
                                <c:when test = 	"${ isSecret && !canAccessSecret}">
                                    <%-- 비공개 글이고 접근 권한이 없을 경우: 링크 무효 처리 --%>
                                    <span class="board-secret-title ellipsis">
                                        <i class="fa-solid fa-lock"></i>
                                        <span>비공개 글입니다</span>
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <%-- 공개 글 또는 비공개 글의 작성자/관리자의 경우: 링크 활성화 --%>
                                    <a class="ellipsis" href="detail?csBoardNo=${csBoardListVO.csBoardNo}"  class="board-title-link ">
                                        <c:if test="${isSecret}">
                                            <i class="fa-solid fa-lock-open"></i>												
                                        </c:if>
                                        <span>${csBoardListVO.csBoardTitle}</span>										
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </div>								
                    </td>
                    
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
		    
		    <%--  2. 검색 결과가 없을 때 메시지 출력 (inquiryListCount 활용)  --%>
            <c:if test="${inquiryListCount == 0}">
				<tr>
					<td colspan="5" class="center p-30" style="color: #777;">
						<i class="fa-solid fa-triangle-exclamation me-5"></i>
						검색 결과가 없습니다. 다른 검색어로 다시 시도해 주세요.
					</td>
				</tr>		        
            </c:if>

		    </tbody>
		</table>
	
	</div>
<%--여기까지 문의 테이블 --%>

	
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
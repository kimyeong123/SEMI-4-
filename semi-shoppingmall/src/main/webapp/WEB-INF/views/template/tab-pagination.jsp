<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%-- 
	[수정]: list.jsp에서 param으로 전달받은 것은 currentDataCount 뿐이므로,
	pageVO와 tabType에 대한 <c:set>은 제거하고 Request Scope를 통해 직접 접근합니다.
--%>
<c:set var="currentDataCount" value="${param.currentDataCount}" /> 


<%-- pageVO, tabType, currentURL은 Request Scope를 통해 자동 접근됩니다. --%>

<c:if test="${currentDataCount != null && currentDataCount > 0}">


<%-- 페이지 네비게이터 (pageVO의 내용을 토대로 작성) --%>
<div class="pagination">

<c:if test="${pageVO.firstBlock == false}">
	<a href="${currentURL == null ? 'list' : currentURL}?page=${pageVO.prevPage}&tabType=${tabType}&${pageVO.searchParams}">&lt;</a>
</c:if>

<c:forEach var="i" begin="${pageVO.blockStart}" end="${pageVO.blockFinish}" step="1">
	<c:choose>
		<c:when test="${pageVO.page == i}">
			<%-- 현재 페이지일 경우 클릭하여 이동할 수 없도록 처리 --%>
			<a class="on">${i}</a>		
		</c:when>
		<c:otherwise>
			<%-- 현재 페이지가 아닐 경우 클릭하여 이동하도록 처리 --%>
			<a href="${currentURL == null ? 'list' : currentURL}?page=${i}&tabType=${tabType }&${pageVO.searchParams}">${i}</a>		
		</c:otherwise>
	</c:choose>
</c:forEach>

<c:if test="${pageVO.lastBlock == false}">
<a href="${currentURL == null ? 'list' : currentURL}?page=${pageVO.nextPage}&tabType=${tabType}&${pageVO.searchParams}">&gt;</a>
</c:if>

</div>

</c:if>
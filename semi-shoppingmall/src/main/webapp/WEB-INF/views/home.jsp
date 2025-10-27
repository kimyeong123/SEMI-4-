<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!-- 정적 include -->
<%-- <%@ include file="/WEB-INF/views/template/header.jsp" %> --%>

<!-- 동적 include -->
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
	컨텐츠
<a href="/product/list">list 임시</a>
<a href="/csBoard/list">고객 센터</a>
<%-- <%@ include file="/WEB-INF/views/template/footer.jsp" %> --%>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
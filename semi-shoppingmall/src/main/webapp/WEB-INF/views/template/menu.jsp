<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>    

<!-- 기존 메뉴(한줄짜리) -->
<div class="menu flex-box">
	<nav class="category-menu">
    <ul>
        <%-- 최상위 카테고리 반복 --%>
        <c:forEach var="topCategory" items="${categoryTree}">
            <li>
                <%-- 최상위 카테고리 링크 --%>
                <a href="/products?categoryNo=${topCategory.categoryNo}">${topCategory.categoryName}</a>

                <%-- 하위 카테고리가 있는지 확인 --%>
                <c:if test="${not empty topCategory.children}">
                    <ul class="sub-menu"> <%-- 하위 메뉴 (CSS로 숨김 처리) --%>
                        <%-- 하위 카테고리 반복 --%>
                        <c:forEach var="subCategory" items="${topCategory.children}">
                            <li>
                                <a href="/products?categoryNo=${subCategory.categoryNo}">${subCategory.categoryName}</a>
                            </li>
                        </c:forEach>
                    </ul>
                </c:if>
            </li>
        </c:forEach>
    </ul>
</nav>
</div>
<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"/>

<div class="container w-800">
    <h1>카테고리 목록</h1>

    <table border="1" cellpadding="5" class="w-100">
        <thead>
            <tr>
                <th>번호</th>
                <th>카테고리 이름</th>
                <th>부모 번호</th>
                <th>작업</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="c" items="${categoryList}">
                <tr>
                    <td>${c.categoryNo}</td>
                    <td>${c.categoryName}</td>
                    <td>
                        <c:choose>
                            <c:when test="${c.parentCategoryNo != null}">
                                ${c.parentCategoryNo}
                            </c:when>
                            <c:otherwise>
                                -
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a href="detail?categoryNo=${c.categoryNo}">상세</a> |
                        <a href="edit?categoryNo=${c.categoryNo}">수정</a> |
                        <a href="delete?categoryNo=${c.categoryNo}">삭제</a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div class="mt-20">
        <a href="add" class="btn btn-positive">카테고리 추가</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"/>

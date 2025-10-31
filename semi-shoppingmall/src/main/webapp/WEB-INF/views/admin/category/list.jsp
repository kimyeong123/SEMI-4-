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
                <th>작업</th>
            </tr>
        </thead>
        <tbody>
            <!-- 상위 카테고리 먼저 -->
            <c:forEach var="c" items="${categoryList}">
                <c:if test="${c.parentCategoryNo == null || c.parentCategoryNo == 0}">
                    <tr style="background-color: #f0f0f0; font-weight: bold;">
                        <td>${c.categoryNo}</td>
                        <td>${c.categoryName}</td>
                        <td>                       
                            <a href="edit?categoryNo=${c.categoryNo}">수정</a> |
                            <a href="delete?categoryNo=${c.categoryNo}">삭제</a>
                        </td>
                    </tr>

                    <!-- 하위 카테고리 표시 -->
                    <c:forEach var="sub" items="${categoryList}">
                        <c:if test="${sub.parentCategoryNo == c.categoryNo}">
                            <tr>
                                <td>${sub.categoryNo}</td>
                                <td style="padding-left: 30px;"> ${sub.categoryName}</td>
                                <td>                              
                                    <a href="edit?categoryNo=${sub.categoryNo}">수정</a> |
                                    <a href="delete?categoryNo=${sub.categoryNo}">삭제</a>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </c:if>
            </c:forEach>
        </tbody>
    </table>

    <div class="mt-20">
        <a href="add" class="btn btn-positive">카테고리 추가</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"/>

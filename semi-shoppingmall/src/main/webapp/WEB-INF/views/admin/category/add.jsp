<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"/>

<div class="container w-600">
    <h1>카테고리 등록</h1>

    <form action="add" method="post">
        <!-- 부모 카테고리 선택 (없으면 최상위 카테고리) -->
        <div class="cell">
            <label>상위 카테고리 (없으면 최상위)</label>
            <select name="parentCategoryNo" class="field w-100">
                <option value="">최상위 카테고리</option>
                <c:forEach var="c" items="${parentCategoryList}">
                    <option value="${c.categoryNo}">${c.categoryName}</option>
                </c:forEach>
            </select>
        </div>

        <!-- 카테고리 이름 -->
        <div class="cell">
            <label>카테고리 이름 *</label>
            <input type="text" name="categoryName" required class="field w-100">
        </div>

        <div class="cell mt-30">
            <button class="btn btn-positive w-100">카테고리 등록</button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"/>

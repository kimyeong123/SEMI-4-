<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<option value="">선택</option>
<c:forEach var="c" items="${childCategoryList}">
    <option value="${c.categoryNo}">${c.categoryName}</option>
</c:forEach>

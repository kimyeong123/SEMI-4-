<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<h1>상품 상세정보</h1>

<c:if test="${product.productThumbnailNo != null}">
    <img src="/attachment/image?attachmentNo=${product.productThumbnailNo}" width="100" height="100">
</c:if>
<br><br>

<table border="1" width="400">
    <tr>
        <th width="25%">번호</th>
        <td>${product.productNo}</td>
    </tr>
    <tr>
        <th>이름</th>
        <td>${product.productName}</td>
    </tr>
    <tr>
        <th>가격</th>
        <td>${product.productPrice}</td>
    </tr>
    <tr>
        <th>설명</th>
        <td>${product.productContent}</td>
    </tr>
    <tr>
        <th>평균 평점</th>
        <td><c:out value="${product.productAvgRating}" default="-" /></td>
    </tr>
</table>

<h2><a href="list">목록으로 이동</a></h2>
<h2><a href="edit?productNo=${product.productNo}">수정하기</a></h2>
<h2>
    <form action="delete" method="post" onsubmit="return confirm('정말 삭제하시겠습니까?');">
        <input type="hidden" name="productNo" value="${product.productNo}">
        <button type="submit">삭제하기</button>
    </form>
</h2>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

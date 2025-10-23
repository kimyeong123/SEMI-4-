<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>상품 목록</h1>
<h2><a href="add">신규 등록</a></h2>

<!-- 성공 / 오류 메시지 -->
<c:if test="${not empty message}">
    <p style="color:green;">${message}</p>
</c:if>
<c:if test="${not empty error}">
    <p style="color:red;">${error}</p>
</c:if>

<h2>상품 수 : ${productList.size()}</h2>

<!-- 검색창 -->
<form action="list" method="get">
    <select name="column">
        <option value="product_name" <c:if test="${param.column == 'product_name'}">selected</c:if>>상품명</option>
        <option value="product_content" <c:if test="${param.column == 'product_content'}">selected</c:if>>설명</option>
    </select>
    <input type="search" name="keyword" value="${param.keyword}">
    <button>검색</button>
</form>

<table border="1" width="800" align="center">
    <thead>
        <tr>
            <th>썸네일</th>
            <th>번호</th>
            <th>상품명</th>
            <th>가격</th>
            <th>평균 평점</th>
            <th>관리</th> <!-- 수정/삭제 버튼 -->
        </tr>
    </thead>
    <tbody align="center">
        <c:forEach var="product" items="${productList}">
            <tr>
                <td>
                    <c:if test="${product.productThumbnailNo != null}">
                        <img src="/attachment/image?attachmentNo=${product.productThumbnailNo}" 
                             width="50" height="50" alt="썸네일">
                    </c:if>
                </td>
                <td>${product.productNo}</td>
                <td>
                    <a href="view?productNo=${product.productNo}">
                        ${product.productName}
                    </a>
                </td>
                <td>${product.productPrice}</td>
                <td>
                    <c:choose>
                        <c:when test="${product.productAvgRating != null}">
                            ${product.productAvgRating}
                        </c:when>
                        <c:otherwise>-</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a href="edit?productNo=${product.productNo}">수정</a> |
                    <form action="delete" method="post" style="display:inline;"
                          onsubmit="return confirm('정말 삭제하시겠습니까?');">
                        <input type="hidden" name="productNo" value="${product.productNo}">
                        <button type="submit">삭제</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

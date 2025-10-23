<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<div class="container w-800">
    <h1>상품 상세정보</h1>

    <!-- 썸네일 표시 -->
    <c:if test="${product.productThumbnailNo != null}">
        <img src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${product.productThumbnailNo}"
             width="150" height="150" style="object-fit: cover;">
    </c:if>

    <br><br>

    <table border="1" width="100%">
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
            <td><c:out value="${product.productAvgRating}" default="-"/></td>
        </tr>
    </table>

    <div style="margin-top:20px;">
        <a href="list" class="btn btn-secondary">목록으로 이동</a>
        <a href="edit?productNo=${product.productNo}" class="btn">수정하기</a>

        <form action="${pageContext.request.contextPath}/admin/product/delete" method="post" style="display:inline;"
              onsubmit="return confirm('정말 삭제하시겠습니까?');">
            <input type="hidden" name="productNo" value="${product.productNo}">
            <button type="submit" class="btn btn-danger">삭제하기</button>
        </form>
    </div>

    <hr>

    <!-- ================= 리뷰 작성 폼 ================= -->
    <div>
        <h3>리뷰 작성</h3>
        <form id="reviewForm" action="${pageContext.request.contextPath}/review/add" method="post" enctype="multipart/form-data">
            <input type="hidden" name="productNo" value="${product.productNo}">
            
            <label>평점:</label>
            <select name="reviewRating">
                <option value="1">1점</option>
                <option value="2">2점</option>
                <option value="3" selected>3점</option>
                <option value="4">4점</option>
                <option value="5">5점</option>
            </select>
            <br><br>
            
            <label>내용:</label><br>
            <textarea name="reviewContent" rows="4" cols="50" placeholder="리뷰를 작성해주세요."></textarea>
            <br><br>

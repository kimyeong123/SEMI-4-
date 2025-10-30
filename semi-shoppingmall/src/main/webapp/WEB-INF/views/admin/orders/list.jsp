<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<link rel="stylesheet" href="/css/commons.css">

<div class="container w-1200">
    <h1>주문 관리</h1>

    <table class="table table-border table-hover w-100">
        <thead>
            <tr>
                <th>주문번호</th>
                <th>회원 ID</th>
                <th>상품명</th>
                <th>옵션</th>
                <th>수량</th>
                <th>총 금액</th>
                <th>수령인</th>
                <th>연락처</th>
                <th>주소</th>
                <th>상태</th>
                <th>변경</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="order" items="${orderList}">
                <tr>
                    <td>${order.ordersNo}</td>
                    <td>${order.ordersId}</td>
                    <td>${order.productName}</td>
                    <td>${order.optionName}</td>
                    <td>${order.orderAmount}</td>
                    <td><fmt:formatNumber value="${order.ordersTotalPrice}" pattern="#,###"/> 원</td>
                    <td>${order.ordersRecipient}</td>
                    <td>${order.ordersRecipientContact}</td>
                    <td>${order.ordersShippingAddress1} ${order.ordersShippingAddress2}</td>
                    <td>${order.ordersStatus}</td>
                    <td>
                        <form action="update" method="post" style="display:flex; gap:5px;">
                            <input type="hidden" name="ordersNo" value="${order.ordersNo}">
                            <select name="ordersStatus" class="field">
                                <c:forEach var="status" items="${statusList}">
                                    <option value="${status}" ${order.ordersStatus == status ? "selected" : ""}>${status}</option>
                                </c:forEach>
                            </select>
                            <button type="submit" class="btn btn-positive">변경</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty orderList}">
                <tr>
                    <td colspan="11" class="center">주문 내역이 없습니다.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- 템플릿 상단 include --%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<div class="container">
	<div class="cell center">
    	<h2>주문 상세 내역</h2>
	</div>
    <c:if test="${not empty orderSummaryList}">
        <%-- 주문 요약 정보 --%>
        <c:set var="orderInfo" value="${orderSummaryList[0]}" />

        <div class="order-summary" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee;">
            <h3>주문 정보</h3>
            <p><strong>주문 번호:</strong> ${orderInfo.ordersNo}</p>
            <p><strong>주문 날짜:</strong> <fmt:formatDate value="${orderInfo.ordersCreatedAt}" pattern="yyyy년 MM월 dd일 HH:mm"/></p>
            <p><strong>주문 상태:</strong> ${orderInfo.ordersStatus}</p>
            <p><strong>총 결제 금액:</strong> <fmt:formatNumber value="${orderInfo.ordersTotalPrice}" type="currency"/> 원</p>
        </div>

        <div class="shipping-info" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee;">
            <h3>배송 정보</h3>
            <p><strong>받는 분:</strong> ${orderInfo.ordersRecipient}</p>
            <p><strong>연락처:</strong> ${orderInfo.ordersRecipientContact}</p>
            <p><strong>주소:</strong> (${orderInfo.ordersShippingPost}) ${orderInfo.ordersShippingAddress1} ${orderInfo.ordersShippingAddress2}</p>
        </div>

        <div class="ordered-items">
            <h3>주문 상품</h3>
            <table style="width: 100%; border-collapse: collapse;">
                <thead>
                    <tr style="border-bottom: 1px solid #ccc;">
                        <th style="padding: 10px; text-align: left;" colspan="2">상품 정보</th>
                        <th style="padding: 10px; text-align: right;">수량</th>
                        <th style="padding: 10px; text-align: right;">상품 금액</th>
                        <th style="padding: 10px; text-align: right;">합계</th>
                    </tr>
                </thead>
                <tbody>
                    <%-- 주문 상품 목록 반복 --%>
                    <c:forEach var="item" items="${orderSummaryList}">
                        <tr style="border-bottom: 1px solid #eee;">
                            <td style="padding: 10px; width: 80px;">
                                <%-- 상품 썸네일 --%>
                                <c:if test="${not empty item.thumbnailName}">
                                    <img src="attachment/download?attachmentNo=${item.productThumbnailNo}" alt="${item.productName}" style="width: 70px; height: 70px; object-fit: cover;">
                                </c:if>
                            </td>
                            <td style="padding: 10px;">
                                <div>${item.productName}</div>
                                <div style="font-size: 0.9em; color: #777;">옵션: ${item.optionName}</div>
                            </td>
                            <td style="padding: 10px; text-align: right;">${item.orderAmount}</td>
                            <td style="padding: 10px; text-align: right;"><fmt:formatNumber value="${item.pricePerItem}" type="currency"/> 원</td>
                            <td style="padding: 10px; text-align: right;"><fmt:formatNumber value="${item.pricePerItem * item.orderAmount}" type="currency"/> 원</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

    </c:if>

    <div class="mt-20 mb-50 center">
    	<button type="button" class="btn btn-neutral" onclick="location.href='/orders/list'">목록으로 돌아가기</button>
    </div>

</div>

<%-- 템플릿 하단 include --%>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
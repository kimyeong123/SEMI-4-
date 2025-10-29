<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />

<div class="container w-800">
    <h1>📦 주문 상세 정보</h1>

    <c:if test="${not empty error}">
        <div class="cell center text-danger">${error}</div>
    </c:if>

    <c:if test="${not empty ordersDto}">
        <div class="table-wrapper mt-30">
            <table class="table table-bordered w-100">
                <tbody>
                    <tr>
                        <th class="w-25">주문번호</th>
                        <td>${ordersDto.ordersNo}</td>
                    </tr>
                    <tr>
                        <th>회원 ID</th>
                        <td>${ordersDto.ordersId}</td>
                    </tr>
                    <tr>
                        <th>수령인</th>
                        <td>${ordersDto.ordersRecipient}</td>
                    </tr>
                    <tr>
                        <th>연락처</th>
                        <td>${ordersDto.ordersRecipientContact}</td>
                    </tr>
                    <tr>
                        <th>주소</th>
                        <td>
                            (${ordersDto.ordersShippingPost}) 
                            ${ordersDto.ordersShippingAddress1}<br>
                            ${ordersDto.ordersShippingAddress2}
                        </td>
                    </tr>
                    <tr>
                        <th>총 결제 금액</th>
                        <td><fmt:formatNumber value="${ordersDto.ordersTotalPrice}" pattern="#,###"/> 원</td>
                    </tr>
                    <tr>
                        <th>주문 상태</th>
                        <td>
                            <form action="${pageContext.request.contextPath}/admin/orders/update" method="post" style="display:flex; align-items:center; gap:10px;">
                                <input type="hidden" name="ordersNo" value="${ordersDto.ordersNo}">
                                <select name="ordersStatus" class="field">
                                    <option value="결제완료" ${ordersDto.ordersStatus == '결제완료' ? 'selected' : ''}>결제완료</option>
                                    <option value="배송준비중" ${ordersDto.ordersStatus == '배송준비중' ? 'selected' : ''}>배송준비중</option>
                                    <option value="배송중" ${ordersDto.ordersStatus == '배송중' ? 'selected' : ''}>배송중</option>
                                    <option value="배송완료" ${ordersDto.ordersStatus == '배송완료' ? 'selected' : ''}>배송완료</option>
                                    <option value="주문취소" ${ordersDto.ordersStatus == '주문취소' ? 'selected' : ''}>주문취소</option>
                                    <option value="취소요청" ${ordersDto.ordersStatus == '취소요청' ? 'selected' : ''}>취소요청</option>
                                    <option value="반품요청" ${ordersDto.ordersStatus == '반품요청' ? 'selected' : ''}>반품요청</option>
                                    <option value="반품완료" ${ordersDto.ordersStatus == '반품완료' ? 'selected' : ''}>반품완료</option>
                                </select>
                                <button type="submit" class="btn btn-positive">상태 변경</button>
                            </form>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </c:if>

    <div class="cell mt-40 center">
        <a href="${pageContext.request.contextPath}/admin/orders/list" class="btn btn-secondary">← 목록으로</a>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />

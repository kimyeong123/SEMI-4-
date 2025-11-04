<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<div class="container">
	<h2>최근 주문 내역</h2>
	<p>상품의 출고지가 여러 곳이거나 온라인 물류센터 보관 장소에 따라 분리 배송될 수 있습니다.</p>
	<hr>

	<c:if test="${not empty message}">
		<div class="alert success">${message}</div>
		<%-- 성공 메시지 표시 --%>
	</c:if>
	<c:if test="${not empty error}">
		<div class="alert error">${error}</div>
		<%-- 오류 메시지 표시 --%>
	</c:if>

	<%-- 주문 목록 시작 --%>
	<c:choose>
		<%-- 주문 내역이 없을 경우 --%>
		<c:when test="${empty orderList}">
			<div class="row">
				<p>주문 내역이 없습니다.</p>
			</div>
		</c:when>
		<%-- 주문 내역이 있을 경우 --%>
		<c:otherwise>
			<c:forEach var="order" items="${orderList}">
				<div class="order-item"
					style="border: 1px solid #eee; margin-bottom: 20px; padding: 15px;">
					<%-- 주문 헤더 --%>
					<div class="order-header"
						style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 10px; font-size: 0.9em; color: #555;">
						<div>
							<%-- 주문 날짜 --%>
							<fmt:formatDate value="${order.ordersCreatedAt}"
								pattern="yyyy.MM.dd" />
							&nbsp;&nbsp; 총 주문금액
							<fmt:formatNumber value="${order.ordersTotalPrice}"
								type="currency" />
							원 &nbsp;&nbsp; 주문번호 ${order.ordersNo}
						</div>
						<div>
							<%-- 상세 내역 링크 --%>
							<a href="${pageContext.request.contextPath}/orders/detail?ordersNo=${order.ordersNo}"
								style="color: #555; text-decoration: none;">상세주문내역 &gt;</a>
						</div>
					</div>

					<%-- 주문 본문 --%>
					<div class="order-body"
						style="display: flex; align-items: center; justify-content: space-between;">
						<%-- 상품 정보 (이미지, 이름, 옵션, 수량, 가격) --%>
						<div style="display: flex; align-items: center;">
							<%-- 썸네일 이미지 (이미지 경로 설정 필요!) --%>
<%-- 							<c:if test="${not empty order.productThumbnailNo}"> --%>
								<img
									src="${pageContext.request.contextPath}/attachment/download?attachmentNo=${order.productThumbnailNo}"
									alt="${order.productName}"
									style="width: 80px; height: 80px; object-fit: cover; margin-right: 15px;">
<%-- 							</c:if> --%>
							<%-- 또는 /static/product-images/${order.thumbnailName} 같은 경로 --%>
							<%-- <img src="${pageContext.request.contextPath}/static/product-images/${order.thumbnailName}" alt="${order.productName}" style="width: 80px; height: 80px; object-fit: cover; margin-right: 15px;"> --%>

							<div>
								<div style="font-weight: bold;">${order.productName}</div>
								<div style="font-size: 0.9em; color: #777;">
									${order.optionName}
									<%-- 옵션 값 --%>
									<c:if test="${not empty order.optionName}"> / </c:if>
									<%-- 옵션 있을 때만 구분자 --%>
									${order.orderAmount}개
									<%-- 수량 --%>
								</div>
								<%-- 개당 가격 --%>
								<div style="font-size: 0.9em; color: #333;">
									<fmt:formatNumber value="${order.pricePerItem}" type="currency" />
									원

								</div>
							</div>
						</div>

						<%-- 주문 상태 및 버튼 --%>
						<div class="center">
							<div style="font-weight: bold;" class="mb-10">${order.ordersStatus}</div>

							<%-- JSP의 주문 취소 버튼 부분 --%>
							<c:if
								test="${order.ordersStatus == '결제완료' || order.ordersStatus == '배송준비중'}">
								<form action="${pageContext.request.contextPath}/orders/cancel" method="post"
									onsubmit="return confirm('정말 주문을 취소하시겠습니까?');">
									<%-- 확인 메시지 추가 --%>
									<input type="hidden" name="ordersNo" value="${order.ordersNo}">
									<button type="submit" class="btn btn-neutral">주문취소</button>
								</form>
							</c:if>
						</div>
					</div>
				</div>
				<%-- /.order-item --%>
			</c:forEach>
		</c:otherwise>
	</c:choose>

</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
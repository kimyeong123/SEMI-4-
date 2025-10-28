<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%-- [수정] header.jsp 경로는 webapp 루트 기준이어야 합니다. --%>
<jsp:include page="/WEB-INF/views/template/header.jsp" />

<head>
<style>
.product-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.cart-controls {
	display: flex;
	align-items: center;
	gap: 15px;
}

.quantity-changer {
	display: inline-flex;
	align-items: center;
	border: 1px solid #ddd;
	border-radius: 50px;
	padding: 5px;
	background-color: #fdfdfd;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.quantity-btn {
	background-color: transparent;
	border: none;
	cursor: pointer;
	font-size: 22px;
	font-weight: bold;
	color: #333;
	padding: 5px 12px;
	line-height: 1;
}

.quantity-btn:hover {
	color: #000;
}

.btn-delete {
	font-size: 18px;
}

.quantity-display {
	font-size: 18px;
	font-weight: bold;
	color: #E58A00;
	padding: 0 8px;
	min-width: 20px;
	text-align: center;
}
/* [수정] 찜 버튼 CSS 제거 */

/* [추가] 주문 요약 CSS */
.order-summary-box {
	background-color: #f4f6fA;
	border-radius: 12px;
	padding: 24px;
	position: sticky;
	top: 20px;
}

.summary-row {
	display: flex;
	justify-content: space-between;
	margin-bottom: 12px;
	font-size: 16px;
}

.summary-total {
	display: flex;
	justify-content: space-between;
	font-size: 18px;
	font-weight: bold;
	padding-top: 15px;
	border-top: 1px solid #ddd;
	margin-top: 15px;
}

.btn-checkout {
	padding: 1em 0;
	font-size: 16px;
	font-weight: bold;
}
</style>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<script type="text/javascript">
	$(function() {

		// (updateTotals, updateQuantityUI 함수는 이전과 동일)
		function updateTotals() {
			var subtotal = 0;
			$('.cart-item-row').each(
					function() {
						var price = parseInt($(this).data('price'));
						var quantity = parseInt($(this).find(
								'.quantity-changer').data('quantity'));

						if (price >= 0 && quantity >= 0) {
							subtotal += price * quantity;
						}
					});
			var formattedSubtotal = subtotal.toLocaleString('ko-KR') + " 원";

			// [수정] 상품금액, 총금액 둘 다 업데이트
			$('.subtotal-amount').text(formattedSubtotal);
			$('.total-amount').text(formattedSubtotal); // (배송비가 0원이므로)
		}

		function updateQuantityUI(changer) {
			var quantity = parseInt(changer.data('quantity'));
			changer.find('.quantity-display').text(quantity);
			if (quantity === 1) {
				changer.find('.btn-delete').show();
				changer.find('.btn-minus').hide();
			} else {
				changer.find('.btn-delete').hide();
				changer.find('.btn-minus').show();
			}
		}

		// (이벤트 핸들러, AJAX 로직은 이전과 동일)
		// [주의] url 경로는 프로젝트 경로(Context Path)에 맞게 수정 필요
		// (예: /shoppingmall/rest/cart/update)
		$('.btn-plus, .btn-minus')
				.on(
						'click',
						function() {
							var quantityChanger = $(this).closest(
									'.quantity-changer');
							var itemRow = $(this).closest('.cart-item-row');
							var cartNo = itemRow.data('cart-no');
							var currentQuantity = parseInt(quantityChanger
									.data('quantity'));
							var newQuantity = $(this).hasClass('btn-plus') ? currentQuantity + 1
									: currentQuantity - 1;

							if (newQuantity < 1)
								return; // 1 미만 방지

							$
									.ajax({
										url : "/rest/cart/update", // [주의] 프로젝트 경로(Context Path) 확인
										method : "POST",
										data : {
											cartNo : cartNo,
											cartAmount : newQuantity
										},
										success : function(response) {
											if (response === true) {
												quantityChanger
														.data('quantity',
																newQuantity);
												updateQuantityUI(quantityChanger);
												updateTotals();
											} else {
												alert("수량 변경 실패");
											}
										},
										error : function(xhr) {
											alert(xhr.responseJSON ? xhr.responseJSON.message
													: "오류 발생");
										}
									});
						});

		$('.btn-delete').on(
				'click',
				function() {
					if (!confirm("상품을 삭제하시겠습니까?"))
						return;

					var itemRow = $(this).closest('.cart-item-row');
					var cartNo = itemRow.data('cart-no');

					$.ajax({
						url : "/rest/cart/delete", // [주의] 프로젝트 경로(Context Path) 확인
						method : "POST",
						data : {
							cartNo : cartNo
						},
						success : function(response) {
							if (response === true) {
								itemRow.remove();
								updateTotals();
							} else {
								alert("삭제 실패");
							}
						},
						error : function(xhr) {
							alert(xhr.responseJSON ? xhr.responseJSON.message
									: "오류 발생");
						}
					});
				});

		// (페이지 로드 시 초기화는 이전과 동일)
		$('.quantity-changer').each(function() {
			updateQuantityUI($(this));
		});
		updateTotals();
	});
</script>
</head>

<body>
	<div class="container w-900">

		<div class="cell center">
			<h1>장바구니</h1>
		</div>

		<div class="flex-box">

			<div class="w-600">

				<%-- [추가] 장바구니가 비었을 때 --%>
				<c:if test="${empty cartlist}">
					<div class="cell center" style="padding: 50px 0;">장바구니에 담긴
						상품이 없습니다.</div>
				</c:if>

				<%-- [추가] JSTL 반복문 --%>
				<c:forEach var="item" items="${cartlist}" varStatus="status">
					<div class="cell cart-item-row" data-cart-no="${item.cartNo}"
						data-price="${item.productPrice}" data-order-date="${order.ordersCreatedAt.time}">

						<div class="flex-box">
							<div class="w-150" style="height: 150px;">
								<%-- 
                                  [수정] 이미지 경로 
                                  (환경에 맞게 /images/ 또는 /download/ 같은 경로 추가)
                                --%>
								<img src="/images/${item.thumbnailName}" class="product-image">
							</div>

							<div class="flex-fill ms-20">
								<div>
									<%-- [수정] 상품명 --%>
									<h3>${item.productName}</h3>
								</div>
								<%-- [수정] 옵션 --%>
								<div>${item.optionName}: ${item.optionValue}</div>
							</div>

							<div class="w-150 right">
								<%-- [수정] 가격 (포맷 적용) --%>
								<h3>
									<fmt:formatNumber value="${item.productPrice}" pattern="#,##0" />
									원
								</h3>
							</div>
						</div>

						<div class="flex-box">
							<div class="cart-controls">
								<%-- [수정] 수량 (data-quantity, span) --%>
								<div class="quantity-changer" data-quantity="${item.cartAmount}">
									<button class="quantity-btn btn-delete">
										<i class="fa-solid fa-trash"></i>
									</button>
									<button class="quantity-btn btn-minus" style="display: none;">-</button>
									<span class="quantity-display">${item.cartAmount}</span>
									<button class="quantity-btn btn-plus">+</button>
								</div>
								<%-- [삭제] 찜 버튼 HTML 제거 --%>
							</div>
						</div>

						<div class="cell">
							<div class="cell">무료배송</div>
							<div class="cell">
    							도착 예정일 : ${estimatedDeliveryDate}
							</div>
						</div>
					</div>

					<%-- [추가] 마지막 아이템이 아니면 구분선 <hr> 추가 --%>
					<c:if test="${not status.last}">
						<hr>
					</c:if>

				</c:forEach>
				<%-- 반복문 종료 --%>

			</div>
			<div class="w-250 ms-50">
				<div class="order-summary-box">
					<h3 class="cell center mt-0">주문 내역</h3>
					<div class="cell">
						<%-- [수정] JS가 금액을 채울 수 있도록 span 태그 추가 --%>
						<div class="summary-row">
							<span>상품 금액</span> <span class="subtotal-amount">0 원</span>
						</div>
						<div class="summary-row">
							<span>배송비</span> <span>무료</span>
						</div>
					</div>

					<%-- [추가] 총 결제 금액 --%>
					<div class="cell">
						<div class="summary-total">
							<span>총 결제 금액</span> <span class="total-amount">0 원</span>
						</div>
					</div>

					<div class="cell mt-30">
						<%-- [수정] /orders/payment로 이동하는 <a> 태그로 변경 --%>
						<%-- [주의] 프로젝트 경로(Context Path) 확인 --%>
						<a href="/orders/payment"
							class="btn btn-positive w-100 btn-checkout"> 주문결제 </a>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>

</html>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
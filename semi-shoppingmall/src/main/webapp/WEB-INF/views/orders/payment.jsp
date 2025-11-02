<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />

<head>
<link rel="stylesheet" type="text/css" href="commons.css">
<link rel="stylesheet" type="text/css"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

<style>
/* [추가] cart.jsp와 동일한 주문 요약 CSS */
.order-summary-box {
	background-color: #f4f6fA;
	border-radius: 12px;
	padding: 24px;
	position: sticky; /* 스크롤 따라오도록 */
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
	src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>

<script
	src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

<script src="./confirm.js"></script>

<script type="text/javascript">
	window
			.addEventListener(
					"load",
					function() {
						var state = {
							recipientValid : false,
							memberContactValid : false,
							memberAddressValid : false,

							ok : function() {
								return this.recipientValid
										&& this.memberContactValid
										&& this.memberAddressValid;
							}

						};

						//이름
						$("[name=ordersRecipient]").on(
								"blur",
								function() {
									var regex = /^[가-힣]{2,7}$/;
									var valid = regex.test($(this).val());

									if (valid == false) {
										$(this).removeClass("success fail")
												.addClass("fail");
										state.recipientValid = false;
										return;
									} else {
										$(this).removeClass("success fail")
												.addClass("success");
										state.recipientValid = true;
									}
								});

						//전화번호
						$("[name=ordersRecipientContact]")
								.on(
										"blur",
										function() {
											var regex = /^010[1-9][0-9]{3}[0-9]{4}$/;
											// [수정] 빈 값 허용 안 함 (결제페이지는 필수)
											var valid = regex.test($(this)
													.val());
											$(this).removeClass("success fail")
													.addClass(
															valid ? "success"
																	: "fail");
											state.memberContactValid = valid;
										});

						$("[name=ordersRecipientContact]").on(
								"input",
								function() {
									var replacement = $(this).val().replace(
											/[^0-9]/g, "");
									replacement = replacement.substring(0, 11);
									$(this).val(replacement);
								});

						//주소
						// 주소
						$("[name=ordersShippingAddress2]")
								.on(
										"blur",
										function() {
											var memberPostInput = $("[name=ordersShippingPost]");
											var memberAddress1Input = $("[name=ordersShippingAddress1]");
											var memberAddress2Input = $(this);

											//빈 값 허용 안 함
											var valid = memberPostInput.val().length > 0
													&& memberAddress1Input
															.val().length > 0
													&& memberAddress2Input
															.val().length > 0;

											memberPostInput.removeClass(
													"success fail").addClass(
													valid ? "success" : "fail");

											memberAddress1Input.removeClass(
													"success fail").addClass(
													valid ? "success" : "fail");

											memberAddress2Input.removeClass(
													"success fail").addClass(
													valid ? "success" : "fail");

											state.memberAddressValid = valid;
										});

						//주소 검색 로직
						$(
								".btn-address-search, [name=ordersShippingPost], [name=ordersShippingAddress1]")
								.on("click", findAddress);
						function findAddress() {
							new daum.Postcode(
									{
										oncomplete : function(data) {
											var addr = '';
											if (data.userSelectedType === 'R') {
												addr = data.roadAddress;
											} else {
												addr = data.jibunAddress;
											}
											document
													.querySelector("[name=ordersShippingPost]").value = data.zonecode;
											document
													.querySelector("[name=ordersShippingAddress1]").value = addr;
											document
													.querySelector(
															"[name=ordersShippingAddress2]")
													.focus();
											document
													.querySelector(".btn-address-clear").style.display = "";
										}
									}).open();
						}
						document
								.querySelector(".btn-address-clear")
								.addEventListener(
										"click",
										function() {
											document
													.querySelector("[name=ordersShippingPost]").value = "";
											document
													.querySelector("[name=ordersShippingAddress1]").value = "";
											document
													.querySelector("[name=ordersShippingAddress2]").value = "";
											this.style.display = "none";
										});

						//지우기 버튼 관련 로직
						$(".btn-clear-address").on(
								"click",
								function() {
									// 관련 입력 필드 값 비우기
									$("[name=ordersRecipient]").val("")
											.removeClass("success fail");
									$("[name=ordersShippingPost]").val("")
											.removeClass("success fail");
									$("[name=ordersShippingAddress1]").val("")
											.removeClass("success fail");
									$("[name=ordersShippingAddress2]").val("")
											.removeClass("success fail");
									$("[name=ordersRecipientContact]").val("")
											.removeClass("success fail");

									state.recipientValid = false;
									state.memberAddressValid = false;
									state.memberContactValid = false;

									//주소 클리어버튼 숨기기
									$(".btn-address-clear").hide();

									//이름 입력창에 focus
									$("[name=ordersRecipient]").focus();
								});

						//폼 전송 시 유효성 검사
						$(".payment-form").on("submit", function(e) {
							$(this).find("[name]").trigger("blur");

							if (state.ok() == false) { //상태가 모두 true가 아니라면
								e.preventDefault(); //전송 취소
								alert("이름과 주소, 전화번호를 모두 입력해야 해요");
							} else {
								if (!confirm("결제 하시겠습니까?")) {
									e.preventDefault(); // 사용자가 '취소' 누르면 전송 중단
									return false;
								}
							}
						});
					});
</script>
</head>

<body>
	<div class="container w-900">
		<div class="cell center">
			<h1>결제하기</h1>
		</div>

		<c:set var="subtotal" value="${totalPrice}" />
		<c:set var="shippingFee" value="${subtotal >= 50000 ? 0 : 3000}" />
		<c:set var="finalTotal" value="${subtotal + shippingFee}" />

		<form class="flex-box payment-form" action="/orders/payment"
			method="post">

			<div class="cell w-600">
				<div class="cell right">
					<input type="checkbox" name="saveAddressAsDefault"
						id="saveAddressCheck" value="true"> <label
						for="saveAddressCheck">이 주소를 기본 배송지로 저장</label>
					<button type="button" class="btn btn-negative btn-clear-address">
						<i class="fa-solid fa-eraser"></i>
					</button>
				</div>
				<div class="cell w-100">
					<span>이름</span> <input type="text" class="field w-100"
						name="ordersRecipient" value="${memberDto.memberName}">
					<div class="fail-feedback">이름은 반드시 입력해야 해요</div>
				</div>
				<div class="cell">주소</div>
				<div class="cell">
					<input type="text" name="ordersShippingPost" placeholder="우편번호"
						size="6" class="field" inputmode="numeric" readonly
						value="${memberDto.memberPost}">
					<button type="button" class="btn btn-neutral btn-address-search">
						<i class="fa-solid fa-magnifying-glass"></i>
					</button>
					<button type="button" class="btn btn-negative btn-address-clear"
						style="display: none;">
						<i class="fa-solid fa-xmark"></i>
					</button>
				</div>
				<div class="cell">
					<input type="text" name="ordersShippingAddress1"
						placeholder="기본 주소" class="field w-100" readonly
						value="${memberDto.memberAddress1}"> <input type="text"
						name="ordersShippingAddress2" placeholder="상세 주소"
						class="field w-100" value="${memberDto.memberAddress2}">
					<div class="success-feedback">주소 입력이 완료되었어요</div>
					<div class="fail-feedback">형식이 맞지 않거나 주소 입력이 완료되지 않았어요</div>
				</div>

				<div class="cell">
					<label>전화번호</label> <input type="text"
						name="ordersRecipientContact" placeholder="- 없이 작성"
						inputmode="tel" class="field w-100"
						value="${memberDto.memberContact}">
					<div class="fail-feedback">전화번호는 반드시 입력해야 해요</div>
				</div>
			</div>

			<div class="cell w-250 ms-50">
				<div class="order-summary-box">
					<h3 class="cell center mt-0">주문 요약</h3>

					<div class="cell">
						<div class="summary-row">
							<span>상품 금액</span> <span><fmt:formatNumber
									value="${subtotal}" pattern="#,##0" /> 원</span>
						</div>
						<div class="summary-row">
							<span>배송비</span> <span><fmt:formatNumber
									value="${shippingFee}" pattern="#,##0" /> 원</span>
						</div>
					</div>

					<div class="cell">
						<div class="summary-total">
							<span>총 결제 금액</span> <span><fmt:formatNumber
									value="${finalTotal}" pattern="#,##0" /> 원</span>
						</div>
					</div>

					<input type="hidden" name="ordersTotalPrice" value="${finalTotal}">

					<div class="cell mt-30">
						<button type="submit" class="btn btn-positive w-100 btn-checkout">결제하기</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</body>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
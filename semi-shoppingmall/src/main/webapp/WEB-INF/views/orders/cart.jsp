<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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

        /* 💡 [수정] 휴지통 아이콘 색상 설정 */
        .btn-delete-item {
            font-size: 18px;
            color: #777; 
        }

        /* 💡 [수정] 수량 글씨 색상 검은색으로 변경 */
        .quantity-display {
            font-size: 18px;
            font-weight: bold;
            color: #333; /* 검은색 계열로 변경 */
            padding: 0 8px;
            min-width: 20px;
            text-align: center;
        }

        /* 주문 요약 CSS */
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

    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <script type="text/javascript">
        $(function () {

            function updateTotals() {
                var subtotal = 0;
                $('.cart-item-row').each(
                    function () {
                        var price = parseInt($(this).data('price'));
                        var quantity = parseInt($(this).find(
                            '.quantity-changer').data('quantity'));

                        if (price >= 0 && quantity >= 0) {
                            subtotal += price * quantity;
                        }
                    });
                var formattedSubtotal = subtotal.toLocaleString('ko-KR') + " 원";

                $('.subtotal-amount').text(formattedSubtotal);
                $('.total-amount').text(formattedSubtotal);
            }

            // 수량이 1일 때만 마이너스 버튼을 숨깁니다.
            function updateQuantityUI(changer) {
                var quantity = parseInt(changer.data('quantity'));
                changer.find('.quantity-display').text(quantity);
                
                if (quantity === 1) {
                    changer.find('.btn-minus').hide();
                } else {
                    changer.find('.btn-minus').show();
                }
            }

            // 수량 변경 이벤트 핸들러
            $('.btn-plus, .btn-minus')
                .on(
                    'click',
                    function () {
                        var quantityChanger = $(this).closest(
                            '.quantity-changer');
                        var itemRow = $(this).closest('.cart-item-row');
                        var cartNo = itemRow.data('cart-no');
                        var currentQuantity = parseInt(quantityChanger
                            .data('quantity'));
                        var newQuantity = $(this).hasClass('btn-plus') ? currentQuantity + 1
                            : currentQuantity - 1;

                        if (newQuantity < 1)
                            return;

                        $.ajax({
                            url: "/rest/cart/update", // [주의] 프로젝트 경로 확인
                            method: "POST",
                            data: {
                                cartNo: cartNo,
                                cartAmount: newQuantity
                            },
                            success: function (response) {
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
                            error: function (xhr) {
                                alert(xhr.responseJSON ? xhr.responseJSON.message
                                    : "오류 발생");
                            }
                        });
                    });

            // 항목 삭제 이벤트 핸들러 (.btn-delete-item 클래스 사용)
            $('.btn-delete-item').on(
                'click',
                function () {
                    if (!confirm("상품을 삭제하시겠습니까?"))
                        return;

                    var itemRow = $(this).closest('.cart-item-row');
                    var productNo = itemRow.data('product-no');
                    var optionNo = itemRow.data('option-no');

                    $.ajax({
                        url: "/rest/cart/delete", // [주의] 프로젝트 경로 확인
                        method: "POST",
                        data: {
                            productNo: productNo,
                            optionNo: optionNo
                        },
                        success: function (response) {
                            if (response === true) {
                                itemRow.remove();
                                updateTotals();
                            } else {
                                alert("삭제 실패");
                            }
                        },
                        error: function (xhr) {
                            alert("삭제 오류 발생: " + (xhr.responseJSON ? xhr.responseJSON.error : "서버 오류"));
                        }
                    });
                });

            // 페이지 로드 시 초기화
            $('.quantity-changer').each(function () {
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
                <c:if test="${empty cartlist}">
                    <div class="cell center" style="padding: 50px 0;">장바구니에 담긴
                        상품이 없습니다.</div>
                </c:if>

                <c:forEach var="item" items="${cartlist}" varStatus="status">
                    <div class="cell cart-item-row" 
                         data-cart-no="${item.cartNo}"
                         data-price="${item.productPrice}" 
                         data-product-no="${item.productNo}"
                         data-option-no="${item.optionNo}"
                         data-order-date="${order.ordersCreatedAt.time}">

                        <div class="flex-box">
                            <div class="w-150" style="height: 150px;">
                                <img src="${pageContext.request.contextPath}/images/${item.thumbnailName}"
                                    class="product-image">
                            </div>

                            <div class="flex-fill ms-20">
                                <div>
                                    <h3>${item.productName}</h3>
                                </div>
                                <div>${item.optionName}:${item.optionValue}</div>
                            </div>

                            <div class="w-150 right">
                                <h3>
                                    <fmt:formatNumber value="${item.productPrice}"
                                        pattern="#,##0" />
                                    원
                                </h3>
                            </div>
                        </div>

                        <div class="flex-box justify-content-between align-items-center">
                            <div class="cart-controls">
                                <div class="quantity-changer"
                                    data-quantity="${item.cartAmount}">
                                    <button class="quantity-btn btn-minus" style="display: none;">-</button>
                                    <span class="quantity-display">${item.cartAmount}</span>
                                    <button class="quantity-btn btn-plus">+</button>
                                </div>
                            </div>
                            
                            <button class="btn-delete-item btn-transparent" style="border: none; background: none; cursor: pointer;">
                                <i class="fa-solid fa-trash-can"></i>
                            </button>
                        </div>

                        <div class="cell">
                            <div class="cell">무료배송</div>
                            <div class="cell">도착 예정일 : ${estimatedDeliveryDate}</div>
                        </div>
                    </div>

                    <c:if test="${not status.last}">
                        <hr>
                    </c:if>
                </c:forEach>
            </div>
            
            <div class="w-250 ms-50">
                <div class="order-summary-box">
                    <h3 class="cell center mt-0">주문 내역</h3>
                    <div class="cell">
                        <div class="summary-row">
                            <span>상품 금액</span> <span class="subtotal-amount">0 원</span>
                        </div>
                        <div class="summary-row">
                            <span>배송비</span> <span>무료</span>
                        </div>
                    </div>

                    <div class="cell">
                        <div class="summary-total">
                            <span>총 결제 금액</span> <span class="total-amount">0 원</span>
                        </div>
                    </div>

                    <div class="cell mt-30">
                        <a href="/orders/payment"
                            class="btn btn-positive w-100 btn-checkout"> 주문결제 </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
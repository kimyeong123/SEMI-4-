<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<jsp:include page="/WEB-INF/views/template/header.jsp" />

<head>
    <title>장바구니</title>
    <style>
        .product-image {
            width: 100%;
            height: 100%;
            object-fit: cover;
            /* 이미지가 150x150 셀에 꽉 차도록 설정 (찌그러짐 방지) */
        }

        /* 컨트롤 버튼 전체 래퍼 */
        .cart-controls {
            display: flex;
            align-items: center;
            gap: 15px;
            /* 수량 버튼과 하트 버튼 사이 간격 */
        }

        /* 수량 변경 그룹 (둥근 테두리) */
        .quantity-changer {
            display: inline-flex;
            align-items: center;
            border: 1px solid #ddd;
            border-radius: 50px;
            /* 둥근 모서리 */
            padding: 5px;
            background-color: #fdfdfd;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
        }

        /* +/-/삭제 버튼 공통 스타일 */
        .quantity-btn {
            background-color: transparent;
            border: none;
            cursor: pointer;
            font-size: 22px;
            /* 아이콘/글자 크기 */
            font-weight: bold;
            color: #333;
            padding: 5px 12px;
            line-height: 1;
            /* 줄 높이 조절 */
        }

        .quantity-btn:hover {
            color: #000;
        }

        .btn-delete {
            font-size: 18px;
            /* 이모지 크기 조절 */
        }

        /* 수량 표시 텍스트 */
        .quantity-display {
            font-size: 18px;
            font-weight: bold;
            color: #E58A00;
            /* 이미지의 주황색 숫자 */
            padding: 0 8px;
            min-width: 20px;
            /* 숫자가 바뀔 때 크기 고정 */
            text-align: center;
        }

        /* --- 하트(찜) 버튼 --- */
        .btn-wish {
            background-color: #f0f0f0;
            border: none;
            border-radius: 50%;
            /* 원형 */
            width: 44px;
            height: 44px;
            cursor: pointer;
            font-size: 20px;
            /* 아이콘 크기 */

            /* flex-box 클래스 없이 아이콘을 중앙 정렬하기 */
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .btn-wish:hover {
            background-color: #e0e0e0;
        }
    </style>

    <!-- jquery cdn -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <script type="text/javascript">
    $(function () {
        
        /**
         * 총 금액 계산 함수
         */
        function updateTotals() {
            var subtotal = 0; // 'var' 사용
            
            $('.cart-item-row').each(function() {
                var price = parseInt($(this).data('price'));
                var quantity = parseInt($(this).find('.quantity-changer').data('quantity'));
                
                // [변경] isNaN 대신 간단한 숫자 비교
                if (price >= 0 && quantity >= 0) { 
                    subtotal += price * quantity;
                }
            });
            
            var formattedSubtotal = subtotal.toLocaleString('ko-KR') + " 원";
            $('.subtotal-amount').text(formattedSubtotal);
            $('.total-amount').text(formattedSubtotal);
        }
        
        /**
         * 수량 버튼 UI 변경 함수
         */
        function updateQuantityUI(changer) { // [변경] 변수명 '$' 제거
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

        // --- 이벤트 핸들러 ---
        
        // 수량 변경 (+, -) 공통 처리
        $('.btn-plus, .btn-minus').on('click', function () {
            var quantityChanger = $(this).closest('.quantity-changer'); // [변경] 변수명
            var itemRow = $(this).closest('.cart-item-row'); // [변경] 변수명
            
            var cartNo = itemRow.data('cart-no');
            var currentQuantity = parseInt(quantityChanger.data('quantity'));
            var newQuantity;
            
            if ($(this).hasClass('btn-plus')) {
                newQuantity = currentQuantity + 1;
            } else {
                if (currentQuantity <= 1) return;
                newQuantity = currentQuantity - 1;
            }
            
            $.ajax({
                url: "/rest/cart/update", 
                method: "POST",
                data: {
                    cartNo: cartNo,
                    cartAmount: newQuantity
                },
                success: function(response) { 
                    if (response === true) {
                        quantityChanger.data('quantity', newQuantity); 
                        updateQuantityUI(quantityChanger); // [변경] 변수명
                        updateTotals(); 
                    } else {
                        alert("수량 변경에 실패했습니다.");
                    }
                },
                error: function(xhr, status, error) {
                    var errorMsg = xhr.responseJSON ? xhr.responseJSON.message : "수량 변경 중 오류 발생";
                    alert(errorMsg);
                }
            });
        });

        // 삭제 버튼 클릭
        $('.btn-delete').on('click', function () {
            if (!confirm("이 상품을 장바구니에서 삭제하시겠습니까?")) {
                return;
            }

            var itemRow = $(this).closest('.cart-item-row'); // [변경] 변수명
            var cartNo = itemRow.data('cart-no');
            
            $.ajax({
                url: "/rest/cart/delete", 
                method: "POST", 
                data: {
                    cartNo: cartNo
                },
                success: function(response) { 
                    if (response === true) {
                        itemRow.remove(); // [변경] 변수명
                        updateTotals();
                    } else {
                        alert("삭제에 실패했습니다.");
                    }
                },
                error: function(xhr, status, error) {
                    var errorMsg = xhr.responseJSON ? xhr.responseJSON.message : "삭제 중 오류 발생";
                    alert(errorMsg);
                }
            });
        });

        // --- 페이지 로드 시 초기화 ---
        $('.quantity-changer').each(function () {
            // 'this'는 .quantity-changer DOM 요소를 가리킴
            // $(this)로 감싸서 jQuery 객체로 만들어 함수에 전달
            updateQuantityUI($(this)); 
        });
        updateTotals(); 
    });
</script>
</head>

<body>
    <div class="container w-800">

        <div class="cell">
            <h1>장바구니</h1>
        </div>

        <div class="flex-box">

            <div class="w-550">

                <div class="cell cart-item-row">
                    <div class="flex-box">

                        <div class="w-150" style="height: 150px;"> <img src="https://picsum.photos/id/1/200/200"
                                class="product-image">
                        </div>

                        <div class="flex-fill ms-20">
                            <div>
                                <h3>나이키 삭스 Z</h3>
                            </div>
                            <div>여성 신발</div>
                            <div>사이즈 260</div>
                        </div>

                        <div class="w-150 right">
                            <h3>159,000 원</h3>
                        </div>
                    </div>

                    <div class="flex-box">
                        <div class="cart-controls">
                            <div class="quantity-changer" data-quantity="1">
                                <button class="quantity-btn btn-delete">
                                    <i class="fa-solid fa-trash"></i>
                                </button>
                                <button class="quantity-btn btn-minus" style="display: none;">-</button>
                                <span class="quantity-display">1</span>
                                <button class="quantity-btn btn-plus">+</button>
                            </div>

                            <div class="cell">
                                <button type="button" class="btn-wish" data-wished="true">
                                    <i class="fa-solid fa-heart red"></i>
                                    <i class="fa-regular fa-heart red"></i>
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="cell">
                        <div class="cell">
                            무료배송
                        </div>
                        <div class="cell">
                            도착 예정일 : 10월 27일(월)
                        </div>
                    </div>
                </div>
                <hr>
            </div>
            <div class="w-200 ms-30">
                <div class="order-summary-box">
                    <h3 class="cell center mt-0">주문 내역</h3>
                    <div class="cell mt-10">상품 가격 : </div>
                    <div class="cell mt-10">배송비 : </div>

                    <div class="cell mt-30">
                        <button class="btn btn-positive w-100 btn-checkout">주문결제</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>

</html>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
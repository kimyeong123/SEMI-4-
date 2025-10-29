<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>위시리스트</title>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"> 

<style>
/* === 공통 레이아웃 === */
.container {
	width: 90%;
	max-width: 1100px;
	margin: 40px auto;
}
h2 { 
    font-size: 2em; 
    padding-bottom: 10px;
    margin-bottom: 30px;
    color: #333;
    border-bottom: 1px solid #ddd;
    text-align: center !important;
}
/* === 위시리스트 컨테이너 및 카드 === */
.wishlist-container {
	display: flex; 
	flex-wrap: wrap; 
	gap: 20px;
	justify-content: flex-start;
	margin-bottom: 50px;
}

.wishlist-card {
	display: flex;
	flex-direction: column;
	border: 1px solid #ddd;
	padding: 15px;
	width: 250px;
	box-shadow: none;
	border-radius: 0;
	transition: box-shadow 0.2s;
}

.wishlist-card:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.wishlist-card img {
	width: 100%;
	height: 250px;
	object-fit: cover;
	margin-bottom: 15px; 
}

.wishlist-card h3 {
	font-size: 1.1em;
	color: #333;
	margin-bottom: 5px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis; 
}

.wishlist-card .price {
	font-size: 1.2em;
	font-weight: bold;
	color: #000;
	margin-bottom: 15px;
}

.wishlist-card .button-group {
    display: flex;
    gap: 5px;
    margin-top: 10px;
}
.empty-message {
    width: 100%;
    text-align: center;
    padding: 50px;
    font-size: 1.1em;
    color: #666;
}

/* === 버튼 스타일 (공통) === */
.btn {
	padding: 10px 15px;
	border-radius: 5px;
	cursor: pointer;
	font-weight: normal;	
	transition: background-color 0.2s, color 0.2s, border-color 0.2s, filter 0.2s;
	text-decoration: none;
	display: inline-block;
	text-align: center;
	border: 1px solid;
	font-size: 0.95em;
}

/* 호버 효과 (모든 버튼에 적용) */
.btn:hover {
    filter: brightness(0.9);
}


/* Black/Primary Action (장바구니) - btn-black 스타일 추가 */
.btn-black {	
	border-color: #333;	
	color: white;	
	background-color: #333;	
}	
.btn-black:hover {	
	/* filter: brightness(0.9)가 적용되므로 별도 background-color 변경은 생략 */
}

/* Negative Action (삭제) */
.btn-negative {
	border-color: #a00;	
	color: #a00;	
	background-color: transparent;
    padding: 10px 10px;
}
.btn-negative:hover {
    background-color: #fdd;
	border-color: #a00;
	color: #a00;
}
</style>

<script type="text/javascript">
$(function() {
    // 상품 상세 보기로 이동
    $(".product-link").on("click", function(e) {
        e.preventDefault();
        var productNo = $(this).data("product-no");
        location.href = "${pageContext.request.contextPath}/product/detail?productNo=" + productNo;
    });

    // 위시리스트 삭제 (AJAX)
    $(".btn-delete").on("click", function() {
        var productNo = $(this).data("product-no");
        if(!confirm("정말 위시리스트에서 삭제하시겠습니까?")) return;

        $.ajax({
            url: "${pageContext.request.contextPath}/rest/wishlist/toggle",
            method: "post",
            data: { productNo: productNo },
            success: function(response) {
                if(!response.wishlisted || response === false) { 
                    alert("삭제되었습니다.");
                    // 삭제된 카드만 제거
                    $("#card-" + productNo).remove(); 
                    if ($(".wishlist-card").length === 0) {
                        // 모든 카드가 삭제되면 목록 없음 메시지를 보여줌
                        $(".wishlist-container").html('<p class="empty-message">위시리스트에 등록된 상품이 없습니다. 💔</p>');
                    }
                } else {
                    alert("처리 실패: 상품이 위시리스트에 남아있습니다.");
                }
            },
            error: function(xhr) {
                if (xhr.status === 401) {
                    alert("로그인이 필요합니다.");
                } else {
                    alert("위시리스트 삭제 중 오류가 발생했습니다.");
                }
            }
        });
    });
    
    // 장바구니 담기 기능 (AJAX) - optionNo 추가
    $(".btn-cart-move").on("click", function() {
        var productNo = $(this).data("product-no");
        var optionNo = $(this).data("option-no");

        if (!productNo) {
            alert("상품 정보를 찾을 수 없습니다.");
            return;
        }
        
        var quantity = 1;

        $.ajax({
            url: "${pageContext.request.contextPath}/rest/cart/add",
            method: "post",
            data: {
                productNo: productNo,
                optionNo: optionNo, 
                cartAmount: quantity
            },
            success: function(response) {
                alert("선택하신 상품이 장바구니에 추가되었습니다.");
            },
            error: function(xhr) {
                if (xhr.status === 401) {
                    alert("로그인이 필요합니다.");
                } else {
                    alert("장바구니 추가 중 오류 발생");
                }
            }
        });
    });
});
</script>
</head>
<body>
<div class="container">
	<h2>내 위시리스트</h2>
	<div class="wishlist-container">
		<c:if test="${empty wishlist}">
			<p class="empty-message">위시리스트에 등록된 상품이 없습니다.</p>
		</c:if>
		<c:forEach var="item" items="${wishlist}">
			<div class="wishlist-card" id="card-${item.productNo}">
				<div class="image-box">
					<img
						src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${item.attachmentNo}"
						alt="${item.productName}">
				</div>
				
				<div class="text-container">
					<h3>
                        <a href="#" class="product-link" data-product-no="${item.productNo}" style="text-decoration:none; color:inherit;">
                            ${item.productName}
                        </a>
                    </h3>
					
					<p class="price">
                        <fmt:formatNumber value="${item.productPrice}" pattern="#,##0"/>원
                    </p>

					<div class="button-group">
                        <button type="button" class="btn btn-black btn-cart-move" 
        					data-product-no="${item.productNo}" 
        					data-option-no="${item.optionNo}" 
       						style="flex-grow: 3;">
    					<i class="fa-solid fa-cart-shopping"></i> 장바구니에 추가
					</button>
                        
						<button type="button" class="btn btn-delete btn-negative" data-product-no="${item.productNo}" style="flex-grow: 1;">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
					</div>
                    
                    <a href="${pageContext.request.contextPath}/product/detail?productNo=${item.productNo}" style="display:none;">상품 보기</a>
				</div>
			</div>
		</c:forEach>
	</div>
</div>
</body>
</html>
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

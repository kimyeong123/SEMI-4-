<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css"> 

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>내 위시리스트</title>

<style>
h2 { font-size: 2em; padding-bottom: 10px; margin-bottom: 30px; color:#333; border-bottom:1px solid #ddd; text-align:center; }
.wishlist-container { display:flex; flex-wrap:wrap; gap:20px; margin-bottom:50px; }
.wishlist-card { display:flex; flex-direction:column; border:1px solid #ddd; padding:15px; width:250px; box-shadow:none; border-radius:0; transition: box-shadow 0.2s, transform 0.1s; cursor:pointer; }
.wishlist-card:hover { box-shadow:0 4px 8px rgba(0,0,0,0.05); transform:translateY(-2px); }
.wishlist-card img { width:100%; height:250px; object-fit:cover; margin-bottom:15px; }
.wishlist-card h3 { font-size:1.1em; color:#333; margin-bottom:5px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
.wishlist-card .price { font-size:1.2em; font-weight:bold; color:#000; margin-bottom:15px; }
.wishlist-card .button-group { display:flex; justify-content:flex-end; gap:5px; }

/* 버튼 스타일: 기본 흰색, 호버 시 색상 반전 */
.btn-delete {
    background: #fff;
    color: #d32f2f;
    border: 1px solid #d32f2f;
    font-size:0.9em;
    padding:8px 10px;
    border-radius:5px;
    display:inline-flex;
    align-items:center;
    justify-content:center;
    gap:5px;
    transition: 0.2s;
}
.btn-delete:hover {
    background: #d32f2f;
    color: #fff;
    transform: scale(1.05);
}

.btn-cart-move {
    background: #fff;
    color: #333;
    border: 1px solid #333;
    font-size:0.9em;
    padding:8px 10px;
    border-radius:5px;
    display:inline-flex;
    align-items:center;
    justify-content:center;
    gap:5px;
    transition: 0.2s;
}
.btn-cart-move:hover {
    background: #333;
    color: #fff;
    transform: scale(1.05);
}

.empty-message { width:100%; text-align:center; padding:50px; font-size:1.1em; color:#666; }

/* 옵션 모달 */
#optionModal { display:none; position:fixed; top:50%; left:50%; transform:translate(-50%,-50%); background:#fff; padding:20px; border-radius:8px; box-shadow:0 4px 12px rgba(0,0,0,0.25); z-index:2000; width:300px; }
#optionModal select { width:100%; padding:8px; margin-bottom:15px; }
#optionModal .btn-confirm { width:100%; padding:8px; border:none; background:#333; color:#fff; border-radius:5px; cursor:pointer; }
#optionModal .btn-confirm:hover { filter:brightness(0.9); }
#modalBackdrop { display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.4); z-index:1500; }
</style>

<script type="text/javascript">
// JSP EL -> JS 변수로 contextPath 할당
var contextPath = '${pageContext.request.contextPath}';

$(function() {

    // 카드 클릭 → 상세페이지 이동
    $(".wishlist-card").on("click", function(e) {
        if ($(e.target).closest(".btn-delete, .btn-cart-move").length > 0) return;
        var productNo = $(this).data("product-no");
        location.href = contextPath + "/product/detail?productNo=" + productNo;
    });

    // 삭제 버튼
    $(".btn-delete").on("click", function(e) {
        e.stopPropagation();
        var productNo = $(this).data("product-no");
        if(!confirm("정말 삭제하시겠습니까?")) return;

        $.post(contextPath + "/rest/wishlist/toggle", { productNo }, function(response){
            if(!response.wishlisted) {
                $("#card-" + productNo).remove();
                if ($(".wishlist-card").length === 0) {
                    $(".wishlist-container").html('<p class="empty-message">위시리스트에 등록된 상품이 없습니다. 💔</p>');
                }
            } else alert("삭제 실패");
        }).fail(function(xhr){
            if(xhr.status===401) alert("로그인이 필요합니다.");
            else alert("위시리스트 삭제 오류");
        });
    });

    // 장바구니 버튼 → 옵션 모달
    $(".btn-cart-move").on("click", function(e){
        e.stopPropagation();
        var productNo = $(this).data("product-no");

        $.get(contextPath + "/rest/product/" + productNo + "/options")
        .done(function(data){
            var options = data.length > 0 ? data : [{optionNo:0, optionName:"기본 옵션"}];
            showOptionModal(productNo, options);
        })
        .fail(function(){
            var fallback = [{optionNo:0, optionName:"기본 옵션"}];
            showOptionModal(productNo, fallback);
        });
    });

    function addToCart(productNo, optionNo){
        $.post(contextPath + "/rest/cart/add",
            { productNo, optionNo, cartAmount:1 },
            function(){ alert("장바구니에 추가되었습니다!"); }
        ).fail(function(xhr){
            if(xhr.status===401) alert("로그인이 필요합니다.");
            else alert("장바구니 추가 오류");
        });
    }

    function showOptionModal(productNo, options){
        var $select = $("#optionSelect").empty();
        options.forEach(function(o){
            var disabled = o.optionStock <= 0 ? "disabled" : "";
            $select.append('<option value="'+o.optionNo+'" '+disabled+'>'+o.optionName+'</option>');
        });
        $("#modalBackdrop, #optionModal").fadeIn();

        $(".btn-confirm").off().on("click", function(){
            var optionNo = $select.val();
            if(!optionNo) { alert("옵션을 선택해주세요."); return; }
            addToCart(productNo, optionNo);
            $("#modalBackdrop, #optionModal").fadeOut();
        });

        $("#modalBackdrop").off().on("click", function(){
            $("#modalBackdrop, #optionModal").fadeOut();
        });
    }
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
            <div class="wishlist-card" id="card-${item.productNo}" data-product-no="${item.productNo}">
                <img src="${contextPath}/attachment/view?attachmentNo=${item.attachmentNo}" alt="${item.productName}">
                <h3>${item.productName}</h3>
                <p class="price"><fmt:formatNumber value="${item.productPrice}" pattern="#,##0"/>원</p>

                <div class="button-group">
                    <button type="button" class="btn btn-cart-move" data-product-no="${item.productNo}">
                        <i class="fa-solid fa-cart-shopping"></i>
                    </button>
                    <button type="button" class="btn btn-delete" data-product-no="${item.productNo}">
                        <i class="fa-solid fa-trash-can"></i>
                    </button>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<!-- 옵션 모달 -->
<div id="modalBackdrop"></div>
<div id="optionModal">
    <h3>옵션을 선택해주세요</h3>
    <select id="optionSelect"></select>
    <button class="btn-confirm">확인</button>
</div>

</body>
</html>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

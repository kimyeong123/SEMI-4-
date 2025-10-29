<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<head>

 <!-- Bootstrap CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js" integrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" crossorigin="anonymous"></script>


<script type="text/javascript">
$(document).on("click", ".wishlistIcon", function() {
    var icon = $(this);
    var productNo = icon.data("product-no");

    $.ajax({
        url: "${pageContext.request.contextPath}/rest/wishlist/toggle",
        method: "post",
        data: { productNo: productNo },
        success: function(response) {
            if(response.wishlisted) {
                icon.removeClass("fa-regular").addClass("fa-solid");
            } else {
                icon.removeClass("fa-solid").addClass("fa-regular");
            }
            icon.siblings(".wishlist-count").text(response.count);
        },
        error: function() {
            alert("로그인이 필요합니다.");
        }
    });
    
});
</script>
<style>
	.product-card-img {
	    height: 250px; /* 이미지 높이 고정 */
	    object-fit: cover; /* 이미지 비율 유지 및 잘림 */
	}
	.wishlist-section {
	    position: absolute;
	    top: 10px;
	    right: 10px;
	    background: rgba(255, 255, 255, 0.7);
	    padding: 5px;
	    border-radius: 50%;
	}
	.red {
	    color: #dc3545; /* 빨간색 하트 */
	}
	.wishlist-count {
	    font-size: 0.8rem;
	    color: #6c757d;
	    margin-left: 5px;
	}
</style>
</head>
<div class="container">
	<h1>상품 목록</h1>

	<h2>상품 수 : ${productList.size()}</h2>

	<form action="list" method="get" class="mb-4 d-flex align-items-center">
        <select name="column" class="form-select w-auto me-2">
            <option value="product_name" ${column == 'product_name' ? 'selected' : ''}>상품명</option>
            <option value="product_content" ${column == 'product_content' ? 'selected' : ''}>상품내용</option>
        </select>
        <input type="search" name="keyword" value="${keyword}" placeholder="검색어 입력" class="form-control w-auto me-2">
        
        <input type="hidden" name="order" value="${order}">
		<input type="hidden" name="categoryNo" value="${categoryNo}">
        
        <button type="submit" class="btn btn-primary me-3">검색</button>

        <div class="btn-group" role="group">
            <a href="list?column=product_price&order=${column == 'product_price' && order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&categoryNo=${categoryNo}"
               class="btn btn-outline-secondary ${column == 'product_price' ? 'active' : ''}">
                가격 ${column == 'product_price' ? (order == 'asc' ? '▼' : '▲') : ''}
            </a>
            <a href="list?column=product_avg_rating&order=${column == 'product_avg_rating' && order == 'desc' ? 'asc' : 'desc'}&keyword=${keyword}&categoryNo=${categoryNo}"
               class="btn btn-outline-secondary ${column == 'product_avg_rating' ? 'active' : ''}">
                평점 ${column == 'product_avg_rating' ? (order == 'desc' ? '▼' : '▲') : ''}
            </a>
        </div>
	</form>
    
	

    <div class="row">
		<c:forEach var="p" items="${productList}">
			
            <div class="col-md-4 mb-4">
				<div class="card h-100 shadow-sm position-relative">
                    
                    <c:choose>
                        <c:when test="${p.productThumbnailNo != null}">
                            <img
                                src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${p.productThumbnailNo}"
                                class="card-img-top product-card-img"
                                alt="${p.productName} 썸네일">
                        </c:when>
                        <c:otherwise>
                             <img src="${pageContext.request.contextPath}/images/error/no-image.png" class="card-img-top product-card-img" alt="이미지 없음">
                        </c:otherwise>
                    </c:choose>
                    
                    <div class="wishlist-section">
                        <i class="wishlistIcon ${wishlistStatus[p.productNo] ? 'fa-solid' : 'fa-regular'} fa-heart red" data-product-no="${p.productNo}"></i>
                        <span class="wishlist-count">${wishlistCounts[p.productNo]}</span>
                    </div>

                    <div class="card-body d-flex flex-column">
                        
                        <h5 class="card-title text-truncate">
                            <a href="detail?productNo=${p.productNo}" class="text-dark text-decoration-none">${p.productName}</a>
                        </h5>
                        
                        <p class="mt-auto fs-5 fw-bold text-end"><fmt:formatNumber value="${p.productPrice}" pattern="#,##0"/>원</p>
                        
                        <div class="d-flex justify-content-between align-items-center">
                            <small class="text-muted">평균 평점:</small>
                            <span class="fw-bold">${p.productAvgRating != null ? '<fmt:formatNumber value="${p.productAvgRating}" pattern="0.00"/>' : 'N/A'}</span>
                        </div>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

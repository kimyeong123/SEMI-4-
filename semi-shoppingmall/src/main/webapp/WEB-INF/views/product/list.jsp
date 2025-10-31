<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<head>
    <style>
        /* 카드 컨테이너 */
        .card-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            margin-top: 20px;
        }

        /* 개별 카드 */
        .card {
            position: relative;
            width: 30%;
            border: 1px solid #ddd;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
            display: flex;
            flex-direction: column;
            transition: transform 0.2s, box-shadow 0.2s;
            background-color: #fff;
            cursor: pointer;
            margin-bottom: 10px;
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
        }

        /* 카드 이미지 */
        .card img {
            width: 100%;
            height: 200px;
            object-fit: contain;
            background-color: #f8f9fa;
            /* 여백 배경색 */
            display: block;
            margin: 0 auto;
        }

        /* 카드 내용 */
        .card-body {
            padding: 15px;
            display: flex;
            flex-direction: column;
            flex-grow: 1;
        }

        .card-title {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 10px;
            text-overflow: ellipsis;
            white-space: nowrap;
            overflow: hidden;
        }

        .card-price {
            font-size: 16px;
            font-weight: bold;
            margin-top: auto;
            text-align: right;
        }

        .card-rating {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 14px;
            margin-top: 8px;
        }

        /* 위시리스트 하트 */
        .wishlist-section {
            position: absolute;
            top: 10px;
            right: 10px;
            display: flex;
            align-items: center;
            gap: 5px;
            transition: all 0.2s;
        }

        /* 하트 아이콘 */
        .wishlist-section i {
            font-size: 18px;
            color: #dc3545;
            cursor: pointer;
        }

        .wishlist-count {
            font-size: 0.8rem;
            color: #555;
        }

        /* 검색/정렬 영역 */
        .search-form {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
            gap: 10px;
        }

        .search-form select,
        .search-form input[type="search"],
        .search-form button {
            padding: 6px 10px;
            font-size: 14px;
        }

        .sort-buttons {
            display: flex;
            gap: 5px;
        }

        .sort-buttons a {
            padding: 6px 10px;
            border: 1px solid #888;
            text-decoration: none;
            color: #000;
            font-size: 14px;
            border-radius: 4px;
        }

        .sort-buttons a.active {
            background-color: #888;
            color: #fff;
        }
    </style>

    <script type="text/javascript">
        document.addEventListener("DOMContentLoaded", function() {
            // 카드 클릭시 이동
            document.querySelectorAll(".card").forEach(function(card) {
                card.addEventListener("click", function(e) {
                    if (e.target.classList.contains("wishlistIcon")) return; // 하트 클릭 시 이동 방지
                    var productNo = card.dataset.productNo;
                    window.location.href = "detail?productNo=" + productNo;
                });
            });

            // wishlist 클릭
            document.querySelectorAll(".wishlistIcon").forEach(function(icon) {
                icon.addEventListener("click", function(e) {
                    e.stopPropagation(); // 카드 클릭 이벤트 방지
                    var productNo = this.dataset.productNo;
                    var xhr = new XMLHttpRequest();
                    var iconEl = this;
                    xhr.open("POST", "${pageContext.request.contextPath}/rest/wishlist/toggle");
                    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                    xhr.onload = function() {
                        if (xhr.status === 200) {
                            var response = JSON.parse(xhr.responseText);
                            if (response.wishlisted) {
                                iconEl.classList.remove("fa-regular");
                                iconEl.classList.add("fa-solid");
                            } else {
                                iconEl.classList.remove("fa-solid");
                                iconEl.classList.add("fa-regular");
                            }
                            var countSpan = iconEl.nextElementSibling;
                            countSpan.textContent = response.count;
                        } else {
                            alert("로그인이 필요합니다.");
                        }
                    };
                    xhr.send("productNo=" + productNo);
                });
            });
        });
    </script>
</head>

<div class="container">
    <h1>상품 목록</h1>
    <h2>상품 수 : ${productList.size()}</h2>

    <form action="list" method="get" class="search-form">
        <select name="column">
            <option value="product_name" ${column=='product_name' ? 'selected' : '' }>상품명</option>
        </select>
        <input type="search" name="keyword" value="${keyword}" placeholder="검색어 입력">
        <input type="hidden" name="order" value="${order}">
        <input type="hidden" name="categoryNo" value="${categoryNo}">
        <button type="submit">검색</button>

        <div class="sort-buttons">
            <a href="list?column=product_price&order=${column == 'product_price' && order == 'asc' ? 'desc' : 'asc'}&keyword=${keyword}&categoryNo=${categoryNo}" class="${column == 'product_price' ? 'active' : ''}">
                가격 ${column == 'product_price' ? (order == 'asc' ? '▼' : '▲') : ''}
            </a>
            <a href="list?column=product_avg_rating&order=${column == 'product_avg_rating' && order == 'desc' ? 'asc' : 'desc'}&keyword=${keyword}&categoryNo=${categoryNo}" class="${column == 'product_avg_rating' ? 'active' : ''}">
                평점 ${column == 'product_avg_rating' ? (order == 'desc' ? '▼' : '▲') : ''}
            </a>
        </div>
    </form>

    <div class="card-container">
        <c:forEach var="p" items="${productList}">
            <div class="card" data-product-no="${p.productNo}">
                <c:choose>
                    <c:when test="${p.productThumbnailNo != null}">
                        <img src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${p.productThumbnailNo}" alt="${p.productName} 썸네일">
                    </c:when>
                    <c:otherwise>
                        <img src="${pageContext.request.contextPath}/images/error/no-image.png" alt="이미지 없음">
                    </c:otherwise>
                </c:choose>

                <c:set var="count" value="${wishlistCounts[p.productNo] != null ? wishlistCounts[p.productNo] : 0}" />

                <div class="wishlist-section">
                    <i class="wishlistIcon ${wishlistStatus[p.productNo] != null && wishlistStatus[p.productNo] ? 'fa-solid' : 'fa-regular'} fa-heart" data-product-no="${p.productNo}"></i>
                    <span class="wishlist-count">${count}</span>
                </div>

                <div class="card-body">
                    <h5 class="card-title">
                        ${p.productName}
                    </h5>

                    <p class="card-price">
                        <fmt:formatNumber value="${p.productPrice}" pattern="#,##0" />원
                    </p>

                    <div class="card-rating">
                        <small>평균 평점:</small>
                        <c:choose>
                            <c:when test="${p.productAvgRating != null}">
                                <span>
                                    <fmt:formatNumber value="${p.productAvgRating}" pattern="0.00" /></span>
                            </c:when>
                            <c:otherwise>
                                <span>N/A</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
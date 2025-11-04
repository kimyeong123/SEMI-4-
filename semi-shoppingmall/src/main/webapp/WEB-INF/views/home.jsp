<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- header include -->
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<!-- Swiper 스타일 및 스크립트 추가 -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@12/swiper-bundle.min.css" />
<script src="https://cdn.jsdelivr.net/npm/swiper@12/swiper-bundle.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>

<style>
	.main-content {
    min-height: 80vh; /* 화면 높이의 80% 이상 확보 */
    padding: 40px 20px; /* 위아래 여백 넉넉하게 */
    box-sizing: border-box;
	}
   .swiper {
    width: 100%;
    max-width: 900px;
    min-height: 600px; /* 최소 높이 지정 */
    margin: 60px auto; /* 위아래 여백 확대 */
    aspect-ratio: 16 / 9; /* 자동 비율 유지 */
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 0 10px rgba(0,0,0,0.15);
    }
    
    .swiper-slide img {
    width: 100%;
    height: 100%;
    object-fit: contain; /* cover → contain */
    background-color: #f3f3f3; /* 여백 생기면 회색 처리 */
    }
    .swiper-slide:hover img {
        transform: scale(1.05);
    }
    .swiper-button-next,
    .swiper-button-prev {
        color: #007bff;
        transition: 0.3s;
    }
    .swiper-button-next:hover,
    .swiper-button-prev:hover {
        color: #0056b3;
    }

    /* 페이지네이션 점 스타일 */
    .swiper-pagination-bullet {
        background-color: #007bff;
        opacity: 0.7;
    }
    .swiper-pagination-bullet-active {
        opacity: 1;
    }

    .btn-primary {
        background-color: #007bff;
        color: white;
        padding: 10px 20px;
        border-radius: 6px;
        text-decoration: none;
        font-weight: bold;
    }
    .btn-primary:hover {
        background-color: #0056b3;
    }
</style>

<!-- Swiper 슬라이더 -->
<div class="container w-1000 center">
    <div class="swiper">
        <div class="swiper-wrapper">
            <!--  DB에서 배너 이미지 불러오기 -->
            <c:forEach var="banner" items="${bannerList}">
                <c:if test="${not empty banner.bannerAttachmentNo}">
                    <div class="swiper-slide">
                        <a href="${banner.bannerLink}">
                            <img src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${banner.bannerAttachmentNo}" alt="${banner.bannerTitle}">
                        </a>
                    </div>
                </c:if>
            </c:forEach>

            <c:if test="${empty bannerList}">
                <div class="swiper-slide">
                    <img src="https://dummyimage.com/900x350/ddd/000.png&text=등록된+배너가+없습니다" alt="No banner">
                </div>
            </c:if>
        </div>

        <!-- 페이지네이션 및 네비게이션 -->
        <div class="swiper-pagination"></div>
        <div class="swiper-button-prev"></div>
        <div class="swiper-button-next"></div>
    </div>
</div>

<!-- Swiper 초기화 -->
<script>
    $(function () {
        new Swiper('.swiper', {
            loop: true,
            autoplay: {
                delay: 3000,
                disableOnInteraction: false
            },
            pagination: {
                el: '.swiper-pagination',
                clickable: true
            },
            navigation: {
                nextEl: '.swiper-button-next',
                prevEl: '.swiper-button-prev'
            },
            effect: 'slide',
            speed: 800
        });
    });
</script>

<!-- footer include -->
<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

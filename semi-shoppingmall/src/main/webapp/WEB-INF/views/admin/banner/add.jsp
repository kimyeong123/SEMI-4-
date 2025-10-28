<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"/>

<div class="container w-600">
    <h1>배너 등록</h1>

    <!-- ✅ 파일 업로드를 위해 반드시 enctype 추가 -->
    <form action="/admin/banner/add" method="post" enctype="multipart/form-data">

        <!-- 배너 제목 -->
        <div class="cell">
            <label>배너 제목 *</label>
            <input type="text" name="bannerTitle" required class="field w-100" placeholder="배너 이름을 입력하세요">
        </div>

        <!-- 배너 링크 -->
        <div class="cell">
            <label>배너 클릭 시 이동할 링크</label>
            <input type="text" name="bannerLink" class="field w-100" placeholder="/product/list">
        </div>

        <!-- 배너 순서 -->
        <div class="cell">
            <label>배너 표시 순서</label>
            <input type="number" name="bannerOrder" class="field w-100" value="1" min="1">
        </div>

        <!-- 배너 이미지 업로드 -->
        <div class="cell">
            <label>배너 이미지 *</label>
            <input type="file" name="attach" class="field w-100" accept="image/*" required>
            <small style="color:#666;">이미지 파일만 업로드 가능합니다 (jpg, png, gif 등)</small>
        </div>

        <div class="cell mt-30">
            <button class="btn btn-positive w-100">배너 등록</button>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"/>

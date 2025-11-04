<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"/>

<div class="container w-600">
    <h1>배너 수정</h1>

    <form action="edit" method="post" enctype="multipart/form-data">
        <input type="hidden" name="bannerNo" value="${bannerDto.bannerNo}">

        <!-- 배너 제목 -->
        <div class="cell">
            <label>배너 제목 *</label>
            <input type="text" name="bannerTitle" value="${bannerDto.bannerTitle}" required class="field w-100">
        </div>

        <!-- 배너 링크 -->
        <div class="cell">
            <label>배너 클릭 시 이동할 링크</label>
            <input type="text" name="bannerLink" value="${bannerDto.bannerLink}" class="field w-100">
        </div>

        <!-- 배너 순서 -->
        <div class="cell">
            <label>배너 표시 순서</label>
            <input type="number" name="bannerOrder" value="${bannerDto.bannerOrder}" class="field w-100" min="1">
        </div>

        <!-- 현재 이미지 -->
        <div class="cell">
            <label>현재 이미지</label><br>
            <c:if test="${not empty bannerDto.bannerAttachmentNo}">
                <img src="${pageContext.request.contextPath}/attachment/download?attachmentNo=${bannerDto.bannerAttachmentNo}" width="300" height="150" style="object-fit:cover;">
            </c:if>
            <c:if test="${empty bannerDto.bannerAttachmentNo}">
                <span style="color:#aaa;">등록된 이미지가 없습니다.</span>
            </c:if>
        </div>

        <!-- 새 이미지 업로드 -->
        <div class="cell">
            <label>새 이미지 업로드 (선택)</label>
            <input type="file" name="attach" class="field w-100" accept="image/*">
        </div>

        <div class="cell mt-30 flex-box flex-space-between">
            <button class="btn btn-positive w-45">수정 완료</button>
            <a href="list" class="btn btn-neutral w-45">목록으로</a>
        </div>
    </form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"/>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<!-- ✅ Summernote CSS & JS 추가 -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-lite.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/summernote@0.8.20/dist/summernote-lite.min.js"></script>

<form action="edit" method="post" enctype="multipart/form-data">
    <input type="hidden" name="productNo" value="${product.productNo}">

    <div class="container w-400">
        <div class="cell center">
            <h1>상품 정보 수정</h1>
        </div>

        <div class="cell">
            <label>상품 이름 <span class="red">*</span></label>
            <input type="text" name="productName" value="${product.productName}" 
                class="field w-100" required>
        </div>

        <div class="cell">
            <label>가격 <span class="red">*</span></label>
            <input type="number" name="productPrice" value="${product.productPrice}" 
                class="field w-100" required>
        </div>

        <div class="cell">
            <label>상품 설명</label>
            <!-- ✅ Summernote 적용 대상 -->
            <textarea id="productContent" name="productContent" class="field w-100">
                ${product.productContent}
            </textarea>
        </div>

        <div class="cell">
            <label>썸네일 이미지</label>
            <input type="file" name="thumbnailFile" class="field w-100">
        </div>

        <div class="cell">
            <label style="display:block">현재 썸네일</label>
            <c:if test="${product.productThumbnailNo != null}">
                <img src="/attachment/image?attachmentNo=${product.productThumbnailNo}" width="100" height="100">
            </c:if>
        </div>

        <div class="cell mt-30">
            <button type="submit" class="btn btn-negative w-100">수정</button>
        </div>
    </div>
</form>

<!-- ✅ Summernote 초기화 스크립트 -->
<script>
$(document).ready(function() {
    $('#productContent').summernote({
        placeholder: '상품 설명을 입력하세요',
        tabsize: 2,
        height: 300,
        lang: 'ko-KR',
        toolbar: [
            ['style', ['bold', 'italic', 'underline', 'clear']],
            ['font', ['fontsize', 'color']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['insert', ['link', 'picture']],
            ['view', ['fullscreen', 'codeview', 'help']]
        ]
    });
});
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

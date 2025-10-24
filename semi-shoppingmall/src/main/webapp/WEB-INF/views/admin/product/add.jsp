<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />

<div class="container w-600">
    <h1>상품 등록</h1>

    <form action="add" method="post" enctype="multipart/form-data">
        <!-- 상품 이름 -->
        <div class="cell">
            <label>상품 이름 *</label>
            <input type="text" name="productName" required class="field w-100">
        </div>

        <!-- 상품 가격 -->
        <div class="cell">
            <label>상품 가격 *</label>
            <input type="number" name="productPrice" required min="0" class="field w-100">
        </div>

        <!-- 상품 내용 -->
        <div class="cell">
            <label>상품 내용 *</label>
            <textarea name="productContent" rows="5" required class="field w-100"></textarea>
        </div>

        <!-- 부모 카테고리 -->
        <div class="cell">
            <label>부모 카테고리 *</label>
            <select name="parentCategoryNo" class="field w-100">
                <option value="">선택</option>
                <c:forEach var="c" items="${parentCategoryList}">
                    <option value="${c.categoryNo}">${c.categoryName}</option>
                </c:forEach>
            </select>
        </div>

        <!-- 하위 카테고리 -->
        <div class="cell">
            <label>하위 카테고리 *</label>
            <select name="childCategoryNo" class="field w-100">
                <option value="">선택</option>
                <c:forEach var="c" items="${childCategoryList}">
                    <option value="${c.categoryNo}">${c.categoryName}</option>
                </c:forEach>
            </select>
        </div>

        <!-- 썸네일 -->
        <div class="cell">
            <label>썸네일 *</label>
            <input type="file" name="thumbnailFile" required class="field w-100">
        </div>

        <!-- 상세 이미지 -->
        <div class="cell">
            <label>상세 이미지</label>
            <input type="file" name="detailImageList" multiple>
        </div>

        <!-- 등록 버튼 -->
        <div class="cell mt-30">
            <button class="btn btn-positive w-100">상품 등록</button>
        </div>
    </form>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
$(function() {
    // 부모 카테고리 변경 시 하위 카테고리 가져오기
    $('select[name="parentCategoryNo"]').change(function() {
        var parentNo = $(this).val();
        var $child = $('select[name="childCategoryNo"]');
        
        if (!parentNo) {
            $child.html('<option value="">선택</option>');
            return;
        }

        $.getJSON('<c:url value="/admin/category/children"/>', { parentCategoryNo: parentNo }, function(data) {
            $child.empty().append('<option value="">선택</option>');
            $.each(data, function(i, c) {
                $child.append('<option value="' + c.categoryNo + '">' + c.categoryName + '</option>');
            });
        });
    });
});
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />

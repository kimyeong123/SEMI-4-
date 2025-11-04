<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/commons.css">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!-- Summernote -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/lang/summernote-ko-KR.min.js"></script>
<!-- option 관리용 js -->
<script src="${pageContext.request.contextPath}/js/option.js"></script>


<div class="container w-600">
    <h1>상품 등록</h1>

    <form action="add" method="post" enctype="multipart/form-data">

        <!-- 상품 기본 정보 -->
        <div class="cell">
            <label>상품 이름 *</label>
            <input type="text" name="productName" required class="field w-100">
        </div>
        
        <div class="cell">
            <label>상품 가격 *</label>
            <input type="number" name="productPrice" required min="0" class="field w-100">
        </div>

        <!-- 상품 내용 -->
        <div class="cell">
            <label>상품 내용 *</label>
            <textarea name="productContent" rows="5" required class="field w-100 summernote-editor"></textarea>
        </div>

        <!-- 카테고리 선택 -->
        <div class="cell">
            <label>부모 카테고리 *</label>
            <select name="parentCategoryNo" class="field w-100">
                <option value="">선택</option>
                <c:forEach var="c" items="${parentCategoryList}">
                    <option value="${c.categoryNo}">${c.categoryName}</option>
                </c:forEach>
            </select>
        </div>

        <div class="cell">
            <label>하위 카테고리 *</label>
            <select name="childCategoryNo" class="field w-100">
                <option value="">선택</option>
                <c:forEach var="c" items="${childCategoryList}">
                    <option value="${c.categoryNo}">${c.categoryName}</option>
                </c:forEach>
            </select>
        </div>
        
        <div class="cell mt-30">
            <label>썸네일 *</label>
            <input type="file" name="thumbnailFile" required class="field w-100">
        </div>
        <div class="cell mt-30">
            <button class="btn btn-positive w-100">상품 등록</button>
        </div>
    </form>
</div>

<script>
$(function() {

    // Summernote 설정
    $(".summernote-editor").summernote({
        height: 250,
        minHeight: 200,
        maxHeight: 400,
        lang: "ko-KR",
        placeholder: "상품 상세 내용을 입력하세요 (이미지 업로드 가능)",
        callbacks: {
            onImageUpload: function(files) {
                var form = new FormData();
                for (var i = 0; i < files.length; i++) {
                    form.append("attach", files[i]);
                }

                $.ajax({
                    processData: false,
                    contentType: false,
                    url: "${pageContext.request.contextPath}/attachment/upload",
                    method: "post",
                    data: form,
                    success: function(response) {
                        for (var i = 0; i < response.length; i++) {
                            var img = $("<img>")
                                .attr("src", "${pageContext.request.contextPath}/attachment/download?attachmentNo=" + response[i])
                                .addClass("custom-image");
                            $(".summernote-editor").summernote("insertNode", img[0]);
                        }
                    },
                    error: function() {
                        alert("이미지 업로드 실패! 서버 로그 확인 필요");
                    }
                });
            }
        }
    });

    // 하위 카테고리 불러오기
    $("select[name='parentCategoryNo']").change(function() {
        var parentNo = $(this).val();
        var $child = $("select[name='childCategoryNo']");

        if (!parentNo) {
            $child.html("<option value=''>선택</option>");
            return;
        }

        $.ajax({
            url: "${pageContext.request.contextPath}/admin/category/children",
            method: "get",
            data: { parentCategoryNo: parentNo },
            success: function(data) {
                $child.empty().append("<option value=''>선택</option>");
                for (var i = 0; i < data.length; i++) {
                    var c = data[i];
                    $child.append("<option value='" + c.categoryNo + "'>" + c.categoryName + "</option>");
                }
            },
            error: function() {
                alert("하위 카테고리 불러오기 실패");
            }
        });
    });
});
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />

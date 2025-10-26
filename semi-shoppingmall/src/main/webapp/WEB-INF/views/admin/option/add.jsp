<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />

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

        <div class="cell">
            <label>상품 내용 *</label>
            <textarea name="productContent" rows="5" required class="field w-100"></textarea>
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

        <!-- ✅ 옵션 등록 폼 -->
        <div class="cell mt-40">
            <h3>옵션 등록</h3>
            <div id="option-container">
                <div class="option-set">
                    <div class="cell">
                        <label>옵션 이름 *</label>
                        <input type="text" name="optionNameList" placeholder="예: 색상" required class="field w-100">
                    </div>

                    <div class="cell">
                        <label>옵션 값 *</label>
                        <div class="option-values" style="display:flex; flex-wrap:wrap; gap:5px;">
                            <div class="option-item">
                                <input type="text" name="optionValueList" placeholder="예: 빨강" class="field option-field">
                                <button type="button" class="btn btn-delete-value">−</button>
                            </div>
                        </div>
                        <button type="button" class="btn btn-add-value mt-10">+ 값 추가</button>
                    </div>

                    <button type="button" class="btn btn-danger btn-remove-set mt-10">옵션 세트 삭제</button>
                    <hr>
                </div>
            </div>

            <button type="button" id="btn-add-set" class="btn btn-positive mt-10">+ 옵션 세트 추가</button>
        </div>

        <!-- 이미지 업로드 -->
        <div class="cell mt-30">
            <label>썸네일 *</label>
            <input type="file" name="thumbnailFile" required class="field w-100">
        </div>

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

<style>
    .cell { margin-bottom:15px; }
    .field { padding:8px; border:1px solid #ccc; border-radius:5px; }
    .option-set { border:1px solid #ddd; border-radius:10px; padding:15px; margin-bottom:15px; background:#fafafa; }
    .option-item { display:flex; align-items:center; gap:5px; margin-bottom:5px; }
    .option-field { width:120px; text-align:center; }
    .btn { border:none; padding:6px 10px; border-radius:5px; cursor:pointer; }
    .btn-delete-value { background:#ddd; }
    .btn-positive { background:#4CAF50; color:#fff; }
    .btn-danger { background:#e74c3c; color:white; }
</style>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
$(function() {

    // ✅ 카테고리 하위 목록 불러오기
    $('select[name="parentCategoryNo"]').change(function() {
        var parentNo = $(this).val();
        var $child = $('select[name="childCategoryNo"]');

        if (!parentNo) {
            $child.html('<option value="">선택</option>');
            return;
        }

        $.getJSON('${pageContext.request.contextPath}/admin/category/children', 
            { parentCategoryNo: parentNo }, 
            function(data) {
                $child.empty().append('<option value="">선택</option>');
                $.each(data, function(i, c) {
                    $child.append('<option value="' + c.categoryNo + '">' + c.categoryName + '</option>');
                });
            }
        ).fail(function() {
            alert("하위 카테고리 로드 실패");
        });
    });

    // ✅ 옵션 세트 추가
    $('#btn-add-set').click(function(){
        const newSet = `
        <div class="option-set">
            <div class="cell">
                <label>옵션 이름 *</label>
                <input type="text" name="optionNameList" placeholder="예: 사이즈" required class="field w-100">
            </div>

            <div class="cell">
                <label>옵션 값 *</label>
                <div class="option-values" style="display:flex; flex-wrap:wrap; gap:5px;">
                    <div class="option-item">
                        <input type="text" name="optionValueList" placeholder="예: S" class="field option-field">
                        <button type="button" class="btn btn-delete-value">−</button>
                    </div>
                </div>
                <button type="button" class="btn btn-add-value mt-10">+ 값 추가</button>
            </div>

            <button type="button" class="btn btn-danger btn-remove-set mt-10">옵션 세트 삭제</button>
            <hr>
        </div>`;
        $('#option-container').append(newSet);
    });

    // ✅ 옵션 세트 삭제
    $(document).on('click', '.btn-remove-set', function(){
        if ($('.option-set').length > 1) {
            $(this).closest('.option-set').remove();
        } else {
            alert('최소 한 개의 옵션 세트는 필요합니다.');
        }
    });

    // ✅ 옵션 값 추가
    $(document).on('click', '.btn-add-value', function(){
        const newValue = `
            <div class="option-item">
                <input type="text" name="optionValueList" placeholder="값 입력" class="field option-field">
                <button type="button" class="btn btn-delete-value">−</button>
            </div>`;
        $(this).siblings('.option-values').append(newValue);
    });

    // ✅ 옵션 값 삭제
    $(document).on('click', '.btn-delete-value', function(){
        const $values = $(this).closest('.option-values').find('.option-item');
        if ($values.length > 1) {
            $(this).closest('.option-item').remove();
        } else {
            alert('옵션 값은 최소 하나 이상 필요합니다.');
        }
    });
});
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />

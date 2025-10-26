<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />

<div class="container w-600">
    <h1>${product.productName} 옵션 관리</h1>

    <!-- 옵션 등록 폼 -->
    <form action="${pageContext.request.contextPath}/admin/product/option/add" method="post">
        <input type="hidden" name="productNo" value="${product.productNo}">
        
        <div class="cell">
            <label>옵션 이름</label>
            <input type="text" name="optionName" class="field w-100" placeholder="예: 색상, 사이즈" required>
        </div>
        <div class="cell">
            <label>옵션 값</label>
            <input type="text" name="optionValue" class="field w-100" placeholder="예: 빨강, XL" required>
        </div>
        <div class="cell">
            <label>재고</label>
            <input type="number" name="optionStock" class="field w-100" placeholder="예: 10" min="0" required>
        </div>
        <div class="cell">
            <button class="btn btn-positive w-100">옵션 등록</button>
        </div>
    </form>

    <hr>

    <!-- 옵션 목록 -->
    <h3>등록된 옵션</h3>
    <table border="1" width="100%">
        <thead>
            <tr>
                <th>옵션 이름</th>
                <th>옵션 값</th>
                <th>재고</th>
                <th>관리</th>
            </tr>
        </thead>
        <tbody id="optionTable">
            <c:forEach var="opt" items="${optionList}">
                <tr>
                    <td class="opt-name">${opt.optionName}</td>
                    <td class="opt-value">${opt.optionValue}</td>
                    <td class="opt-stock">${opt.optionStock}</td>
                    <td>
                        <button class="btn-edit" data-option-no="${opt.optionNo}">수정</button>
                        <button class="btn-update" data-option-no="${opt.optionNo}" style="display:none;">완료</button>
                        <a href="${pageContext.request.contextPath}/admin/product/option/delete?optionNo=${opt.optionNo}&productNo=${opt.productNo}" 
                           onclick="return confirm('삭제하시겠습니까?');" class="btn-delete">삭제</a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty optionList}">
                <tr><td colspan="4" style="text-align:center;">등록된 옵션이 없습니다.</td></tr>
            </c:if>
        </tbody>
    </table>

    <div style="margin-top:20px;">
        <a href="${pageContext.request.contextPath}/admin/product/detail?productNo=${product.productNo}" 
           class="btn btn-secondary w-100">상품 상세로 돌아가기</a>
    </div>
</div>

<style>
.cell { margin-bottom:15px; }
.field { padding:8px; border:1px solid #ccc; border-radius:5px; }
.btn { border:none; padding:6px 10px; border-radius:5px; cursor:pointer; margin-right:4px; }
.btn-positive { background:#4CAF50; color:white; }
.btn-secondary { background:#777; color:white; text-decoration:none; display:block; text-align:center; padding:8px; border-radius:5px; }
.btn-edit { background:#2196F3; color:white; }
.btn-delete { background:#f44336; color:white; text-decoration:none; padding:6px 10px; border-radius:5px; }
</style>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script>
$(function() {
    // 수정 버튼 클릭 시 인라인 편집 활성화
    $(document).on("click", ".btn-edit", function() {
        const tr = $(this).closest("tr");
        const name = tr.find(".opt-name").text();
        const value = tr.find(".opt-value").text();
        const stock = tr.find(".opt-stock").text();

        tr.find(".opt-name").html('<input type="text" class="edit-name" value="'+name+'">');
        tr.find(".opt-value").html('<input type="text" class="edit-value" value="'+value+'">');
        tr.find(".opt-stock").html('<input type="number" class="edit-stock" value="'+stock+'">');

        $(this).hide();
        tr.find(".btn-update").show();
    });

    // 수정 완료 버튼 클릭 시 AJAX로 DB 반영
    $(document).on("click", ".btn-update", function() {
        const tr = $(this).closest("tr");
        const optionNo = $(this).data("option-no");
        const optionName = tr.find(".edit-name").val();
        const optionValue = tr.find(".edit-value").val();
        const optionStock = tr.find(".edit-stock").val();

        $.ajax({
            url: "${pageContext.request.contextPath}/admin/product/option/edit",
            type: "post",
            data: {
                optionNo: optionNo,
                optionName: optionName,
                optionValue: optionValue,
                optionStock: optionStock
            },
            success: function() {
                alert("옵션 수정 완료!");
                location.reload();
            },
            error: function() {
                alert("수정 실패");
            }
        });
    });
});
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />

<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />


<script>
$(function() {
    var skuIndex = ${optionList.size()}; // 기존 목록 수에 이어서 인덱스 시작

    // "조합 생성하기" 버튼 클릭
    $("#generate-sku-btn").on("click", function() {
        var name1 = $("#option1-name").val().trim(); // 예: "색상"
        var values1 = $("#option1-values").val().trim().split(',')
                        .map(s => s.trim()).filter(s => s); // ["빨강", "파랑"]
        
        var name2 = $("#option2-name").val().trim(); // 예: "사이즈"
        var values2 = $("#option2-values").val().trim().split(',')
                        .map(s => s.trim()).filter(s => s); // ["S", "M"]

        if (values1.length === 0) {
            alert("옵션 1 값을 콤마로 구분하여 입력하세요.");
            return;
        }

        // 1. 옵션이 1개일 경우 (예: 신발 사이즈)
        if (values2.length === 0) {
            values1.forEach(function(val1) {
                var skuName = (name1 ? name1 + ": " : "") + val1; // 예: "사이저: 250" 또는 "250"
                addSkuRow(skuName, 0);
            });
        } 
        // 2. 옵션이 2개일 경우 (조합 생성)
        else {
            values1.forEach(function(val1) {
                values2.forEach(function(val2) {
                    // 예: "색상: 빨강 / 사이즈: S"
                    var skuName = (name1 ? name1 + ": " : "") + val1 + 
                                  " / " + 
                                  (name2 ? name2 + ": " : "") + val2;
                    addSkuRow(skuName, 0);
                });
            });
        }
    });

    // SKU 테이블에 행(row) 추가
    function addSkuRow(name, stock) {
        var html = '<tr>' +
            '<td>' +
                '<input type="text" name="optionList[' + skuIndex + '].optionName" class="field" value="' + name + '" readonly>' +
            '</td>' +
            '<td>' +
                '<input type="number" name="optionList[' + skuIndex + '].optionStock" class="field" value="' + stock + '" min="0">' +
            '</td>' +
            '<td>' +
                '<span class="btn-delete-sku"><i class="fa-solid fa-trash"></i></span>' +
            '</td>' +
        '</tr>';
        $("#sku-table-body").append(html);
        skuIndex++;
    }

    // SKU 행 삭제 버튼
    $("#sku-table-body").on("click", ".btn-delete-sku", function() {
        if(confirm("이 옵션 조합을 삭제하시겠습니까?")) {
            $(this).closest("tr").remove();
            // (참고) 인덱스(skuIndex)를 재정렬할 필요는 없음. 서버는 받는 대로 처리함.
        }
    });
});
</script>

<style>
/* ... (기존 CSS와 공통 CSS 사용) ... */
.option-group { border: 1px solid #ddd; padding: 15px; margin-bottom: 15px; border-radius: 5px; }
.option-values { font-style: italic; color: #555; }
#sku-table th, #sku-table td { text-align: left; padding: 10px; }
#sku-table input[type="text"], #sku-table input[type="number"] { width: 90%; }
.btn-delete-sku { color: #e74c3c; cursor: pointer; }
</style>

<div class="container w-800">
    <h1>"${product.productName}" - 옵션 조합(SKU) 관리</h1>

    <!-- 1. 옵션 정의 섹션 -->
    <div class="option-group">
        <h3>옵션 조합 생성</h3>
        <p class="gray">옵션 값은 콤마(,)로 구분하여 입력하세요.</p>
        <div class="cell flex-box" style="gap: 10px;">
            <input type="text" id="option1-name" class="field" placeholder="옵션 1 이름 (예: 색상)">
            <input type="text" id="option1-values" class="field flex-fill" placeholder="옵션 1 값 (예: 빨강, 파랑, 검정)">
        </div>
        <div class="cell flex-box" style="gap: 10px;">
            <input type="text" id="option2-name" class="field" placeholder="옵션 2 이름 (예: 사이즈)">
            <input type="text" id="option2-values" class="field flex-fill" placeholder="옵션 2 값 (예: S, M, L)">
        </div>
        <button type="button" id="generate-sku-btn" class="btn btn-neutral">조합 생성하기</button>
    </div>

    <!-- 2. SKU 등록 폼 -->
    <form action="save" method="post">
        <input type="hidden" name="productNo" value="${product.productNo}">
        
        <h3>생성된 SKU 목록</h3>
        <table id="sku-table" class="table table-border w-100">
            <thead>
                <tr>
                    <th>옵션 조합 이름 (optionName)</th>
                    <th>재고 (optionStock)</th>
                    <th>삭제</th>
                </tr>
            </thead>
            <tbody id="sku-table-body">
                <%-- 기존에 저장된 SKU 목록 (수정용) --%>
                <c:forEach var="opt" items="${optionList}" varStatus="status">
                    <tr>
                        <td>
                            <input type="text" name="optionList[${status.index}].optionName" class="field" value="${opt.optionName}" readonly>
                        </td>
                        <td>
                            <input type="number" name="optionList[${status.index}].optionStock" class="field" value="${opt.optionStock}" min="0">
                        </td>
                        <td>
                            <span class="btn-delete-sku"><i class="fa-solid fa-trash"></i></span>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        
        <button type="submit" class="btn btn-positive w-100 mt-20">전체 SKU 저장하기</button>
    </form>
</div>


<jsp:include page="/WEB-INF/views/template/footer.jsp" />

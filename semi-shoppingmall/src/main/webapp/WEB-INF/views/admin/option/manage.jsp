<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/template/header.jsp" />

<div class="container w-800">
    <h1>${product.productName} - 옵션 관리</h1>

    <!-- ✅ 옵션 추가 폼 -->
    <form action="add" method="post" style="margin-bottom:20px;">
        <input type="hidden" name="productNo" value="${product.productNo}">
        <div class="cell">
            <label>옵션 이름</label>
            <input type="text" name="optionName" required class="field w-100">
        </div>
        <div class="cell">
            <label>옵션 값</label>
            <input type="text" name="optionValue" required class="field w-100">
        </div>
        <div class="cell">
            <label>재고</label>
            <input type="number" name="optionStock" min="0" value="0" class="field w-100">
        </div>
        <button class="btn btn-positive w-100 mt-10">+ 옵션 추가</button>
    </form>

    <!-- ✅ 옵션 목록 -->
    <table class="table table-border table-hover w-100">
        <thead>
            <tr>
                <th>번호</th>
                <th>옵션 이름</th>
                <th>옵션 값</th>
                <th>재고</th>
                <th>수정 / 삭제</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty optionList}">
                    <tr>
                        <td colspan="5" style="text-align:center;">등록된 옵션이 없습니다.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="opt" items="${optionList}">
                        <tr>
                            <td>${opt.optionNo}</td>
                            <td>${opt.optionName}</td>
                            <td>${opt.optionValue}</td>
                            <td>${opt.optionStock}</td>
                            <td>
                                <button type="button" class="btn btn-warning btn-edit" data-no="${opt.optionNo}">수정</button>
                                <a href="delete?optionNo=${opt.optionNo}&productNo=${product.productNo}" class="btn btn-danger">삭제</a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>

<!-- ✅ CSS -->
<style>
.cell { margin-bottom: 12px; }
.field { padding:8px; border:1px solid #ccc; border-radius:5px; }
.btn { border:none; padding:6px 10px; border-radius:5px; cursor:pointer; }
.btn-positive { background:#4CAF50; color:white; }
.btn-warning { background:#f39c12; color:white; }
.btn-danger { background:#e74c3c; color:white; }
.table { border-collapse: collapse; width: 100%; margin-top: 15px; }
.table th, .table td { border: 1px solid #ccc; padding: 8px; text-align: center; }
.table-hover tr:hover { background: #fafafa; }
.mt-10 { margin-top:10px; }
</style>

<!-- ✅ JS -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
$(function() {
    $(".btn-edit").click(function() {
        var optionNo = $(this).data("no");
        var row = $(this).closest("tr");
        var name = prompt("옵션 이름 수정", row.find("td:eq(1)").text());
        var value = prompt("옵션 값 수정", row.find("td:eq(2)").text());
        var stock = prompt("재고 수정", row.find("td:eq(3)").text());

        if (!name || !value || stock === null) return;

        $.ajax({
            url: "edit",
            type: "POST",
            data: {
                optionNo: optionNo,
                optionName: name,
                optionValue: value,
                optionStock: stock
            },
            success: function() {
                alert("수정 완료!");
                location.reload();
            },
            error: function() {
                alert("수정 실패!");
            }
        });
    });
});
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<script>
$(function() {

    // === 옵션 등록 (AJAX 등록) ===
    $("#option-add-form").submit(function(e) {
        e.preventDefault();

        const formData = $(this).serialize();

        $.ajax({
            url: "${pageContext.request.contextPath}/admin/product/option/add",
            type: "post",
            data: formData,
            success: function() {
                alert("옵션이 등록되었습니다!");
                location.reload();
            },
            error: function() {
                alert("옵션 등록 중 오류 발생");
            }
        });
    });

    // === 옵션 삭제 ===
    $(document).on("click", ".btn-option-delete", function() {
        const optionNo = $(this).data("option-no");
        const productNo = $(this).data("product-no");
        if (!confirm("정말 삭제하시겠습니까?")) return;

        $.ajax({
            url: "${pageContext.request.contextPath}/admin/product/option/delete",
            type: "get",
            data: { optionNo: optionNo, productNo: productNo },
            success: function() {
                alert("삭제 완료!");
                location.reload();
            },
            error: function() {
                alert("삭제 중 오류 발생");
            }
        });
    });

    // === 옵션 수정 ===
    $(document).on("click", ".btn-option-edit", function() {
        const tr = $(this).closest("tr");
        const name = tr.find(".opt-name").text().trim();
        const value = tr.find(".opt-value").text().trim();
        const stock = tr.find(".opt-stock").text().trim();

        tr.find(".opt-name").html('<input type="text" class="edit-name" value="' + name + '">');
        tr.find(".opt-value").html('<input type="text" class="edit-value" value="' + value + '">');
        tr.find(".opt-stock").html('<input type="number" class="edit-stock" value="' + stock + '">');

        $(this).hide();
        tr.find(".btn-option-update").show();
    });

    // === 수정 완료 ===
    $(document).on("click", ".btn-option-update", function() {
        const tr = $(this).closest("tr");
        const optionNo = $(this).data("option-no");
        const name = tr.find(".edit-name").val();
        const value = tr.find(".edit-value").val();
        const stock = tr.find(".edit-stock").val();

        $.ajax({
            url: "${pageContext.request.contextPath}/admin/product/option/edit",
            type: "post",
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
                alert("수정 실패");
            }
        });
    });
});
</script>

<div class="container w-800">
    <h1>상품 상세정보</h1>

    <c:if test="${product.productThumbnailNo != null}">
        <img src="${pageContext.request.contextPath}/attachment/view?attachmentNo=${product.productThumbnailNo}"
             width="150" height="150" style="object-fit: cover;">
    </c:if>

    <p>${product.productName}를(을) 위시리스트에 추가한 사람 수: ${wishlistCount}</p>
    <br>

    <table border="1" width="100%">
        <tr><th width="25%">번호</th><td>${product.productNo}</td></tr>
        <tr><th>이름</th><td>${product.productName}</td></tr>
        <tr><th>가격</th><td><fmt:formatNumber value="${product.productPrice}" type="number"/>원</td></tr>
        <tr><th>설명</th><td>${product.productContent}</td></tr>
        <tr><th>평균 평점</th><td>${product.productAvgRating}</td></tr>
    </table>

    <hr>

    <!-- ✅ 옵션 등록 폼 -->
    <h3>옵션 등록</h3>
    <form id="option-add-form">
        <input type="hidden" name="productNo" value="${product.productNo}">
        <table border="1" width="100%" style="margin-bottom: 20px;">
            <tr>
                <th>옵션 이름</th>
                <th>옵션 값</th>
                <th>재고</th>
                <th>등록</th>
            </tr>
            <tr>
                <td><input type="text" name="optionName" class="field w-100" placeholder="예: 색상" required></td>
                <td><input type="text" name="optionValue" class="field w-100" placeholder="예: 빨강" required></td>
                <td><input type="number" name="optionStock" class="field w-100" value="0" min="0" required></td>
                <td><button type="submit" class="btn btn-positive">등록</button></td>
            </tr>
        </table>
    </form>

    <!-- ✅ 옵션 목록 -->
    <h3>옵션 목록</h3>
    <table border="1" width="100%">
        <thead>
            <tr>
                <th>옵션 이름</th>
                <th>옵션 값</th>
                <th>재고</th>
                <th>관리</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="opt" items="${optionList}">
                <tr>
                    <td class="opt-name">${opt.optionName}</td>
                    <td class="opt-value">${opt.optionValue}</td>
                    <td class="opt-stock">${opt.optionStock}</td>
                    <td>
                        <button class="btn-option-edit" data-option-no="${opt.optionNo}">수정</button>
                        <button class="btn-option-update" data-option-no="${opt.optionNo}" style="display:none;">완료</button>
                        <button class="btn-option-delete" data-option-no="${opt.optionNo}" data-product-no="${product.productNo}">삭제</button>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty optionList}">
                <tr><td colspan="4" style="text-align:center;">등록된 옵션이 없습니다.</td></tr>
            </c:if>
        </tbody>
    </table>

    <hr>

    <!-- ✅ 리뷰 목록 -->
    <h3>리뷰 목록</h3>
    <table class="w-100 table table-border">
        <thead>
            <tr>
                <th>작성자</th>
                <th>평점</th>
                <th>내용</th>
                <th>작성일</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="review" items="${reviewList}">
                <tr id="review-${review.reviewNo}">
                    <td>${review.memberNickname}</td>
                    <td>
                        <c:forEach begin="1" end="${review.reviewRating}">
                            <i class="fa-solid fa-star gold"></i>
                        </c:forEach>
                        <c:forEach begin="${review.reviewRating + 1}" end="5">
                            <i class="fa-regular fa-star"></i>
                        </c:forEach>
                    </td>
                    <td class="review-content">${review.reviewContent}</td>
                    <td><fmt:formatDate value="${review.reviewCreatedAt}" pattern="yyyy-MM-dd" /></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div style="margin-top: 20px;">
        <a href="list" class="btn btn-secondary">목록으로 이동</a>
        <a href="edit?productNo=${product.productNo}" class="btn btn-primary">수정하기</a>

        <form action="${pageContext.request.contextPath}/admin/product/delete"
              method="post" style="display:inline;"
              onsubmit="return confirm('정말 삭제하시겠습니까?');">
            <input type="hidden" name="productNo" value="${product.productNo}">
            <button type="submit" class="btn btn-danger">삭제하기</button>
        </form>
    </div>
</div>

<style>
    .field { padding:6px; border:1px solid #ccc; border-radius:5px; }
    .btn { border:none; padding:6px 10px; border-radius:5px; cursor:pointer; }
    .btn-positive { background:#4CAF50; color:white; }
    .btn-primary { background:#2196F3; color:white; }
    .btn-secondary { background:#999; color:white; text-decoration:none; padding:6px 12px; border-radius:5px; }
    .btn-danger { background:#e74c3c; color:white; }
</style>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />

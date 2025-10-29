<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<style>
    .container {
        width: 90%;
        max-width: 1200px;
        margin: 40px auto;
        background-color: #f7f9fc;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    }
    h1 {
        border-bottom: 2px solid #ddd;
        padding-bottom: 15px;
        margin-bottom: 30px;
        color: #333;
    }
    h3 {
        color: #555;
        border-left: 5px solid #007bff;
        padding-left: 10px;
        margin-top: 30px;
        margin-bottom: 15px;
    }
    .field {
        padding: 8px;
        border: 1px solid #ccc;
        border-radius: 4px;
        width: 95%;
        box-sizing: border-box;
    }
    .btn {
        border: none;
        padding: 8px 15px;
        border-radius: 4px;
        cursor: pointer;
        font-weight: bold;
        transition: background-color 0.2s;
        text-decoration: none;
        display: inline-block;
        text-align: center;
    }
    .btn-positive { background-color: #28a745; color: white; }
    .btn-positive:hover { background-color: #218838; }
    .btn-neutral { background-color: #6c757d; color: white; }
    .btn-neutral:hover { background-color: #5a6268; }
    .btn-negative { background-color: #dc3545; color: white; }
    .btn-negative:hover { background-color: #c82333; }
    table {
        border-collapse: collapse;
        width: 100%;
        margin-bottom: 20px;
        background-color: white;
        border: 1px solid #dee2e6;
        border-radius: 4px;
        overflow: hidden;
    }
    th, td {
        padding: 12px 15px;
        text-align: left;
        border: 1px solid #dee2e6;
    }
    th {
        background-color: #e9ecef;
        color: #495057;
        font-weight: 600;
        text-align: center;
    }
    table tbody tr:hover {
        background-color: #f8f9fa;
    }
    .gold { color: gold; }
</style>

<script>
$(function() {
    // ✅ 옵션 등록
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
                alert("옵션 등록 중 오류 발생!");
            }
        });
    });

    // ✅ 옵션 삭제
    $(document).on("click", ".btn-option-delete", function() {
        const optionNo = $(this).data("option-no");
        if (!confirm("정말 삭제하시겠습니까?")) return;
        $.ajax({
            url: "${pageContext.request.contextPath}/admin/product/option/delete",
            type: "post",
            data: { optionNo: optionNo },
            success: function() {
                alert("삭제 완료!");
                location.reload();
            },
            error: function() {
                alert("삭제 중 오류 발생");
            }
        });
    });

    // ✅ 옵션 수정
    $(document).on("click", ".btn-option-edit", function() {
        const tr = $(this).closest("tr");
        const name = tr.find(".opt-name").text().trim();
        const value = tr.find(".opt-value").text().trim();
        const stock = tr.find(".opt-stock").text().trim();

        tr.find(".opt-name").html(`<input type="text" class="field edit-name" value="${name}">`);
        tr.find(".opt-value").html(`<input type="text" class="field edit-value" value="${value}">`);
        tr.find(".opt-stock").html(`<input type="number" class="field edit-stock" value="${stock}" min="0">`);

        $(this).hide();
        tr.find(".btn-option-update").show();
    });

    // ✅ 옵션 수정 완료
    $(document).on("click", ".btn-option-update", function() {
        const tr = $(this).closest("tr");
        const optionNo = $(this).data("option-no");
        const name = tr.find(".edit-name").val();
        const value = tr.find(".edit-value").val();
        const stock = tr.find(".edit-stock").val();

        if (!name || !value || stock === undefined) {
            alert("모든 필드를 채워주세요.");
            return;
        }

        $.ajax({
            url: "${pageContext.request.contextPath}/admin/product/option/edit",
            type: "post",
            data: { optionNo, optionName: name, optionValue: value, optionStock: stock },
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

    <table>
        <tr><th>상품 번호</th><td>${product.productNo}</td></tr>
        <tr><th>이름</th><td>${product.productName}</td></tr>
        <tr><th>가격</th><td><fmt:formatNumber value="${product.productPrice}" type="number"/>원</td></tr>
        <tr><th>설명</th><td>${product.productContent}</td></tr>
        <tr><th>평균 평점</th><td><fmt:formatNumber value="${product.productAvgRating}" pattern="0.00"/></td></tr>
    </table>

    <!-- ✅ 옵션 등록 -->
    <h3>옵션 등록</h3>
    <form id="option-add-form">
        <input type="hidden" name="productNo" value="${product.productNo}">
        <table>
            <tr>
                <th>옵션 이름</th>
                <th>옵션 값</th>
                <th>재고</th>
                <th>등록</th>
            </tr>
            <tr>
                <td>
                    <select name="optionName" class="field w-100">
                        <option value="색상">색상</option>
                        <option value="사이즈">사이즈</option>
                    </select>
                </td>
                <td><input type="text" name="optionValue" class="field w-100" placeholder="예: 빨강" required></td>
                <td><input type="number" name="optionStock" class="field w-100" value="10" min="0" required></td>
                <td><button type="submit" class="btn btn-positive">등록</button></td>
            </tr>
        </table>
    </form>

    <!-- ✅ 옵션 목록 -->
    <h3>옵션 목록</h3>
    <table>
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
                        <button class="btn btn-neutral btn-option-edit" data-option-no="${opt.optionNo}">수정</button>
                        <button class="btn btn-positive btn-option-update" data-option-no="${opt.optionNo}" style="display:none;">완료</button>
                        <button class="btn btn-negative btn-option-delete" data-option-no="${opt.optionNo}">삭제</button>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty optionList}">
                <tr><td colspan="4" style="text-align:center;">등록된 옵션이 없습니다.</td></tr>
            </c:if>
        </tbody>
    </table>

    <!-- ✅ 리뷰 목록 -->
    <h3>리뷰 목록</h3>
    <table>
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
                <tr>
                    <td>${review.memberNickname}</td>
                    <td>
                        <c:forEach begin="1" end="${review.reviewRating}">
                            <i class="fa-solid fa-star gold"></i>
                        </c:forEach>
                        <c:forEach begin="${review.reviewRating + 1}" end="5">
                            <i class="fa-regular fa-star"></i>
                        </c:forEach>
                    </td>
                    <td>${review.reviewContent}</td>
                    <td><fmt:formatDate value="${review.reviewCreatedAt}" pattern="yyyy-MM-dd"/></td>
                </tr>
            </c:forEach>
            <c:if test="${empty reviewList}">
                <tr><td colspan="4" style="text-align:center;">등록된 리뷰가 없습니다.</td></tr>
            </c:if>
        </tbody>
    </table>

    <div style="margin-top: 20px;">
        <a href="list" class="btn btn-neutral">상품 목록</a>
        <a href="edit?productNo=${product.productNo}" class="btn btn-positive">수정하기</a>
        <form action="${pageContext.request.contextPath}/admin/product/delete"
              method="post" style="display:inline;"
              onsubmit="return confirm('정말 삭제하시겠습니까?');">
            <input type="hidden" name="productNo" value="${product.productNo}">
            <button type="submit" class="btn btn-negative">삭제하기</button>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />

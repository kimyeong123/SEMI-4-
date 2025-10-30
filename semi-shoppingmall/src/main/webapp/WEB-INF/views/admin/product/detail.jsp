<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<%-- (Summernote 관련 링크는 이 페이지에서 필요 없어 보이므로 생략 가능) --%>

<style>
/* ... (기존 스타일은 그대로 사용 ... */
h1 {font-size: 1.8em;padding-bottom: 10px;margin-bottom: 30px;color: #333;border-bottom: 1px solid #ddd;}
h3 { color: #444; font-size: 1.5em; margin-top: 30px; margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
.field, .form-control {padding: 10px;border: 1px solid #ccc;border-radius: 0;box-sizing: border-box;width: 100%;}
.w-100 { width: 100%; }
.btn {padding: 10px 20px;border-radius: 0;cursor: pointer;font-weight: normal;transition: background-color 0.2s, color 0.2s, border-color 0.2s;text-decoration: none;display: inline-block;text-align: center;border: 1px solid;font-size: 1em;}
.btn-positive { background:#4CAF50; color:white; }
.btn-warning { background:#f39c12; color:white; }
.btn-danger { background:#e74c3c; color:white; }
.btn-neutral { background-color: #6c757d; color: white; }
.table { border-collapse: collapse; width: 100%; margin-bottom: 20px; }
.table th, .table td { border: 1px solid #ccc; padding: 8px; text-align: center; }
.table-hover tr:hover { background: #fafafa; }
.gold { color: gold; }
.mt-20 { margin-top: 20px; }
</style>

<script>
$(function() {
    
    // ✅ 옵션 "수정" 버튼 클릭 (인라인 편집)
    $(document).on("click", ".btn-option-edit", function() {
    	var tr = $(this).closest("tr");

        if (tr.find("input").length > 0) return;

        var name = tr.find(".opt-name").text().trim();
    	var stock = tr.find(".opt-stock").text().trim();

        // --- ✨ 여기가 진짜 수정된 부분! (JSP EL 충돌 해결) ✨ ---
        tr.find(".opt-name").html('<input type="text" class="field edit-name" value="' + name + '">');
        tr.find(".opt-stock").html('<input type="number" class="field edit-stock" value="' + stock + '" min="0">');
        // --- ✨ 수정 끝 ✨ ---

        $(this).hide(); // "수정" 버튼 숨기기
        tr.find(".btn-option-update").show(); // "완료" 버튼 보이기
    });

    // ✅ 옵션 "수정 완료" 버튼 클릭 (이전과 동일)
    $(document).on("click", ".btn-option-update", function() {
    	var tr = $(this).closest("tr");
    	var optionNo = $(this).data("option-no");
    	var name = tr.find(".edit-name").val();
    	var stock = tr.find(".edit-stock").val();

        if (!name || stock === null || stock < 0) {
            alert("모든 필드를 올바르게 채워주세요 (재고 0 이상).");
            return;
        }

        $.ajax({
            url: "${pageContext.request.contextPath}/admin/product/option/edit", 
            type: "post",
            data: { 
                optionNo: optionNo,
                optionName: name,
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

    // ✅ 옵션 "삭제" 버튼 클릭 (이전과 동일)
    $(document).on("click", ".btn-option-delete", function() {
    	var optionNo = $(this).data("option-no");
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
});
</script>

<div class="container w-800">
    <h1>상품 상세정보 (관리자)</h1>

    <%-- ... (상품 기본 정보 표시는 동일) ... --%>
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
        <tr><th>평균 평점</th><td><fmt:formatNumber value="${avgRating}" pattern="0.0"/></td> <%-- avgRating 사용 --%>
    </table>

    <%-- ❌ 1. "옵션 등록" 폼 삭제 --%>
    <%-- (이전 폼 코드 ... 삭제 ...) --%>

    <%-- ✨ 2. "옵션 관리 페이지"로 이동하는 링크 추가 --%>
    <h3>옵션 관리</h3>
    <p>상품 옵션(SKU) 생성 및 일괄 관리는 전용 페이지에서 진행합니다.</p>
    <a href="${pageContext.request.contextPath}/admin/product/option/manage?productNo=${product.productNo}" 
       class="btn btn-neutral mt-10">
        옵션 조합(SKU) 관리 페이지로 이동
    </a>

    <!-- ✨ 3. "옵션 목록" 테이블 (SKU 기준으로 수정) -->
    <h3>현재 등록된 옵션(SKU) 목록</h3>
    <table>
        <thead>
            <tr>
                <th>조합 이름 (optionName)</th>
                <th>재고 (optionStock)</th>
                <th>관리</th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty optionList}">
                    <tr>
                        <td colspan="3" style="text-align:center;">등록된 옵션이 없습니다.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="opt" items="${optionList}">
                        <tr>
                            <%-- <td>${opt.optionNo}</td> --%> <%-- 옵션 번호는 숨겨도 됨 --%>
                            <td class="opt-name">${opt.optionName}</td> <%-- SKU 이름 --%>
                            <%-- ❌ ${opt.optionValue} 삭제 (오류 원인) --%>
                            <td class="opt-stock">${opt.optionStock}</td> <%-- SKU 재고 --%>
                            <td>
                                <button class="btn btn-warning btn-option-edit" data-option-no="${opt.optionNo}">수정</button>
                                <button class="btn btn-positive btn-option-update" data-option-no="${opt.optionNo}" style="display:none;">완료</button>
                                <button class="btn btn-danger btn-option-delete" data-option-no="${opt.optionNo}">삭제</button>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <!-- ... (리뷰 목록, 상품 수정/삭제 버튼 등은 동일) ... -->
    <h3>리뷰 목록</h3>
    <%-- ... (리뷰 테이블 코드) ... --%>
    
    <div style="margin-top: 20px;">
        <a href="list" class="btn btn-neutral">상품 목록</a>
        <a href="edit?productNo=${product.productNo}" class="btn btn-positive">수정하기</a>
        <%-- ... (삭제 폼) ... --%>
    </div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />


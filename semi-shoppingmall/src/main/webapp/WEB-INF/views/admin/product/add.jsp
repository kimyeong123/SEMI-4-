<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<div class="container w-600">
	<div class="cell center">
		<h1>상품 둥록</h1>
	</div>

	<form autocomplete="off" action="add" method="post"
		enctype="multipart/form-data">
		<!-- 상품 이름 -->
		<div class="cell">
			<label>상품 이름 *</label> <input type="text" name="productName"
				placeholder="(ex) KH 패션 로고 반팔티" required class="field w-100">
		</div>

		<!-- 상품 가격 -->
		<div class="cell">
			<label>상품 가격 *</label> <input type="number" name="productPrice"
				min="0" required class="field w-100">
		</div>

		<!-- 상품 내용 -->
		<div class="cell">
			<label>상품 내용 *</label>
			<textarea name="productContent" rows="5" placeholder="상품 설명을 입력하세요"
				required class="field w-100"></textarea>
		</div>

		<!-- 부모 카테고리 -->
		<div class="cell">
			<label>부모 카테고리 *</label>
			<!-- 선택 -->
			<select name="parentCategoryNo" class="field w-100">
				<option value="">기존 카테고리 선택</option>
				<c:forEach var="c" items="${parentCategoryList}">
					<option value="${c.categoryNo}"
						<c:if test="${selectedParentNo == c.categoryNo}">selected</c:if>>
						${c.categoryName}</option>
				</c:forEach>
			</select>
			<!-- 직접 입력 -->
			<input type="text" name="newParentCategory"
				placeholder="새 부모 카테고리 입력" class="field w-100 mt-10">
			<button type="button" onclick="alert('새 부모 카테고리 추가 기능은 나중에 구현')">추가</button>
		</div>

		<!-- 하위 카테고리 -->
		<div class="cell">
			<label>하위 카테고리 *</label>
			<!-- 선택 -->
			<select name="childCategoryNo" class="field w-100">
				<option value="">기존 카테고리 선택</option>
				<c:forEach var="c" items="${childCategoryList}">
					<option value="${c.categoryNo}">${c.categoryName}</option>
				</c:forEach>
			</select>
			<!-- 직접 입력 -->
			<input type="text" name="newChildCategory" placeholder="새 하위 카테고리 입력"
				class="field w-100 mt-10">
			<button type="button"
				onclick="
        <c:choose>
            <c:when test='${selectedParentNo != null}'>
                alert('새 하위 카테고리 추가 기능은 나중에 구현')
            </c:when>
            <c:otherwise>
                alert('먼저 부모 카테고리를 선택해야 합니다.')
            </c:otherwise>
        </c:choose>
    ">추가</button>
		</div>

		<!-- 썸네일 이미지 -->
		<div class="cell">
			<label>상품 이미지 *</label> <input type="file" name="thumbnailFile"
				accept="image/*" class="field w-100" required>
		</div>

		<!-- 버튼 -->
		<div class="cell mt-30">
			<button class="btn btn-positive w-100">상품 등록</button>
		</div>
	</form>

	<!-- 이미지 선택 오류 표시 -->
	<c:if test="${param.error != null}">
		<div class="cell mt-20">
			<h3 style="color: red">이미지는 반드시 설정해야 합니다</h3>
		</div>
	</c:if>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>

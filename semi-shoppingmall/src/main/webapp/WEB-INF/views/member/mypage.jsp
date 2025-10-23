<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>	

<style>
	.profile-wrapper {
		width:200px;
		height:200px;
		position: relative;
		border-radius: 50%;
		overflow: hidden;
	}
	.profile-wrapper  > img{
		width: 100%;
		height: 100%;
	}
	.profile-wrapper  > label{
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.3);
		color: white;
		display: none;
	}
	.profile-wrapper:hover > label {
		display: flex;
	}
</style>

<!-- 프로필 변경 기능 코드  -->
<script type="text/javascript">
	$(function(){
		//이미지의 최초 주소를 불러와 저장 (캐싱 우회)
	 	var origin = $(".image-profile").attr("src");
		$("#profile-input").on("input", function(){
			//선택된 파일을 구해서
			//var list = document.querySelector(".profile-input").files; //JS
			//var list = $(".profile-input")[0].files //jQuery
			var list = $("#profile-input").prop("files"); //jQuery
			console.log(list);
			if(list.length == 0) return;
			
			//비동기 통신으로 전송
			//ajax도 form처럼 아무말 안하면 urlencode로 보냄
			//파일은 multipart 방식이라 기본설정 제거가 필요
			//processData, contentType을 제거 후 FormData를 생성해 전달
			var form = new FormData(); //form역할
			form.append("attach", list[0]);
			
			$.ajax({
				processData : false, //전처리 제거
				contentType : false, //MIME 제거
				url:"/rest/member/profile",
				method:"post",
				data: form,
				success:function(response){
					console.log("완료");
					//origin에 시간 붙여서 src를 재설정
					//중요 브라우저의 캐싱 우회를 위해 시간을 파라미터로 첨부
					var newOrigin = origin + "&t" + new Date().getTime();
					$(".image-profile").attr("src", newOrigin);	
				}
			});
		});
		
		//삭제 버튼을 누르면 물어본 뒤 확인을 눌렀을 경우 삭제 진행
		$(".profile-delete-btn").on("click", function(){
			var choice = window.confirm("정말 삭제하시겠습니까?\n삭제 후 복구할 수 없습니다");
			if(choice == false) return;
			
			$.ajax({
				url:"/rest/member/delete",
				method:"post",
				success:function(){
					var newOrigin = origin + "&t=" + new Date().getTime();
					$(".image-profile").attr("src", newOrigin);
				}
			});
		});
	});
</script>

<div class="container w-600">
	<div class = "cell center">
		<h2>${memberDto.memberId}님의 정보</h2>
	</div>
	<div class = "cell">
		<table class="w-100 table table-border">
			<tr>
				<th>닉네임</th>
				<td>${memberDto.memberNickname}</td>
			</tr>
			<tr>
				<th>이미지</th>
				<td>
					<div class="profile-wrapper">
						<img class="image-profile"  src = "/member/profile?memberId=${memberDto.memberId }" >
						<label for="profile-input" class="flex-box flex-center">변경</label>
						<input type="file" id="profile-input" style="display:none">
						<!-- <button type ="button" class="profile-change-btn">변경</button> -->
					</div>
					<label class="profile-delete-btn red">
						<i class="fa-solid fa-xmark"></i>
						<span>삭제</span>
					</label>
					<!-- <input type = "file" id="profile-input" style="display:none"> -->
					<br><br>
				</td>
			</tr>
			<tr>
				<th>이메일</th>
				<td>${memberDto.memberEmail}</td>
			</tr>
			<tr>
				<th>생년월일</th>
				<td>${memberDto.memberBirth}</td>
			</tr>
			<tr>
				<th>연락처</th>
				<td>${memberDto.memberContact}</td>
			</tr>
			<tr>
				<th>등급</th>
				<td>${memberDto.memberLevel}</td>
			</tr>
			<tr>
				<th>포인트</th>
				<td>${memberDto.memberPoint}포인트</td>
			</tr>
			<tr>
				<th>주소</th>
				<td>
					<c:if test="${memberDto.memberPost != null}">
					[${memberDto.memberPost}] 
					${memberDto.memberAddress1} 
					${memberDto.memberAddress2}
					</c:if>
				</td>
			</tr>
			<tr>
				<th>가입일</th>
				<td>
					<fmt:formatDate value="${memberDto.memberJoin}" pattern="y년 M월 d일 H시 m분 s초"/>
				</td>
			</tr>
			<tr>
				<th>최종로그인</th>
				<td>
					<fmt:formatDate value="${memberDto.memberLogin }" pattern= "y년 M월 d일 H시 m분 s초"/>
				</td>
			</tr>
			<tr>
				<th>비밀번호 변경일</th>
				<td>
					<fmt:formatDate value="${memberDto.memberChange }" pattern= "y년 M월 d일 H시 m분 s초"/>
				</td>
			</tr>
		
			
		</table>
	</div>
	
<hr>

	<%-- =================================================== --%>
	<%-- 1. 나의 장바구니 목록  --%>
	<%-- =================================================== --%>
	<div class = "cell center w-100">
		<h2>나의 장바구니 목록</h2>
		<table class="w-100 table table-border">
			<thead>
				<tr>
					<th>상품명</th>
					<th>수량</th>
					<th>단가</th>
					<th>총 금액</th>
					<th>관리</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="cart" items="${cartList}">
				<tr>
					<td><a href="/product/detail?productNo=${cart.productNo}">${cart.productName}</a></td>
					<td>${cart.cartQty}개</td>
					<td>${cart.productPrice}원</td>
					<td>${cart.cartQty * cart.productPrice}원</td>
					<td>
						<a href="/cart/delete?productNo=${cart.productNo}">삭제</a>
					</td>
				</tr>
				</c:forEach>
                <c:if test="${empty cartList}">
				<tr>
					<td colspan="5" class="center">장바구니에 담긴 상품이 없습니다.</td>
				</tr>
				</c:if>
			</tbody>
		</table>
	</div>	
	
<hr>

	<%-- =================================================== --%>
	<%-- 2. 나의 찜 목록  --%>
	<%-- =================================================== --%>
	<div class = "cell center w-100">
		<h2>나의 찜 목록</h2>
		<table class="w-100 table table-border">
			<thead>
				<tr>
					<th>상품명</th>
					<th>가격</th>
					<th>관리</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="wish" items="${wishlistList}">
				<tr>
					<td><a href="/product/detail?productNo=${wish.productNo}">${wish.productName}</a></td>
					<td>${wish.productPrice}원</td>
					<td>
						<a href="/wishlist/delete?productNo=${wish.productNo}">삭제</a>
					</td>
				</tr>
				</c:forEach>
                <c:if test="${empty wishlistList}">
				<tr>
					<td colspan="3" class="center">찜한 상품이 없습니다.</td>
				</tr>
				</c:if>
			</tbody>
		</table>
	</div>
	
<hr>

	<%-- =================================================== --%>
	<%-- 3. 나의 상품 주문 내역 --%>
	<%-- =================================================== --%>
	<div class = "cell center w-100">
		<h2>나의 상품 주문 내역</h2>
		<table class="w-100 table table-border">
			<thead>
				<tr>
					<th>주문번호</th>
					<th>대표 상품</th>
					<th>총 금액</th>
					<th>주문일시</th>
					<th>진행상태</th>
					<th>상세</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="order" items="${ordersList}">
				<tr>
					<td>${order.ordersNo}</td>
					<td>
						<a href="/order/detail?ordersNo=${order.ordersNo}">
							${order.productName} 
						</a>
					</td>
					<td>${order.ordersTotalPrice}원</td>
					<td>
						<fmt:formatDate value="${order.ordersDate}" pattern="yyyy-MM-dd HH:mm"/>
					</td>
					<td>${order.ordersStatus}</td>
					<td>
						<a href="/order/detail?ordersNo=${order.ordersNo}" class="btn btn-primary btn-sm">상세보기</a>
					</td>
				</tr>
				</c:forEach>
                <c:if test="${empty ordersList}">
				<tr>
					<td colspan="6" class="center">주문 내역이 없습니다.</td>
				</tr>
				</c:if>
			</tbody>
		</table>
	</div>	



	
	<%-- =================================================== --%>
	<%-- 4. 나의 리뷰 목록  --%>
	<%-- =================================================== --%>
	<hr>
		
	<div class="cell center">
		<h2>나의 상품 리뷰 내역</h2>
	</div>
	<div class="cell">
		<table class="w-100 table table-border">
			<thead>
				<tr>
					<th>상품명</th>
					<th>평점</th>
					<th>내용</th>
					<th>작성일</th>
					<th>관리</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="review" items="${reviewList }">
					<tr>
						<td><a href = "/product/detail?productNo=${review.productNo }">${review.productName }</a></td>
						<td>
							<c:forEach begin ="1"  end = "${review.reviewRating }">
								<i class = "fa-solid fa-star gold"></i>
							</c:forEach>
							<c:forEach begin = "${review.reviewRating + 1 }" end="5">
								<i class="fa-regular fa-star"></i>
							</c:forEach>
						</td>
						<td>${review.reviewContent}</td>
						<td><fmt:formatDate value="${review.reviewTime}" pattern="yyyy-MM-dd"/></td>
						<td>
							<%-- 리뷰 수정/삭제 링크 (필요 시) --%>
							<a href="/review/edit?reviewNo=${review.reviewNo}">수정</a> | 
							<a href="/review/delete?reviewNo=${review.reviewNo}">삭제</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
		

	
	<h2><a href = "edit">내 정보 변경</a></h2>
	<h2><a href = "password">비밀번호 변경하기</a></h2>
	<h2><a href = "drop">회원 탈퇴하기</a></h2>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
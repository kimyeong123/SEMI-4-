<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>	

<!-- 다음주소 cdn -->
<script src="${pageContext.request.contextPath}//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src = "/js/member-edit.js"></script>

<div class = "container w-600">
	<div class ="cell center">
		<h1>회원정보 수정</h1>
	</div>
	
	<!-- 수정본 -->

	 <div>
		<a class = "btn btn-netural" href = "/member/mypage">마이페이지로 다시가기</a>
	 </div>
	
	<form action="edit" method="post" enctype="multipart/form-data" autocomplete="off" class="check-form">
        
        <div class="cell">
            <label>
                **현재 비밀번호 입력 (본인 인증)** <i class="fa-solid fa-asterisk red"></i>
                <i class="fa-solid fa-eye-slash" id="password-show"></i>
            </label>
            <input type="password" name="memberPw" class="field w-100" required>
            <div class="fail-feedback">본인 인증을 위해 현재 비밀번호를 입력하세요</div>
        </div>
        
        <div class="cell">
            <label>아이디</label>
            <input type="text" name="memberId" class="field w-100" 
                   value="${memberDto.memberId}" readonly>
        </div>
        
        <div class="cell">
            <label>닉네임 <i class="fa-solid fa-asterisk red"></i></label>
            <input type="text" name="memberNickname" class="field w-100" 
                   value="${memberDto.memberNickname}" 
                   data-initial-value="${memberDto.memberNickname}">
            <div class="success-feedback">멋진 닉네임입니다!</div>
            <div class="fail-feedback">한글 또는 숫자 2~10글자로 작성하세요</div>
            <div class="fail2-feedback">닉네임이 이미 사용중입니다</div>
        </div>

        <div class="cell">
            <label>이메일 <i class="fa-solid fa-asterisk red"></i></label>
            <div class="flex-box" style="flex-wrap: wrap;">
                <input type="text" inputmode="email" name="memberEmail" class="field w-50 flex-fill" 
                       value="${memberDto.memberEmail}" 
                       data-initial-value="${memberDto.memberEmail}" readonly >
                <button type="button" class="btn btn-neutral ms-20 btn-cert-send">
                    <i class="fa-solid fa-rotate-right"></i> <span>인증번호 재발송</span>
                </button>
                <div class="success-feedback w-100">이메일 인증이 완료되었습니다</div>
                <div class="fail-feedback w-100">올바른 이메일 형식이 아닙니다</div>
            </div>
        </div>
        <div class="cell flex-box cell-cert-input" style="display: none; flex-wrap: wrap;">
            <input type="text" inputmode="numeric" class="field cert-input" placeholder="인증번호 입력">
            <button type="button" class="btn btn-positive ms-20 btn-cert-check">
                <i class="fa-solid fa-key"></i>
                <span>인증번호 확인</span>
            </button>
            <div class="fail-feedback w-100">인증번호는 숫자 6자리입니다</div>
            <div class="fail2-feedback w-100">인증번호가 일치하지 않습니다</div>
        </div>

        <div class="cell">
            <label>연락처</label>
            <input type="text" inputmode="tel" name="memberContact" class="field w-100"
                   value="${memberDto.memberContact}">
            <div class="fail-feedback">010으로 시작하는 11자리 휴대전화번호를 입력하세요 (- 사용 불가)</div>
        </div>

        <div class="cell">
            <label>생년월일</label>
            <input type="date" name="memberBirth" class="field w-100"
                   value="${memberDto.memberBirth}">
            <div class="fail-feedback">올바른 날짜 형식이 아닙니다</div>
            <div class="fail2-feedback">미래는 설정할 수 없습니다</div>
        </div>
        
        <div class="cell">
            <label>주소</label> <br>
            <input type="text" name="memberPost" placeholder="우편번호" size="6" class="field" inputmode="numeric"
                   value="${memberDto.memberPost}" readonly>
            <button type="button" class="btn btn-neutral btn-address-search">
                <i class="fa-solid fa-magnifying-glass"></i>
            </button>
            <button type="button" class="btn btn-negative btn-address-clear" style="display: none;">
                <i class="fa-solid fa-xmark"></i>
            </button>
        </div>
        <div class="cell">
            <input type="text" name="memberAddress1" placeholder="기본주소" class="field w-100"
                   value="${memberDto.memberAddress1}" readonly>
        </div>
        <div class="cell">
            <input type="text" name="memberAddress2" placeholder="상세주소" class="field w-100"
                   value="${memberDto.memberAddress2}">
            <div class="fail-feedback">주소는 모두 작성해야 합니다</div>
        </div>

<!--         <div class="cell"> -->
<!--             <label>프로필 이미지</label> -->
<!--             <input type="file" name="attach" class="field w-100"> -->
<!--         </div> -->
<!--         <div class="cell"> -->
<!--             <img src="${pageContext.request.contextPath}/images/error/no-image.png" width="200px" class="img-preview"> -->
<!--         </div> -->
<!-- 		<div class="cell"> -->
<%-- 			<img class="image-profile" src="${pageContext.request.contextPath}/member/profile?memberId=${memberDto.memberId}&t=<%= System.currentTimeMillis() %>"> --%>
<!-- 		</div> -->

        <div class="cell mt-40">
            <button type="submit" class="btn btn-negative w-100">
                <i class="fa-solid fa-floppy-disk"></i>
                <span>정보 수정</span>
            </button>
        </div>

    </form>
	
	

</div>


<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>	
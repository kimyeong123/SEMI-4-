<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>

<link rel = "stylesheet" type = "text/css" href="/multipage/multipage.css">
<!-- 다음주소 cdn -->
<script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
<script src = "/multipage/multipage.js"></script>
<script src = "/js/member-join.js"></script>

<div class="flex-fill"></div>
    <form action="join" method="post" enctype="multipart/form-data" autocomplete="off" class="check-form">
        <div class="container w-600">
            
            <div class="cell center">
                <h1>가입 정보 입력</h1>
            </div>

            <div class="cell">
                <div class="progressbar">
                    <div class="guage target"></div>
                </div>
            </div>
            
            <!-- 1페이지 아이디+중복 -->
            <div class="page">
                <div class="flex-box">
                    <div class="w-100 right">
                        <button type="button" class="btn btn-neutral btn-next">
                            <i class="fa-solid fa-arrow-right"></i>
                        </button>                        
                    </div>
                </div>

                <div class="cell center">
                    <h2>1단계: 아이디 입력</h2>
                </div>

                <!-- 아이디 -->
                <div class="cell">
                    <label>아이디 <i class="fa-solid fa-asterisk red"></i></label>
                    <input type="text" name="memberId" class="field w-100">
                    <div class="success-feedback">멋진 아이디입니다!</div>
                    <div class="fail-feedback">아이디는 알파벳 소문자로 시작하며 숫자를 포함해 8~20자로 작성하세요</div>
                    <div class="fail2-feedback">아이디가 이미 사용중입니다</div>
                </div>
            </div>
            
            <!-- 비번+비번확인 -->
            <div class="page">
                <div class="flex-box">
                    <div class="w-100 left">
                        <button class="btn btn-neutral btn-prev">
                            <i class="fa-solid fa-arrow-left"></i>
                        </button>
                    </div>
                    <div class="w-100 right">
                        <button type="button" class="btn btn-neutral btn-next">
                            <i class="fa-solid fa-arrow-right"></i>
                        </button>                        
                    </div>
                </div>             
                
                <div class="cell center">
                    <h2>2단계: 비밀번호 입력</h2>
                </div>                
                
                <!-- 비밀번호-->
                <div class="cell">
                    <label>
                        비밀번호
                        <i class="fa-solid fa-asterisk red"></i>
                        <i class="fa-solid fa-eye-slash" id="password-show"></i>
                    </label>
                    <input type="password" name="memberPw" class="field w-100">
                    <div class="success-feedback">비밀번호가 올바른 형식입니다</div>
                    <div class="fail-feedback">알파벳 대/소문자, 숫자, 특수문자를 반드시 포함하여 8 ~16자로 작성하세요</div>
                </div>
                
                <div class="cell">
                    <label>비밀번호 확인 <i class="fa-solid fa-asterisk red"></i></label>
                    <input type="password" id="password-check" class="field w-100">
                    <div class="success-feedback">비밀번호가 일치합니다</div>
                    <div class="fail-feedback">비밀번호가 일치하지 않습니다</div>
                </div>
            </div>
            
            <!-- 닉네임+중복검사 -->
            <div class="page">
                <div class="flex-box">
                    <div class="w-100 left">
                        <button class="btn btn-neutral btn-prev">
                            <i class="fa-solid fa-arrow-left"></i>
                        </button>
                    </div>
                    <div class="w-100 right">
                        <button type="button" class="btn btn-neutral btn-next">
                            <i class="fa-solid fa-arrow-right"></i>
                        </button>                        
                    </div>
                </div>

                <div class="cell center">
                    <h2>3단계: 닉네임 입력</h2>
                </div>

                <!-- 닉네임 -->
                <div class="cell">
                    <label>닉네임 <i class="fa-solid fa-asterisk red"></i></label>
                    <input type="text" name="memberNickname" class="field w-100">
                    <div class="success-feedback">멋진 닉네임입니다!</div>
                    <div class="fail-feedback">한글 또는 숫자 2~10글자로 작성하세요</div>
                    <div class="fail2-feedback">닉네임이 이미 사용중입니다</div>
                </div>
            </div>

            <!-- 이메일+이메일 인증 -->
            <div class="page">                
                <div class="flex-box">
                    <div class="w-100 left">
                        <button class="btn btn-neutral btn-prev">
                            <i class="fa-solid fa-arrow-left"></i>
                        </button>
                    </div>
                    <div class="w-100 right">
                        <button type="button" class="btn btn-neutral btn-next">
                            <i class="fa-solid fa-arrow-right"></i>
                        </button>                        
                    </div>
                </div>

                <div class="cell center">
                    <h2>4단계: 이메일 입력</h2>
                </div>

                <!-- 이메일 -->
                <div class="cell">
                    <label>이메일 <i class="fa-solid fa-asterisk red"></i></label>
                    <div class="flex-box" style="flex-wrap: wrap;">
                        <input type="text" inputmode="email" name="memberEmail" class="field w-50 flex-fill">
                        <button type="button" class="btn btn-neutral ms-20 btn-cert-send">
                            <i class="fa-solid fa-paper-plane"></i>
                            <span>인증번호 보내기</span>
                        </button>
                        <div class="success-feedback w-100">(이메일 인증이 완료되었습니다) 올바른 이메일 형식입니다</div>
                        <div class="fail-feedback w-100">올바른 이메일 형식이 아닙니다</div>
                        <div class="fail2-feedback w-100">이메일 인증이 완료되지 않았습니다</div>
                    </div>
                </div>
    
<!--                 인증번호입력창 -->
                <div class="cell flex-box cell-cert-input" style="display: none; flex-wrap: wrap;">
                    <input type="text" inputmode="numeric" class="field cert-input" placeholder="인증번호 입력">
                    <button type="button" class="btn btn-positive ms-20 btn-cert-check">
                        <i class="fa-solid fa-key"></i>
                        <span>인증번호 확인</span>
                    </button>
                    <div class="fail-feedback w-100">인증번호는 숫자 6자리입니다</div>
                    <div class="fail2-feedback w-100">인증번호가 일치하지 않습니다</div>
                </div>
            </div>
            
            <!-- 연락처+생년월일 -->
            <div class="page">
                <div class="flex-box">
                    <div class="w-100 left">
                        <button class="btn btn-neutral btn-prev">
                            <i class="fa-solid fa-arrow-left"></i>
                        </button>
                    </div>
                    <div class="w-100 right">
                        <button type="button" class="btn btn-neutral btn-next">
                            <i class="fa-solid fa-arrow-right"></i>
                        </button>                        
                    </div>
                </div>

                <div class="cell center">
                    <h2>5단계: 연락처 생년월일 입력</h2>
                </div>

                <!-- 연락처 -->
                <div class="cell">
                    <label>연락처</label>
                    <input type="text" inputmode="tel" name="memberContact" class="field w-100">
                    <div class="fail-feedback">010으로 시작하는 11자리 휴대전화번호를 입력하세요 (- 사용 불가)</div>
                </div>

                <!-- 생년월일 -->
                <div class="cell">
                    <label>생년월일</label>
                    <input type="date" name="memberBirth" class="field w-100">
                    <div class="fail-feedback">올바른 날짜 형식이 아닙니다</div>
                    <div class="fail2-feedback">미래는 설정할 수 없습니다</div>
                </div>
            </div>

            <!-- 주소 -->
            <div class="page">                
                <div class="flex-box">
                    <div class="w-100 left">
                        <button class="btn btn-neutral btn-prev">
                            <i class="fa-solid fa-arrow-left"></i>
                        </button>
                    </div>
                    <div class="w-100 right">
                        <button type="button" class="btn btn-neutral btn-next">
                            <i class="fa-solid fa-arrow-right"></i>
                        </button>                        
                    </div>
                </div>

                <div class="cell center">
                    <h2>6단계: 주소 입력</h2>
                </div>

                <!-- 주소 -->
                <div class="cell">
                    주소
                </div>
                <div class="cell">
                    <input type="text" name="memberPost" placeholder="우편번호" size="6" class="field" inputmode="numeric"
                        readonly>
                    <button type="button" class="btn btn-neutral btn-address-search">
                        <i class="fa-solid fa-magnifying-glass"></i>
                    </button>
                    <button type="button" class="btn btn-negative btn-address-clear" style="display: none;">
                        <i class="fa-solid fa-xmark"></i>
                    </button>
                </div>
                <div class="cell">
                    <input type="text" name="memberAddress1" placeholder="기본주소" class="field w-100" readonly>
                </div>
                <div class="cell">
                    <input type="text" name="memberAddress2" placeholder="상세주소" class="field w-100">
                    <div class="fail-feedback">주소는 모두 작성해야 합니다</div>
                </div>
            </div>
            <!-- 프로필 이미지 -->
            <div class="page">
                <div class="flex-box">
                    <div class="w-100 left">
                        <button type="button" class="btn btn-neutral btn-prev">
                            <i class="fa-solid fa-arrow-left"></i>
                        </button>
                    </div>
                </div>

                <div class="cell center">
                    <h2>7단계: 프로필 이미지 설정</h2>
                </div>

                <!-- 프로필 선택 -->
                <div class="cell">
                    <label>프로필 이미지</label>
                    <input type="file" name="attach" class="field w-100">
                </div>
                <!-- 미리보기 -->
                <div class="cell">
                    <img src="/images/error/no-image.png" width="200px" class = "img-preview">
                </div>
    
                <!-- 가입버튼 -->
                <div class="cell mt-40">
                    <button type="submit" class="btn btn-positive w-100">
                        <i class="fa-solid right-to-bracket"></i>
                        <span>회원 가입</span>
                    </button>
                </div>
            </div>

        </div>
    </form>
<div class="flex-fill"></div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>	
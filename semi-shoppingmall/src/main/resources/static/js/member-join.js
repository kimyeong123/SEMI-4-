//회원 가입 입력화면에 대한 처리
$(function(){
    //상태 객체
    var state = {
        memberIdValid : false,
        memberPwValid : false,
        memberPwCheckValid : false,
        memberNicknameValid : false,
        memberEmailValid : false,
        memberContactValid : true,
        memberBirthValid : true,
        memberAddressValid : true,
        ok: function(){
            return this.memberIdValid && this.memberPwValid && this.memberPwCheckValid
                        && this.memberNicknameValid && this.memberEmailValid;
        }
    };
    
    //아이디 관련
    $("[name=memberId]").on("blur", function(){
        //[1] 형식 검사를 먼저 수행
        var regex = /^[a-z][a-z0-9]{4,19}$/;
        var valid = regex.test($(this).val());
        if(valid == false) {//형식이 올바르지 않다면
            $(this).removeClass("success fail fail2").addClass("fail");
            state.memberIdValid = false;
            return;
        }
        
        //[2] 형식 검사를 통과했다면...
        //var memberId = $(this).val();
        var memberId = $("[name=memberId]").val();
        $.ajax({
            url: "/rest/member/checkMemberId?memberId="+memberId,
            success: function(response) {
                //서버의 검사 결과에 따른 처리
                //- 서버가 아이디가 사용중이라고 하면 fail2를 추가
                //- 서버가 아이디가 사용중이 아니라고 하면 success를 추가
                //console.log(response);
                if(response == true) {//아이디가 서버에 이미 존재하는 경우
                    $("[name=memberId]").removeClass("success fail fail2").addClass("fail2");
                    state.memberIdValid = false;
                }
                else {//아이디가 서버에 존재하지 않는 경우
                    $("[name=memberId]").removeClass("success fail fail2").addClass("success");
                    state.memberIdValid = true;
                }
            }
        });
        
    });

    //비밀번호 관련
    $("[name=memberPw] , #password-check").on("blur", function(){
        //this를 특정할 수 없음(둘 중 하나)
        //[1] 모든 클래스제거
        $("[name=memberPw] , #password-check").removeClass("success fail");

        //[2] 비밀번호 형식검사
        var regex = /^(?=.*?[A-Z]+)(?=.*?[a-z]+)(?=.*?[0-9]+)(?=.*?[!@#$]+)[A-Za-z0-9!@#$]{8,16}$/;
        var valid = regex.test($("[name=memberPw]").val());
        $("[name=memberPw]").addClass(valid ? "success" : "fail");
        state.memberPwValid = valid;
        //[3] 비밀번호 일치 검사 (비밀번호가 입력된 경우에만 수행)
        if($("[name=memberPw]").val().length > 0) {
            var valid2 = $("[name=memberPw]").val() == $("#password-check").val();
            $("#password-check").addClass(valid2 ? "success" : "fail");
            state.memberPwCheckValid = valid2;
        }
    });
    $("#password-show").on("click", function(){
        if($(this).hasClass("fa-eye-slash")) {//type=password 상태라면
            $(this).removeClass("fa-eye-slash").addClass("fa-eye");
            $("[name=memberPw], #password-check").prop("type", "text");
        }
        else {//type=text 상태라면
            $(this).removeClass("fa-eye").addClass("fa-eye-slash");
            $("[name=memberPw], #password-check").prop("type", "password");
        }
    });

    //닉네임 관련
    $("[name=memberNickname]").on("blur", function(){
        var regex = /^[가-힣0-9]{2,10}$/;
        var valid = regex.test($(this).val());
        if(valid == false) {//형식 검사가 틀린 경우(fail)
            $(this).removeClass("success fail fail2").addClass("fail");
            state.memberNicknameValid = false;
            return;
        }

        var memberNickname = $(this).val();
        $.ajax({
            url:"/rest/member/checkMemberNickname?memberNickname="+memberNickname,
            method:"get",
            success:function(response){
                //this가 입력창이 아님
                if(response) {//사용중이라면(존재한다면)
                    $("[name=memberNickname]").removeClass("success fail fail2").addClass("fail2");
                    state.memberNicknameValid = false;
                }
                else {//사용중이 아니라면(존재하지 않는다면)
                    $("[name=memberNickname]").removeClass("success fail fail2").addClass("success");
                    state.memberNicknameValid = true;
                }
            }
        });
    });

    //이메일 관련  //인증 관련내용 때문에 재 작성
    //인증번호 보내기 or 인증번호 재발송
//    $(".btn-cert-send").on("click", function(){
//        //재발송인 경우 => 아이콘이 fa-rotate-right인 경우
//        if($(this).find("i").hasClass("fa-rotate-right")) {
//            $("[name=memberEmail]").removeClass("success fail fail2")
//                                                    .val("").prop("readonly", false);
//            $(this).find("i").removeClass("fa-rotate-right").addClass("fa-paper-plane");
//            $(this).find("span").text("인증번호 보내기");
//            state.memberEmailValid = false;
//            return;
//        }
//
//        //보내기인 경우
//        $("[name=memberEmail]").removeClass("success fail fail2");
//        //[1] 이메일 형식 검사
//        var regex = /^(.*?)@(.*?)$/;
//        var email = $("[name=memberEmail]").val();
//        var valid = regex.test(email);
//        if(valid == false) {
//            $("[name=memberEmail]").removeClass("success fail fail2").addClass("fail");
//            state.memberEmailValid = false;
//            return;
//        }
//
//        //[2] 인증 이메일 발송 요청
//        $.ajax({
//            url:"/rest/member/certSend",
//            method:"post",
//            data: { certEmail : email },
//            success: function(response) {
//                $(".cell-cert-input").show();
//            },
//            //통신이 진행 중일 때 버튼의 상태를 변화시키기 위한 장치
//            beforeSend:function(){
//                $(".btn-cert-send").prop("disabled", true);
//                $(".btn-cert-send").find("i").removeClass("fa-paper-plane").addClass("fa-spinner fa-spin");
//                $(".btn-cert-send").find("span").text("인증메일 발송중");
//            },
//            complete:function(){
//                $(".btn-cert-send").prop("disabled", false);
//                $(".btn-cert-send").find("i").removeClass("fa-spinner fa-spin").addClass("fa-paper-plane");
//                $(".btn-cert-send").find("span").text("인증메일 보내기");
//            }
//        });
//    });
//
//    //.btn-cert-check를 누르면 이메일과 인증번호를 보내서 확인 요청
//    $(".btn-cert-check").on("click", function(){
//        var certNumber = $(".cert-input").val();
//        var regex = /^[0-9]{6}$/;
//        var valid = regex.test(certNumber);
//        if(valid == false) {
//            $(".cert-input").removeClass("success fail fail2").addClass("fail");
//            return;
//        }
//
//        var certEmail = $("[name=memberEmail]").val();
//        $.ajax({
//            url:"/rest/member/certCheck",
//            method:"post",
//            data: {certEmail : certEmail , certNumber : certNumber},
//            success: function(response) {
//                if(response) {//true가 온 경우 (인증 완료)
//                    $(".cert-input").removeClass("success fail fail2").val("");//입력창 초기화
//                    $(".cell-cert-input").hide();//영역 자체를 숨김 처리
//                    //이메일 입력창을 성공 표시하고 잠금 처리
//                    $("[name=memberEmail]").removeClass("success fail fail2").addClass("success");
//                    $("[name=memberEmail]").prop("readonly", true);
//                    //버튼의 글자와 아이콘을 변경
//                    $(".btn-cert-send").find("i").removeClass("fa-paper-plane").addClass("fa-rotate-right");
//                    $(".btn-cert-send").find("span").text("인증번호 재발송");
//                    //최종 성공한 상태를 저장
//                    state.memberEmailValid = true;
//                }
//                else {//false가 온 경우 (인증 실패)
//                    $(".cert-input").removeClass("success fail fail2").addClass("fail2");
//                    state.memberEmailValid = false;
//                }
//            }
//        });
//    });
	
	//인증 내용없는 이메일 형식검사
	$("[name=memberEmail]").on("blur", function(){
	    $("[name=memberEmail]").removeClass("success fail fail2");

	    var email = $(this).val();

	    // [1] 빈 값 검사 (필수 항목)
	    if (email.length === 0) {
	        $(this).addClass("fail");
	        $(this).nextAll(".fail-feedback").first().text("이메일은 필수 입력 항목입니다");
	        state.memberEmailValid = false;
	        return;
	    }

	    // [2] 형식 검사
	    var regex = /^(.*?)@(.*?)$/;
	    var valid = regex.test(email);
	    
	    if (valid === false) {
	        $(this).addClass("fail");
	        $(this).nextAll(".fail-feedback").first().text("올바른 이메일 형식이 아닙니다");
	        state.memberEmailValid = false;
	    } else {
	        $(this).addClass("success");
	        state.memberEmailValid = true;
	    }
	    
	    // 이메일 입력 필드 아래 피드백 문구 초기화
	    $(this).nextAll(".fail-feedback").first().text("올바른 이메일 형식이 아닙니다");
	});


    //연락처 관련
    $("[name=memberContact]").on("blur", function(){
        var regex = /^010[1-9][0-9]{3}[0-9]{4}$/;
        //var regex = /^010-[1-9][0-9]{3}-[0-9]{4}$/;
        var valid = $(this).val().length == 0 || regex.test($(this).val());
        $(this).removeClass("success fail").addClass(valid ? "success" : "fail");
        state.memberContactValid = valid;
    });
    $("[name=memberContact]").on("input", function(){
        //[1] 숫자만 입력가능하게
        var replacement = $(this).val().replace(/[^0-9]/g, "");//숫자가 아닌 것을 제거
        replacement = replacement.substring(0, 11);//11글자가 넘는 부분을 제거
        $(this).val(replacement);
        
        //[2] 대시가 자동포함되도록
        // if(replacement.length <= 7) {//글자수가 7글자 이하라면(대시 1개)
        //     var regex = /^([0-9]{3})([0-9]{1,4})$/;
        //     replacement = replacement.replace(regex, "$1-$2");
        //     $(this).val(replacement);
        // }
        // else {//8글자 이상이라면(대시 2개)
        //     var regex = /^([0-9]{3})([0-9]{4})([0-9]{1,4})$/;
        //     replacement = replacement.replace(regex, "$1-$2-$3");
        //     $(this).val(replacement);
        // }
    });
    
    //생년월일 관련
    $("[name=memberBirth]").on("blur", function(){
        var regex = /^(19[0-9]{2}|20[0-9]{2})-((02-(0[1-9]|1[0-9]|2[0-9]))|((0[469]|11)-(0[1-9]|1[0-9]|2[0-9]|30))|((0[13578]|1[02])-(0[1-9]|1[0-9]|2[0-9]|3[01])))$/;
        var valid = regex.test($(this).val());
        if(valid == false) {//날짜 형식에 맞지 않을 때
            $(this).removeClass("success fail fail2").addClass("fail");
            state.memberBirthValid = false;
        }
        else {//날짜 형식에 맞을 때 (momentjs를 이용해서 미래의 날짜인지를 검사)
            //moment(항목).명령()
            
            //var valid2 = moment($(this).val()).isBefore();//이전 isBefore() , 이후 isAfter()

            var current = moment();//LocalDate current = LocalDate.now();
            var inputDate = moment($(this).val());//LocalDate inputDate = LocalDate.parse(...);
            var valid2 = current.isAfter(inputDate);//또는 inputDate.isBefore(current);

            $(this).removeClass("success fail fail2").addClass(valid2 ? "success" : "fail2");
            state.memberBirthValid = valid2;
        }
    });

    //주소 관련
    //- 검색버튼, 우편번호입력창, 기본주소입력창을 클릭하면 다음 우편 API가 실행되어야 한다
    //- 상세주소입력창에 blur 이벤트가 발생하면 형식검사(모두 있거나 모두 없어야 통과)
    //- 내용이 작성되어 있는 경우에는 x버튼이 나와야 한다
    //- 우편 API를 실행하는 내용은 findAddress 라는 함수로 따로 분리하여 구현한다
    //- Daum 우편 API CDN이 필요
    function findAddress() {
        //daum에서 제공하는 샘플 코드
        new daum.Postcode({
            oncomplete: function (data) {
                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                // 각 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                var addr = ''; // 주소 변수

                //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    addr = data.roadAddress;
                } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    addr = data.jibunAddress;
                }
                $("[name=memberPost]").val(data.zonecode);
                $("[name=memberAddress1]").val(addr);
                $("[name=memberAddress2]").trigger("focus");

                //삭제버튼 표시
                $(".btn-address-clear").show();
            }
        }).open();
    }
    $(".btn-address-search, [name=memberPost], [name=memberAddress1]").on("click", findAddress);
    $(".btn-address-clear").on("click", function(){
        //모든 입력창의 입력값을 지우고 삭제버튼 숨김 처리
        $("[name=memberPost], [name=memberAddress1], [name=memberAddress2]").val("");
        $(this).hide();
    });
    $("[name=memberAddress2]").on("blur", function(){
        var fill = $("[name=memberPost]").val().length > 0
                        && $("[name=memberAddress1]").val().length > 0
                        && $("[name=memberAddress2]").val().length > 0;
        var empty = $("[name=memberPost]").val().length == 0
                        && $("[name=memberAddress1]").val().length == 0
                        && $("[name=memberAddress2]").val().length == 0;
        var valid = fill || empty;
        $("[name=memberPost], [name=memberAddress1], [name=memberAddress2]")
            .removeClass("success fail").addClass(valid ? "success" : "fail");

        state.memberAddressValid = valid;
    });

    //프로필 이미지 관련
    //- 미리보기를 처리하는 방법은 2가지가 존재
    //   1. 실제로 읽어와서 파일의 이름이 아닌 데이터를 src에 넣는 방법 (FileReader 사용)
    //      - 장점 : 메모리 관리를 자동으로 해줌 / 단점 : 느림, 코드가 복잡함
    //   2. ObjectURL 사용 (브라우저가 내부적으로 제공해주는 주소를 이용)
    //      - 장점 : 매우빠름(참조만 하기 때문) / 단점 : 메모리를 수동으로 제거해야함
    //      - 주소 생성 : URL.createObjectURL(파일)
    //      - 주소 해제 : URL.revokeObjectURL(생성된주소)
    $("[name=attach]").on("input", function(){
        //this == 파일 선택창
        
        //(+추가) 만약 img 태그의 src가 blob: 로 시작한다면 해당 주소를 제거하도록 요청
        var originUrl = $(".img-preview").prop("src");
        //if(originUrl이 blob: 으로 시작한다면) {
        //if(originUrl.substring(0, 5) == "blob:") {
        //if(originUrl.indexOf("blob:") == 0) {
        if(originUrl.startsWith("blob:")) {
            URL.revokeObjectURL(originUrl);
            console.log("Revoke URL 실행!");
        }
        
        if(this.files.length == 0) {//파일 선택을 취소한 것
            $(".img-preview").prop("src", "./images/no-image.png");
        }
        else {//파일을 선택한 것
            var imageUrl = URL.createObjectURL(this.files[0]);
            $(".img-preview").prop("src", imageUrl);
        }
    });

	//엔터 키로 폼 제출 방지 로직 (10-23추가됨)
	$(".check-form input").on("keydown", function(e) {
		if(e.key === "Enter") {
			e.preventDefault();
		}
	});
	
    //폼 검사
    $(".check-form").on("submit", function(){
        //$(this).find("[name]").trigger("blur");
        //return state.ok();
		
		if(state.ok() == false) {
			window.alert("필수 항목을 작성해야 합니다");
			return false;
		}
		return true;
    });
});
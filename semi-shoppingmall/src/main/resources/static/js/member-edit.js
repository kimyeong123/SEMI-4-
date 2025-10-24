//회원 정보 수정 입력화면에 대한 처리
$(function(){
    // 상태 객체: memberPwValid는 본인 인증 후 서버에서 처리하므로 여기서 최종 유효성 검사에선 제외
    var state = {
        // 아이디는 수정 페이지에서 수정 불가능하다고 가정하고 항상 true
        memberIdValid : true, 
        // 비밀번호는 본인 인증용으로만 사용되며, 클라이언트 유효성 검사에선 제외 (혹은 본인 인증 통과 시 서버에서 세션 처리)
        memberPwValid : true, 
        memberNicknameValid : true, // 초기값을 가져오므로 기본 true
        memberEmailValid : true,    // 초기값을 가져오므로 기본 true
        memberContactValid : true,
        memberBirthValid : true,
        memberAddressValid : true,
        ok: function(){
            // memberIdValid, memberNicknameValid, memberEmailValid만 필수 유효성 검사에 포함
            return this.memberIdValid && this.memberNicknameValid && this.memberEmailValid;
        }
    };
    
    // ------------------------------------------
    // 초기값 저장 (JSP에서 data-initial-value에 설정했다고 가정)
    // 폼 로드 시 모든 필드의 초기값을 data 속성에 저장합니다.
    $("input[type=text], input[type=email], input[type=date], input[type=tel]").each(function() {
        var name = $(this).attr('name');
        // JSP에서 value로 설정된 초기값을 data-initial-value에 저장 (필요한 경우)
        // ex: <input name="memberNickname" value="${memberDto.memberNickname}" data-initial-value="${memberDto.memberNickname}">
        if (!$(this).data('initial-value')) {
             $(this).data('initial-value', $(this).val());
        }
    });
    // ------------------------------------------

    // 아이디 관련: 수정 불가이므로 유효성 검사 로직을 제거하거나 항상 true로 설정
    // 현재 state.memberIdValid = true; 로 설정되어 있으므로 별도 로직은 필요 없음

    // 비밀번호 관련: 본인 인증용으로만 사용
    // 여기서는 형식 검사만 유지하고, 실제 인증은 폼 제출 후 서버에서 처리함
    $("[name=memberPw]").on("blur", function(){
        $(this).removeClass("success fail");
        
        var pwValue = $(this).val();
        
        // 비밀번호 필드는 본인 인증을 위해 반드시 입력해야 한다고 가정 (required 속성 필요)
        if(pwValue.length === 0) { 
            $(this).addClass("fail");
            // state.memberPwValid는 폼 유효성 검사(state.ok())에서 제외했으므로 별도 업데이트는 안 함
            return;
        }

        var regex = /^(?=.*?[A-Z]+)(?=.*?[a-z]+)(?=.*?[0-9]+)(?=.*?[!@#$]+)[A-Za-z0-9!@#$]{8,16}$/;
        var valid = regex.test(pwValue);
        $(this).addClass(valid ? "success" : "fail");
        // state.memberPwValid = valid; // 본인 인증용이므로 state.ok()에서 제외
    });
    
    $("#password-show").on("click", function(){
        if($(this).hasClass("fa-eye-slash")) {
            $(this).removeClass("fa-eye-slash").addClass("fa-eye");
            $("[name=memberPw]").prop("type", "text");
        } else {
            $(this).removeClass("fa-eye").addClass("fa-eye-slash");
            $("[name=memberPw]").prop("type", "password");
        }
    });

    // 닉네임 관련 (값 변경 시에만 중복 검사)
    $("[name=memberNickname]").on("blur", function(){
        var currentValue = $(this).val();
        var initialValue = $(this).data("initial-value"); 
        
        // [초기값 검사] 값이 변경되지 않았다면 유효성 통과 및 서버 요청 생략
        if(currentValue === initialValue) { 
            $(this).removeClass("success fail fail2").addClass("success"); 
            state.memberNicknameValid = true;
            return;
        }

        // [1] 형식 검사
        var regex = /^[가-힣0-9]{2,10}$/;
        var valid = regex.test(currentValue);
        if(valid == false) {
            $(this).removeClass("success fail fail2").addClass("fail");
            state.memberNicknameValid = false;
            return;
        }

        // [2] 중복 검사 (AJAX) - 값이 변경된 경우에만 요청
        var memberNickname = currentValue;
        $.ajax({
            url:"http://localhost:8080/rest/member/checkMemberNickname?memberNickname="+memberNickname,
            method:"get",
            success:function(response){
                if(response) { // 사용중이라면(존재한다면)
                    $("[name=memberNickname]").removeClass("success fail fail2").addClass("fail2");
                    state.memberNicknameValid = false;
                } else { // 사용중이 아니라면(존재하지 않는다면)
                    $("[name=memberNickname]").removeClass("success fail fail2").addClass("success");
                    state.memberNicknameValid = true;
                }
            }
        });
    });

    // 이메일 관련 (값 변경 시에만 인증 절차 재시작)
    $(".btn-cert-send").on("click", function(){
        var emailInput = $("[name=memberEmail]");
        var currentValue = emailInput.val();
        var initialValue = emailInput.data("initial-value");
        
        // [재발송] 재발송 버튼을 누르면 초기 상태로 되돌림
        if($(this).find("i").hasClass("fa-rotate-right")) {
            emailInput.removeClass("success fail fail2").prop("readonly", false);
            $(this).find("i").removeClass("fa-rotate-right").addClass("fa-paper-plane");
            $(this).find("span").text("인증번호 보내기");
            state.memberEmailValid = false;
            $(".cell-cert-input").hide(); // 인증번호 입력창 숨김
            return;
        }

        // [초기값 검사] 값이 변경되지 않았다면 인증메일 보내기 요청 생략 (이미 인증 완료로 간주)
        if(currentValue === initialValue) {
            // 이메일 필드를 성공 표시하고 잠금 처리
            emailInput.removeClass("success fail fail2").addClass("success");
            emailInput.prop("readonly", true);
            // 버튼의 글자와 아이콘을 변경 (재발송 버튼)
            $(".btn-cert-send").find("i").removeClass("fa-paper-plane").addClass("fa-rotate-right");
            $(".btn-cert-send").find("span").text("인증번호 재발송");
            state.memberEmailValid = true;
            return;
        }
        
        // [인증번호 보내기] (값이 변경되었거나, 초기값이 없었다면)
        emailInput.removeClass("success fail fail2");
        // [1] 이메일 형식 검사
        var regex = /^(.*?)@(.*?)$/;
        var email = emailInput.val();
        var valid = regex.test(email);
        if(valid == false) {
            emailInput.removeClass("success fail fail2").addClass("fail");
            state.memberEmailValid = false;
            return;
        }
        
        // [2] 인증 이메일 발송 요청 (기존 로직 유지)
        $.ajax({
            url:"http://localhost:8080/rest/member/certSend",
            method:"post",
            data: { certEmail : email },
            success: function(response) {
                $(".cell-cert-input").show();
            },
            beforeSend:function(){
                $(".btn-cert-send").prop("disabled", true);
                $(".btn-cert-send").find("i").removeClass("fa-paper-plane").addClass("fa-spinner fa-spin");
                $(".btn-cert-send").find("span").text("인증메일 발송중");
            },
            complete:function(){
                $(".btn-cert-send").prop("disabled", false);
                $(".btn-cert-send").find("i").removeClass("fa-spinner fa-spin").addClass("fa-paper-plane");
                $(".btn-cert-send").find("span").text("인증메일 보내기");
            }
        });
    });

    // .btn-cert-check: 인증번호 확인 (기존 로직 유지)
    $(".btn-cert-check").on("click", function(){
        var certNumber = $(".cert-input").val();
        var regex = /^[0-9]{6}$/;
        var valid = regex.test(certNumber);
        if(valid == false) {
            $(".cert-input").removeClass("success fail fail2").addClass("fail");
            return;
        }

        var certEmail = $("[name=memberEmail]").val();
        $.ajax({
            url:"http://localhost:8080/rest/member/certCheck",
            method:"post",
            data: {certEmail : certEmail , certNumber : certNumber},
            success: function(response) {
                if(response) { // true가 온 경우 (인증 완료)
                    $(".cert-input").removeClass("success fail fail2").val("");
                    $(".cell-cert-input").hide();
                    
                    $("[name=memberEmail]").removeClass("success fail fail2").addClass("success");
                    $("[name=memberEmail]").prop("readonly", true);
                    
                    $(".btn-cert-send").find("i").removeClass("fa-paper-plane").addClass("fa-rotate-right");
                    $(".btn-cert-send").find("span").text("인증번호 재발송");
                    
                    state.memberEmailValid = true;
                } else { // false가 온 경우 (인증 실패)
                    $(".cert-input").removeClass("success fail fail2").addClass("fail2");
                    state.memberEmailValid = false;
                }
            }
        });
    });
	
//인증 내용없는 이메일 형식검사
//$("[name=memberEmail]").on("blur", function(){
//    $("[name=memberEmail]").removeClass("success fail fail2");
//
//    var email = $(this).val();
//
//    // [1] 빈 값 검사 (필수 항목)
//    if (email.length === 0) {
//        $(this).addClass("fail");
//        $(this).nextAll(".fail-feedback").first().text("이메일은 필수 입력 항목입니다");
//        state.memberEmailValid = false;
//        return;
//    }
//
//    // [2] 형식 검사
//    var regex = /^(.*?)@(.*?)$/;
//    var valid = regex.test(email);
//    
//    if (valid === false) {
//        $(this).addClass("fail");
//        $(this).nextAll(".fail-feedback").first().text("올바른 이메일 형식이 아닙니다");
//        state.memberEmailValid = false;
//    } else {
//        $(this).addClass("success");
//        state.memberEmailValid = true;
//    }
//    
//    // 이메일 입력 필드 아래 피드백 문구 초기화
//    $(this).nextAll(".fail-feedback").first().text("올바른 이메일 형식이 아닙니다");
//});
	
	
    // ------------------------------------------

    // 연락처, 생년월일, 주소 관련 로직은 값이 입력되었을 때만 검사하고, 
    // 빈 값은 허용하도록(state.memberContactValid 등 기본 true) 기존 로직을 유지합니다.
    
    // 연락처 관련 (기존 로직 유지, 값 없으면 통과)
    $("[name=memberContact]").on("blur", function(){
        var regex = /^010[1-9][0-9]{3}[0-9]{4}$/;
        // 값이 없거나 형식이 맞으면 통과
        var valid = $(this).val().length == 0 || regex.test($(this).val()); 
        $(this).removeClass("success fail").addClass(valid ? "success" : "fail");
        state.memberContactValid = valid;
    });
    $("[name=memberContact]").on("input", function(){
        var replacement = $(this).val().replace(/[^0-9]/g, "");
        replacement = replacement.substring(0, 11);
        $(this).val(replacement);
    });

    // 생년월일 관련 (기존 로직 유지, 값 없으면 통과)
    $("[name=memberBirth]").on("blur", function(){
        if($(this).val().length == 0) {
            $(this).removeClass("success fail fail2").addClass("success");
            state.memberBirthValid = true;
            return;
        }
        var regex = /^(19[0-9]{2}|20[0-9]{2})-((02-(0[1-9]|1[0-9]|2[0-9]))|((0[469]|11)-(0[1-9]|1[0-9]|2[0-9]|30))|((0[13578]|1[02])-(0[1-9]|1[0-9]|2[0-9]|3[01])))$/;
        var valid = regex.test($(this).val());
        if(valid == false) {
            $(this).removeClass("success fail fail2").addClass("fail");
            state.memberBirthValid = false;
        }
        else {
            var current = moment();
            var inputDate = moment($(this).val());
            var valid2 = current.isAfter(inputDate);

            $(this).removeClass("success fail fail2").addClass(valid2 ? "success" : "fail2");
            state.memberBirthValid = valid2;
        }
    });

    // 주소 관련 (기존 로직 유지, 모두 채우거나 모두 비우면 통과)
    function findAddress() {
        new daum.Postcode({
            oncomplete: function (data) {
                var addr = (data.userSelectedType === 'R') ? data.roadAddress : data.jibunAddress;
                $("[name=memberPost]").val(data.zonecode);
                $("[name=memberAddress1]").val(addr);
                $("[name=memberAddress2]").trigger("focus");
                $(".btn-address-clear").show();
            }
        }).open();
    }
    $(".btn-address-search, [name=memberPost], [name=memberAddress1]").on("click", findAddress);
    $(".btn-address-clear").on("click", function(){
        $("[name=memberPost], [name=memberAddress1], [name=memberAddress2]").val("");
        $(this).hide();
        $("[name=memberPost], [name=memberAddress1], [name=memberAddress2]").removeClass("success fail");
        state.memberAddressValid = true;
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

    // 프로필 이미지 관련 (기존 로직 유지)
    $("[name=attach]").on("input", function(){
        var originUrl = $(".img-preview").prop("src");
        if(originUrl.startsWith("blob:")) {
            URL.revokeObjectURL(originUrl);
        }
        
        if(this.files.length == 0) {
            $(".img-preview").prop("src", "/images/error/no-image.png");
        }
        else {
            var imageUrl = URL.createObjectURL(this.files[0]);
            $(".img-preview").prop("src", imageUrl);
        }
    });

    // 폼 검사: memberPw, memberPwCheckValid는 제외됨
    $(".check-form").on("submit", function(e){
        // memberId, memberNickname, memberEmail이 유효한지 최종 확인
        if(!state.ok()) {
            e.preventDefault();
            alert("필수 항목의 유효성 검사를 통과하지 못했습니다.");
            return;
        }
        
        // 본인 인증을 위해 비밀번호가 입력되었는지 확인 (서버에서 이 비밀번호로 인증함)
        if ($("[name=memberPw]").val().length === 0) {
            e.preventDefault();
            alert("정보 수정을 위해 현재 비밀번호를 입력해주세요.");
            $("[name=memberPw]").focus();
            return;
        }
        
        // 여기에 본인 인증을 위한 AJAX 요청을 추가할 수도 있으나,
        // 현재 로직은 폼 제출 시 서버에서 memberPw로 인증 후 정보 수정을 진행하는 방식에 초점을 맞춥니다.

        // 모든 검사 통과 시 폼 제출
        return true;
    });
});
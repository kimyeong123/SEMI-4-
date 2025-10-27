$(function () {
    //[name=boardContent]를 summernote로 변환
    $(".summernote-editor").summernote({
        //높이 옵션
        height: 250,//시작 높이(px)
        minHeight: 200,//최소 높이(px)
        maxHeight: 400,//최대 높이(px)

        placeholder: "타인에 대한 무분별한 비방 시 예고 없이 삭제될 수 있습니다",

        //메뉴 설정(toolbar)
        toolbar: [
            //하나의 메뉴 그룹을 하나의 배열로 묶어서 작성
            //["그룹명", ["메뉴종류", "메뉴종류", ...]]
            ["font", ["style", "fontname", "fontsize", "forecolor", "backcolor"]],
            ["style", ["bold", "italic", "underline", "strikethrough"]],
            ["attach", ["picture"]],
            ["tool", ["ol", "ul", "table", "hr", "fullscreen"]]
        ],
		
		//커스텀 훅(hook)
		callbacks : {
			onImageUpload : function(files) {
				console.log("파일 업로드 시도중...");
				console.log(files);
				
				//예상 시나리오
				//1. 서버로 사용자가 선택한 이미지를 업로드
				// - ajax + post + multipart 형태로 전송
				//2. 서버에서는 이미지를 저장한 뒤 이미지의 번호를 반환
				//3. 클라이언트에서는 획득한 번호로 <img> 생성
				//4. 에디터에 생성한 이미지를 추가
				// - $(".summernote-editor").summernote("insertNode", 생성한이미지태그객체);
				
				var form = new FormData();//폼 태그 대신 사용할 도구
				for(var i=0; i < files.length; i++) {
					form.append("attach", files[i]);
				}
				
				$.ajax({
					processData:false,
					contentType:false,
					url:"/rest/csBoard/temps",
					method:"post",
					data:form,
					success:function(response){//response == List<Integer>
						//이미지 태그 생성 후 에디터에 추가
						for(var i=0; i < response.length; i++) {
							var img = $("<img>").attr("src", "/attachment/download?attachmentNo="+response[i])
												.attr("data-pk", response[i])
												.addClass("custom-image");
							$(".summernote-editor").summernote("insertNode", img[0]);
						}
					}
				});
			}
		}
    });
});
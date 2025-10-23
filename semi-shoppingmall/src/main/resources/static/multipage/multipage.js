$(function () {
    //1. 첫 페이지만 보이게 하는 것
    $(".page").hide().first().show();

    console.log("총 페이지 수:", $(".page").length);
    console.log("보이는 페이지:", $(".page:visible").attr("class"));

    //2. 다음버튼을 누르면 현재페이지를 숨기고 다음페이지를 표시
    $(".btn-next").on("click", function () {
        $(this).closest(".page").hide(); //현재페이지 숨김
        $(this).closest(".page").next().show(); //다음페이지 표시
        // $(".progressbar").css("width", "40%");

        // var totalLength = $(this).closest(".page").parent().find(".progressbar").width();
        // var currentLength = $(this).closest(".page").parent().find(".progressbar").children().width();
        // var currentPercent = (currentLength / totalLength) * 100;
        // var currentProgress = (currentPercent + 20) + "%";
        // console.log("퍼센트" + currentProgress);
        // var currentLength = $(this).closest(".page").parent().find(".progressbar").children().css("width", currentProgress);


        refreshPage();
    });

    //3. 이전버튼을 누르면 현재페이지를 숨기고 이전페이지를 표시
    $(".btn-prev").on("click", function () {
        $(this).closest(".page").hide(); //현재페이지 숨김
        $(this).closest(".page").prev().show(); //이전페이지 표시

        // var totalLength = $(this).closest(".page").parent().find(".progressbar").width();
        // var currentLength = $(this).closest(".page").parent().find(".progressbar").children().width();
        // var currentPercent = (currentLength / totalLength) * 100;
        // var currentProgress = (currentPercent - 20) + "%";
        // console.log("퍼센트" + currentProgress);
        // var currentLength = $(this).closest(".page").parent().find(".progressbar").children().css("width", currentProgress);

        refreshPage();
    });

    //첫 페이지 상태 계산
    refreshPage();

    // function refreshPage() {
    //     //4. 총 페이지 수 출력
    //     var totalPage = $(".page").length;
    //     $(".total-page").text(totalPage);

    //     //5. 현재 페이지 출력
    //     // 전체 중 표시되는 페이지 위치
    //     var currentPage = $(".page").index(".page:visible") + 1;
    //     $(".current-page").text(currentPage);

    //     //6. 진행상황 계산
    //     var percent = currentPage * 100 / totalPage;
    //     $(".progressbar > .guage").css("width", percent + "%");

    // }

    function refreshPage() {
        //4. 총 페이지 수를 출력
        var totalPage = $(".page").length;
        $(".total-page").text(totalPage);

        //5. 현재 페이지 위치를 출력
        //var currentPage = 전체 페이지 중에서 표시되는 페이지의 위치;
        var currentPage = $(".page").index($(".page:visible")) + 1;
        $(".current-page").text(currentPage);

        //6. 진행상황 계산
        var percent = currentPage * 100 / totalPage;
        $(".progressbar > .guage").css("width", percent + "%");
    }

});

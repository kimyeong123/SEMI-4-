<c:if test="${not empty product}">
<script type="text/javascript">
var productNo = ${product.productNo};

$(function() {

    // 위시리스트 토글
    $("#wishlist-heart").on("click", function() {
        $.ajax({
            url : "${pageContext.request.contextPath}/rest/wishlist/toggle",
            method : "post",
            data : { productNo : productNo },
            success : function(response) {
                if (response.wishlisted) {
                    $("#wishlist-heart i").removeClass("fa-regular").addClass("fa-solid").css("color", "red");
                } else {
                    $("#wishlist-heart i").removeClass("fa-solid").addClass("fa-regular").css("color", "gray");
                }
                $("#wishlist-count").text(response.count);
            },
            error : function() { alert("로그인이 필요합니다."); }
        });
    });

    // 초기 위시리스트 상태 설정
    if ('${wishlisted}' === 'true') {
        $("#wishlist-heart i").css("color", "red");
    } else {
        $("#wishlist-heart i").css("color", "gray");
    }

    // 리뷰 수정 모드 진입
    $(document).on("click", ".btn-edit", function() {
        var btn = $(this);
        var tr = btn.closest("tr");
        var contentTd = tr.find("td.review-content");
        var original = contentTd.text().trim();

        if (contentTd.find("textarea").length > 0) return;

        contentTd.html('<textarea class="edit-content form-control" rows="3" style="width:100%;">' + original + '</textarea>');
        btn.text("완료").removeClass("btn-edit").addClass("btn-update btn-success");
    });

    // 리뷰 수정 완료
    $(document).on("click", ".btn-update", function() {
        var btn = $(this);
        var tr = btn.closest("tr");
        var reviewNo = btn.data("review-no");
        var newContent = tr.find("textarea.edit-content").val().trim();

        if (!newContent) {
            alert("내용을 입력해주세요.");
            return;
        }

        $.ajax({
            url : "${pageContext.request.contextPath}/rest/review/update",
            type : "post",
            data : {
                reviewNo : reviewNo,
                reviewContent : newContent
            },
            success : function(result) {
                if (result) {
                    tr.find("td.review-content").text(newContent);
                    btn.text("수정").removeClass("btn-update btn-success").addClass("btn-edit");
                    alert("리뷰 수정 완료!");
                } else {
                    alert("리뷰 수정 실패 (서버 문제)");
                }
            },
            error : function(xhr) {
                if (xhr.status === 401) alert("로그인이 필요합니다.");
                else if (xhr.status === 403) alert("수정 권한이 없습니다.");
                else if (xhr.status === 404) alert("존재하지 않는 리뷰입니다.");
                else alert("수정 중 알 수 없는 오류 발생");
            }
        });
    });

    // 리뷰 삭제
    $(document).on("click", ".btn-review-delete", function() {
        var btn = $(this);
        var reviewNo = btn.data("review-no");
        if (!confirm("정말 삭제하시겠습니까?")) return;

        $.ajax({
            url : "${pageContext.request.contextPath}/rest/review/delete",
            type : "post",
            data : { reviewNo : reviewNo },
            success : function(result) {
                if (result) {
                    $('#review-' + reviewNo).remove();
                    alert("삭제 완료!");
                    location.reload();
                } else {
                    alert("리뷰 삭제 실패 (서버 문제)");
                }
            },
            error : function(xhr) {
                if (xhr.status === 401) alert("로그인이 필요합니다.");
                else if (xhr.status === 403) alert("삭제 권한이 없습니다.");
                else if (xhr.status === 404) alert("해당 리뷰를 찾을 수 없습니다.");
                else alert("삭제 중 알 수 없는 오류 발생");
            }
        });
    });

    // 리뷰 등록
    $("#submitReviewBtn").click(function() {
        var reviewContent = $('.summernote-editor').summernote('code').trim();
        $("#reviewForm").find("textarea[name='reviewContent']").val(reviewContent);

        var formData = new FormData($("#reviewForm")[0]);
        formData.append("productNo", productNo);

        if (reviewContent === "" || reviewContent === "<p><br></p>" || reviewContent.replace(/<[^>]*>/g, '').trim() === "") {
            alert("리뷰 내용을 입력해주세요.");
            return;
        }
        if ($("#reviewRatingInput").val() === "0") {
            alert("평점을 선택해주세요.");
            return;
        }

        $.ajax({
            url : "${pageContext.request.contextPath}/rest/review/add",
            type : "post",
            data : formData,
            processData : false,
            contentType : false,
            success : function(result) {
                if (result) {
                    alert("리뷰가 등록되었습니다!");
                    location.reload();
                } else {
                    alert("리뷰 등록 실패");
                }
            },
            error : function() {
                alert("리뷰 등록 중 오류가 발생했습니다.");
            }
        });
    });

    // 별점 선택
    $("#reviewRatingStars").on("mouseover", ".star-input", function() {
        var currentRating = $(this).data('rating');
        $("#reviewRatingStars .star-input").each(function() {
            if ($(this).data('rating') <= currentRating)
                $(this).removeClass("fa-regular").addClass("fa-solid").css("color", "orange");
            else
                $(this).removeClass("fa-solid").addClass("fa-regular").css("color", "#ccc");
        });
    }).on("mouseout", function() {
        var selectedRating = $("#reviewRatingInput").val();
        $("#reviewRatingStars .star-input").each(function() {
            if ($(this).data('rating') <= selectedRating)
                $(this).removeClass("fa-regular").addClass("fa-solid").css("color", "orange");
            else
                $(this).removeClass("fa-solid").addClass("fa-regular").css("color", "#ccc");
        });
    }).on("click", ".star-input", function() {
        var selectedRating = $(this).data('rating');
        $("#reviewRatingInput").val(selectedRating);
        $("#reviewRatingStars .star-input").each(function() {
            if ($(this).data('rating') <= selectedRating)
                $(this).removeClass("fa-regular").addClass("fa-solid").css("color", "orange");
            else
                $(this).removeClass("fa-solid").addClass("fa-regular").css("color", "#ccc");
        });
    });

    // 장바구니 담기
    $("#addToCartBtn").on("click", function() {
        var color = $("#color-selector").val();
        var size = $("#size-selector").val();
        var quantity = $("#cartQuantity").val();

        if (!color || !size) {
            alert("색상과 사이즈를 모두 선택해주세요.");
            return;
        }
        if (quantity < 1 || isNaN(quantity)) {
            alert("수량을 1개 이상 입력해주세요.");
            $("#cartQuantity").val(1);
            return;
        }

        $.ajax({
            url: "${pageContext.request.contextPath}/rest/cart/add",
            method: "post",
            data: {
                productNo: productNo,
                color: color,
                size: size,
                cartAmount: quantity
            },
            success: function() {
                alert("장바구니에 상품을 담았습니다!");
            },
            error: function(xhr) {
                if (xhr.status === 401) alert("로그인이 필요합니다.");
                else alert("장바구니 담기 중 오류 발생");
            }
        });
    });

});
</script>
</c:if>

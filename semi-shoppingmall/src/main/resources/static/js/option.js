$(function() {
    let optionIndex = 0; // 옵션 세트 인덱스 관리용

    // ✅ 옵션 세트 추가
    $("#btn-add-set").click(function() {
        var html = `
        <div class='option-set' data-index='${optionIndex}'>
            <div class='cell'>
                <label>옵션 이름 *</label>
                <input type='text' name='optionList[${optionIndex}].optionName' placeholder='예: 색상' required class='field w-100'>
            </div>

            <div class='cell'>
                <label>옵션 값 *</label>
                <div class='option-values' style='display:flex; flex-wrap:wrap; gap:5px;'>
                    <div class='option-item'>
                        <input type='text' name='optionList[${optionIndex}].optionValueList[0]' placeholder='예: 빨강' class='field option-field'>
                        <button type='button' class='btn btn-delete-value'>−</button>
                    </div>
                </div>
                <button type='button' class='btn btn-add-value mt-10' data-index='${optionIndex}'>+ 값 추가</button>
            </div>

            <button type='button' class='btn btn-danger btn-remove-set mt-10'>옵션 세트 삭제</button>
            <hr>
        </div>`;
        $("#option-container").append(html);
        optionIndex++;
    });

    // ✅ 옵션 세트 삭제
    $(document).on("click", ".btn-remove-set", function() {
        if ($(".option-set").length > 1) {
            $(this).closest(".option-set").remove();
        } else {
            alert("최소 한 개의 옵션 세트는 필요합니다.");
        }
    });

    // ✅ 옵션 값 추가
    $(document).on("click", ".btn-add-value", function() {
        var idx = $(this).data("index");
        var valueCount = $(this).siblings(".option-values").find(".option-item").length;
        var html = `
        <div class='option-item'>
            <input type='text' name='optionList[${idx}].optionValueList[${valueCount}]' placeholder='값 입력' class='field option-field'>
            <button type='button' class='btn btn-delete-value'>−</button>
        </div>`;
        $(this).siblings(".option-values").append(html);
    });

    // ✅ 옵션 값 삭제
    $(document).on("click", ".btn-delete-value", function() {
        var $values = $(this).closest(".option-values").find(".option-item");
        if ($values.length > 1) {
            $(this).closest(".option-item").remove();
        } else {
            alert("옵션 값은 최소 하나 이상 필요합니다.");
        }
    });
});

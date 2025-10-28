$(function() {
    // 옵션 세트 추가
    $("#btn-add-set").click(function() {
        var html = "";
        html += "<div class='option-set'>";
        html += "  <div class='cell'>";
        html += "    <label>옵션 이름 *</label>";
        html += "    <input type='text' name='optionNameList' placeholder='예: 사이즈' required class='field w-100'>";
        html += "  </div>";
        html += "  <div class='cell'>";
        html += "    <label>옵션 값 *</label>";
        html += "    <div class='option-values' style='display:flex; flex-wrap:wrap; gap:5px;'>";
        html += "      <div class='option-item'>";
        html += "        <input type='text' name='optionValueList' placeholder='예: S' class='field option-field'>";
        html += "        <button type='button' class='btn btn-delete-value'>−</button>";
        html += "      </div>";
        html += "    </div>";
        html += "    <button type='button' class='btn btn-add-value mt-10'>+ 값 추가</button>";
        html += "  </div>";
        html += "  <button type='button' class='btn btn-danger btn-remove-set mt-10'>옵션 세트 삭제</button>";
        html += "  <hr>";
        html += "</div>";
        $("#option-container").append(html);
    });

    // 옵션 세트 삭제
    $(document).on("click", ".btn-remove-set", function() {
        if ($(".option-set").length > 1) {
            $(this).closest(".option-set").remove();
        } else {
            alert("최소 한 개의 옵션 세트는 필요합니다.");
        }
    });

    // 옵션 값 추가
    $(document).on("click", ".btn-add-value", function() {
        var html = "";
        html += "<div class='option-item'>";
        html += "  <input type='text' name='optionValueList' placeholder='값 입력' class='field option-field'>";
        html += "  <button type='button' class='btn btn-delete-value'>−</button>";
        html += "</div>";
        $(this).siblings(".option-values").append(html);
    });

    // 옵션 값 삭제
    $(document).on("click", ".btn-delete-value", function() {
        var $values = $(this).closest(".option-values").find(".option-item");
        if ($values.length > 1) {
            $(this).closest(".option-item").remove();
        } else {
            alert("옵션 값은 최소 하나 이상 필요합니다.");
        }
    });
});

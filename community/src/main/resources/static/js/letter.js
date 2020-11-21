$(function () {
    $("#sendBtn").click(send_letter);
    $(".close").click(delete_msg);
});

function send_letter() {
    $("#sendModal").modal("hide");

    var toName = $("#recipient-name").val();
    var content = $("#message-text").val();
    $.post(
        CONTEXT_PATH + "/letter/send",
        {"toName": toName, "content": content},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $("#hintBody").text("发送成功");
            } else {
                $("#hintBody").text(data.msg);
            }

            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                location.reload();
            }, 2000);
        }
    )
}

function delete_msg() {
    var btn = this
    var id = $(btn).prev().val()

    // TODO 删除数据
    $.post(
        CONTEXT_PATH + "/letter/delete",
        {"id": id},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(this).parents(".media").remove();
                location.reload();
            } else {
                alert(data.msg)
            }
        }
    )
}
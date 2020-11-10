$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    $("#publishModal").modal("hide");

    // 获取到标题和内容
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
    // 发送异步请求
    // 按照如下格式：
    // 1 访问路径
    // 2 传入的数据
    // 3 回调函数
    $.post(
        CONTEXT_PATH + "/discuss/add",
        {"title": title, "content": content},
        function (data) {
            data = $.parseJSON(data)
            // 在提示框中显示返回的消息,异步操作
            $("#hintBody").text(data.msg)
            // 显示提示框
            $("#hintModal").modal("show");
            // 两秒后自动隐藏提示框
            setTimeout(function () {
                $("#hintModal").modal("hide");
                // 刷新页面
                if (data.code == 0) {
                    window.location.reload()
                }
            }, 2000);
        }
    )
}
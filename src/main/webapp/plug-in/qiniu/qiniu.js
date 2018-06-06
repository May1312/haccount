function qiNiuupload(url, inputFileId, inputId, imageShowId) {

    // $("#btn_uploadimg").click(function () {
    var fileObj = document.getElementById(inputFileId).files[0]; // js 获取文件对象
    if (typeof (fileObj) == "undefined" || fileObj.size <= 0) {
        alert("请选择图片");
        return;
    }
    var formFile = new FormData();
    formFile.append("action", "UploadVMKImagePath");
    formFile.append("file", fileObj); //加入文件对象

    //第一种  XMLHttpRequest 对象
    //var xhr = new XMLHttpRequest();
    //xhr.open("post", "/Admin/Ajax/VMKHandler.ashx", true);
    //xhr.onload = function () {
    //    alert("上传完成!");
    //};
    //xhr.send(formFile);

    //第二种 ajax 提交

    var data = formFile;

    $.ajax({
        url: url,
        data: data,
        type: "Post",
        dataType: "json",
        cache: false,//上传文件无需缓存
        processData: false,//用于对data参数进行序列化处理 这里必须false
        contentType: false, //必须
        success: function (result) {
            $("#" + inputId).val(result.obj);
            var img = document.getElementById(imageShowId);
            img.src = result.obj;
            img.style.display = "block";
        },
    })
    // })

}

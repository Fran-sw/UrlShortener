$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                $("#result").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $("#resultQr").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $("#result2").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $.ajax({
                    type: "POST",
                    url: "/link",
                    data: $(this).serialize(),
                    success: function (msg) {
                        console.log(msg)
                        $("#result").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>"
                            );
                    },
                    error: function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
        $("#shortenerQR").submit(
            function (event) {
                event.preventDefault();
                $("#result").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $("#resultQr").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $("#result2").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $.ajax({
                    type: "POST",
                    url: "/linkQR",
                    data: $(this).serialize(),
                    success: function (msg) {
                        console.log(msg)
                        $("#resultQr").html(
                            "<img src=\"data:image/png;base64, " + msg.qr +"\" />"
                            + "<div class='alert alert-success lead'><a target='_blank' href='"
                            + "data:image/png;base64, " + msg.qr
                            + "'>"
                            + msg.uri+ ".png"
                            + "</a></div>"
                            );
                    },
                    error: function () {
                        $("#resultQr").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
        $("#CSVshortener").submit(
            function (event) {
                event.preventDefault();
                $("#result").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $("#resultQr").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $("#result2").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                var form = $('#CSVshortener')[0];
                var data = new FormData(form);
                $.ajax({
                    type: "POST",
                    url: "/csv",
                    enctype: 'multipart/form-data',
                    processData: false,
                    //contentType: multipart/form-data, charset=utf-8, boundary="---54143246---",
                    contentType: false,
                    data: data,
                    success: function (msg) {
                        var blob = new Blob([msg],{type:'text/plain'});
                        var link = document.createElement('a');
                        link.href = window.URL.createObjectURL(blob);
                        link.download="shortened.csv";
                        link.innerHTML="Download File";
                        //document.getElementsById("result2").innerHTML=link;
                        $("#result2").html(
                            link);
                        //link.click();
                        //document.body.removeChild(link);
                    },
                    error: function () {
                        $("#result2").html(
                            "<div class='alert alert-danger lead'>File not found</div>");
                    }
                });
            });
        $("#agentsInfo").submit(
            function (event) {
                event.preventDefault();
                $("#result").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $("#resultQr").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $("#result2").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $.ajax({
                    type: "GET",
                    url: "/agentsInfo",
                    data: $(this).serialize(),
                    success: function (msg) {
                        console.log(msg)
                        $("#resultA").html(
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg
                            + "'>"
                            + msg
                            + "</a></div>"
                            );
                    },
                    error: function () {
                        $("#resultA").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                    });
                });
    });
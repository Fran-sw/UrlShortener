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
                        devolver = "";
                        console.log(msg)
                        if (msg.qr != null) {
                            devolver =
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri + "</div>"
    
                            + "<div class='alert alert-success lead'><a target='_blank' href='"
                            + "data:image/png;base64, " + msg.qr
                            + "'>"
                            + msg.uri+ ".png"
                            + "</a></div>"
                        } else {
                            devolver = 
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri
                            + "</a></div>"
                            
                        }
                        $("#result").html(devolver);
                    },
                    error: function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR: URL de destino no valida todavia</div>");
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
                    contentType: false,
                    data: data,
                    success: function (msg) {
                        var blob = new Blob([msg],{type:'text/plain'});
                        var link = document.createElement('a');
                        link.href = window.URL.createObjectURL(blob);
                        link.download="shortened.csv";
                        link.innerHTML="Download File";
                        $("#result2").html(
                            link);
                    },
                    error: function () {
                        $("#result2").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
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
                        var s = JSON.stringify(msg);

                        $("#resultA").html(
                            "<div class='alert alert-success lead'>"
                            + s
                            +" </div>"
                            );
                    },
                    error: function () {
                        $("#resultA").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                    });
                });
    });
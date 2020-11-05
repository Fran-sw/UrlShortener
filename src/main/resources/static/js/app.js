$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
                $("#result").html(
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
                            + "<img src=\"data:image/png;base64, " + msg.qr +"\" />"
                            );
                        
                        // QR print
                        //var b64Response = btoa(msg.qr_code);
                        //var outputImg = document.createElement('img');
                        //outputImg.src = 'data:image/jpgbase64,' + b64Response;
                        //document.body.appendChild(outputImg);
                    },
                    error: function () {
                        $("#result").html(
                            "<div class='alert alert-danger lead'>ERROR</div>");
                    }
                });
            });
        $("#CSVshortener").submit(
            function (event) {
                event.preventDefault();
                $("#result").html(
                    "<div class='alert alert-danger lead' style='display: none'></div>");
                $("#resul2").html(
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
                        console.log(msg)
                        $("#result2").html(
                            "<div class='alert alert-success lead'>"+"Te"+"</div>");
                    },
                    error: function () {
                        $("#result2").html(
                            "<div class='alert alert-danger lead'>ERROR2</div>");
                    }
                });
            });
    });
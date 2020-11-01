$(document).ready(
    function () {
        $("#shortener").submit(
            function (event) {
                event.preventDefault();
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
    });
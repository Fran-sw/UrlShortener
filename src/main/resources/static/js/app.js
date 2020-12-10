var socketClient = null;
var stompClient = null;
            
function connect() {
    var socket = new SockJS('/chat'); 
    socketClient = socket;
    stompClient = Stomp.over(socket);  
    stompClient.connect({}, function(frame) { 
        console.log('Connected: ' + frame);
        Thread.sleep(100); 
        stompClient.subscribe('/topic/messages', function(messageOutput) {
            showMessageOutput(JSON.parse(messageOutput.body));
        });
    });
}
            
function disconnect() {
    $("#result2").html("Entra en disconnect");
    if(stompClient != null) {
        $("#result2").html("Tenemos un cliente que desconectar");
        stompClient.disconnect();
        console.log('Disconected');
        $("#result2").html("Hemos desconectado al cliente");
    }
    $("#result2").html("Acabamos la función de desconectar");
}
            
function sendMessage() {
    $("#result2").html("preparamos para enviar");
    stompClient.send("/app/chat", {}, JSON.stringify({'from':"from", 'text':"text"}));
    $("#result2").html("enviado");
}
            
function showMessageOutput(messageOutput) {
    $("#result2").html("responden");
    $("#result2").html(messageOutput.text);
}

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
                            + msg.uri 
                            + "</div>"
    
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
                            "<div class='alert alert-danger lead'>ERROR: URL de destino no valida o no alcanzable</div>");
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
                $("#result2").html("Paso 1");
                connect();
                $("#result2").html("Paso 2");
                sendMessage();
                $("#result2").html("Paso 3");
                disconnect();
                $("#result2").html("Funciona");
                /*
                $.ajax({
                    type: "POST",
                    url: "/csv",
                    enctype: 'multipart/form-data',
                    processData: false,
                    contentType: false,
                    data: data,
                    success: function (msg) {
                        var blob = new Blob([msg],{type:'text/csv'});
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
                });*/
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
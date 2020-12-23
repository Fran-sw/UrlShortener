var socketClient = null;
var stompClient = null;
var resultado = "";
            
function connect(fileContent) {
    $("#result2").html("Inicio conexion");
    var socket = new SockJS('/chat'); 
    socketClient = socket;
    stompClient = Stomp.over(socket);  
    stompClient.connect({}, function(frame) { 
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function(messageOutput) {
            showMessageOutput(JSON.parse(messageOutput.body));
        });
        $("#result2").html("Enviamos");
        sendMessage(fileContent);
        $("#result2").html("Enviado");
        //disconnect();
    });
}
            
function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
        console.log('Disconected');
    }
}
   
/*
function sendMessage(file) {
    stompClient.send("/app/chat", {}, JSON.stringify({'from':"from", 'csv':file, 'answer':"text"}));
}*/
function sendMessage(fileContent) {
    stompClient.send("/app/chat", {}, JSON.stringify({'content':fileContent, 'answer':"text"}));
}
            
function showMessageOutput(messageOutput) {
    $("#result2").html("Respuesta");
    $("#result2").html(messageOutput.answer);
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
                $("#result2").html("Empieza");
                var form = $('#CSVshortener')[0];
                var data = new FormData(form);
                const reader = new FileReader();
                $("#result2").html("abre reader");
                var fileInput = data.get('csv');
                reader.addEventListener('load', (event) => {
                  const result = event.target.result;
                  $("#result2").html(result);
                  connect(result);
                });
                reader.readAsText(fileInput);
                //$("#result2").html("lee3");
                var fileInput = data.get('csv');
                //connect(data);
                
                //https://github.com/piter1902/UrlShortener/blob/csvWebsockets/webApp/src/main/resources/static/js/app.js

                /*
                $.ajax({
                    type: "POST",
                    url: "/csv",
                    enctype: 'multipart/form-data',
                    processData: false,
                    contentType: false,
                    data: data,
                    success: function (msg) {/*
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
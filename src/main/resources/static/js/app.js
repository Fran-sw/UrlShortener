var socketClient = null;
var stompClient = null;
var resultado = "";
var lineas = 0;
var recibidas = 0;
            
function connect(fileContent) {
    var socket = new SockJS('/chat'); 
    socketClient = socket;
    stompClient = Stomp.over(socket);  
    stompClient.connect({}, function(frame) { 
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/messages', function(messageOutput) {
            showMessageOutput(JSON.parse(messageOutput.body));
        });
        //Limpieza de lineas vacias de https://stackoverflow.com/questions/16369642/javascript-how-to-use-a-regular-expression-to-remove-blank-lines-from-a-string
        fileContent = fileContent + "\r\n" + "\r\n";
        var fileContentCount = fileContent.replace(/^\s*[\r\n]/gm,'');
        //Siempre habrá una línea vacía al final del fichero, se ignora
        lineas = (fileContentCount.split(/\r\n|\r|\n/).length-1);
        if(lineas>0){
            sendMessage(fileContent);
        }else{
            $("#result2").html("<div class='alert alert-danger lead'>No se ha introducido fichero</div>");
            disconnect();
        }
    });
}
            
function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
}
   
function sendMessage(fileContent) {
    stompClient.send("/app/chat", {}, JSON.stringify({'content':fileContent, 'answer':window.location.href}));
}
            
function showMessageOutput(messageOutput) {
    recibidas++;
    if(messageOutput.answer=="El fichero está vacio"){
        $("#result2").html("<div class='alert alert-danger lead'>El fichero esta vacio</div>");
    }else{
        resultado=resultado+messageOutput.answer;
        if(recibidas>=lineas){
            var blob = new Blob([resultado],{type:'text/csv'});
            var link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download="shortened.csv";
            link.innerHTML="Download File";
            $("#result2").html(link);
            disconnect();
        }
    }
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
                        if (msg.qrUrl != null) {
                            devolver =
                            "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.uri
                            + "'>"
                            + msg.uri 
                            + "</div>"
    
                            + "<div class='alert alert-success lead'><a target='_blank' href='"
                            + msg.qrUrl
                            + "'>"
                            + msg.qrUrl 
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
                            "<div class='alert alert-danger lead'>ERROR: URL de destino no valida</div>");
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
                resultado="";
                lineas=0;
                recibidas=0;
                var form = $('#CSVshortener')[0];
                var data = new FormData(form);
                const reader = new FileReader();
                
                //https://github.com/piter1902/UrlShortener/blob/csvWebsockets/webApp/src/main/resources/static/js/app.js
                // Solución inspirada en esta solución, la idea de leer el contenido del fichero desde el cliente y mandar el contenido al servidor.

                var fileInput = data.get('csv');
                reader.addEventListener('load', (event) => {
                  const result = event.target.result;
                  connect(result);
                });
                reader.readAsText(fileInput);
                
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
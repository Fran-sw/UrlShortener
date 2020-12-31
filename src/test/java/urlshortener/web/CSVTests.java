package urlshortener.web;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static urlshortener.fixtures.ShortURLFixture.someUrl;
import static urlshortener.fixtures.ShortURLFixture.shortURL1;
import static urlshortener.fixtures.ShortURLFixture.shortURL2;
import static urlshortener.fixtures.ShortURLFixture.shortURL3;
import static urlshortener.fixtures.ShortURLFixture.shortURL4;
import static urlshortener.fixtures.ShortURLFixture.shortURL5;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.net.URI;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.http.MediaType;

import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import urlshortener.service.Message;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class CSVTests {

    //private MockMvc mockMvc;

    @Mock
    private ClickService clickService;
  
    @Mock
    private ShortURLService shortUrlService;
  
    @InjectMocks
    private UrlShortenerController urlShortener;

    private String contenidoCSV; 
    private String expectedCSV; 
    private int recibidas=0;
    private int total=1;
  
    @Before
    public void setup() {
        contenidoCSV = "https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat;";
        expectedCSV = "https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat;true;http://localhost:8080/ec64f62e;";
    }
  
    @Ignore
    @Test
    public void checkCreateShortenedCSV(){
        //Preparamos el mensaje a enviar
        Message mensaje = new Message(contenidoCSV,"http://localhost:8080/");
        //Iniciamos la conexión con el endpoint
        WebSocketStompClient  stompClient = Stomp.over(new SockJs("/chat"));
        //Generamos la sesión de stomp
        StompSession stompSession = stompClient.connect("http://localhost:8080/", new StompSessionHandlerAdapter() {});
        //Nos suscribimos y preparamos para recibir respuesta
        stompSession.subscribe("/user/topic/messages", new showMessageOutput(JSON.parse(messageOutput.body)));
        //Enviamos el mensaje con la información
        stompSession.send("/app/chat", mensaje);
    }

    //La función comprobará si coinciden las strings esperadas y recibidas
    public void showMessageOutput(String messageOutput) {
        assertEquals(messageOutput,expectedCSV);
    }
}
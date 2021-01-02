/*
 * Partial functionality from https://github.com/rstoyanchev/spring-websocket-portfolio/blob/master/src/test/java/org/springframework/samples/portfolio/web/context/ContextPortfolioControllerTests.java
 */
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

import urlshortener.service.MessageInternal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import org.springframework.stereotype.Service;

@Service
public class CSVTests {

	@Autowired private AbstractSubscribableChannel clientInboundChannel;

	@Autowired private AbstractSubscribableChannel clientOutboundChannel;

	@Autowired private AbstractSubscribableChannel brokerChannel;

	private TestChannelInterceptor clientOutboundChannelInterceptor;

	private TestChannelInterceptor brokerChannelInterceptor;

    private String contenidoCSV = "https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat;"; 
    private String expectedCSV = "https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat;true;http://localhost:8080/ec64f62e;"; 
    private int recibidas=0;
    private int total=1;
  
    @Before
    public void setUp() throws Exception {
		this.brokerChannelInterceptor = new TestChannelInterceptor();
		this.clientOutboundChannelInterceptor = new TestChannelInterceptor();

		this.brokerChannel.addInterceptor(this.brokerChannelInterceptor);
		this.clientOutboundChannel.addInterceptor(this.clientOutboundChannelInterceptor);
    }
  

    @Test
    @Ignore
    public void checkCreateShortenedCSV() throws Exception {

        //Enviamos una solicitud de SUBSCRIBE
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		headers.setSubscriptionId("0");
		headers.setDestination("/topic/messages");
		headers.setSessionId("0");
		headers.setSessionAttributes(new HashMap<>());
		Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());

		this.clientOutboundChannelInterceptor.setIncludedDestinations("/topic/messages");
        this.clientInboundChannel.send(message);
        
        MessageInternal mensaje = new MessageInternal(contenidoCSV,"http://localhost:8080/");
        //Enviamos la URL a acortar
        StompHeaderAccessor headers2 = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		headers2.setSubscriptionId("0");
		headers2.setDestination("/app/chat");
		headers2.setSessionId("0");
		headers2.setSessionAttributes(new HashMap<>());
		Message<MessageInternal> message2 = MessageBuilder.createMessage(mensaje, headers.getMessageHeaders());

		this.clientOutboundChannelInterceptor.setIncludedDestinations("/app/chat");
		this.clientInboundChannel.send(message2);
        
        //Esperamos a recibir una respuesta
        Message<?> positionUpdate = this.brokerChannelInterceptor.awaitMessage(25);
		assertNotNull(positionUpdate);
        
        //Comprobamos el contenido del mensaje
        assertEquals(positionUpdate.getPayload(),expectedCSV);
    }
}
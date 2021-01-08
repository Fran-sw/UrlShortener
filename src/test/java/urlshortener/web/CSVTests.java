/*
 * Partial functionality from https://github.com/rstoyanchev/spring-websocket-portfolio/blob/master/src/test/java/org/springframework/samples/portfolio/web/context/ContextPortfolioControllerTests.java
 */
package urlshortener.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.MimeType;

import urlshortener.service.MessageInternal;

import static org.junit.Assert.*;


import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CSVTests {

	@Autowired private AbstractSubscribableChannel clientInboundChannel;

	@Autowired private AbstractSubscribableChannel brokerChannel;

	private TestChannelInterceptor brokerChannelInterceptor;

    @Before
    public void setUp() {
		brokerChannelInterceptor = new TestChannelInterceptor();
		brokerChannel.addInterceptor(this.brokerChannelInterceptor);
    }

    @Test
    public void currentBehaviour() throws Exception {
		//Enviamos una solicitud de SUBSCRIBE a /user/topic/messages al principio, pero solo debe mandarse una vez.
		StompHeaderAccessor subscribeHeaders = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		subscribeHeaders.setDestination("/user/topic/messages");
		subscribeHeaders.setSessionId("");
		Message<byte[]> subscribeMessage = MessageBuilder.createMessage(new byte[0], subscribeHeaders.getMessageHeaders());
		clientInboundChannel.send(subscribeMessage);
		test(
			"https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat;\n",
			"https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat;;true;http://localhost:8080/3ae419f7;\n"
		);
	}

	@Test
	public void whithoutTheLastSemicolonTheHashChanges() throws Exception {
		test(
			"https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat\n",
			"https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat;true;http://localhost:8080/ec64f62e;\n"
		);
	}

	@Test
	public void whyThisFails() throws Exception {	//Used to fail, now both client and server make sure to add 1 line separator at the end of the file for correct line count
		test(
			"https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat",
			"https://www.youtube.com/watch?v=oGURDYckNEI&ab_channel=Kat;true;http://localhost:8080/ec64f62e;\n"
		);
	}

	public void test(String send, String expected) throws IOException, InterruptedException {
		//No enviamos la petición SUBSCRIBE aqui pues se repetiría la petición y causa problemas
		/*
		StompHeaderAccessor subscribeHeaders = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
		subscribeHeaders.setDestination("/user/topic/messages");
		subscribeHeaders.setSessionId("");
		Message<byte[]> subscribeMessage = MessageBuilder.createMessage(new byte[0], subscribeHeaders.getMessageHeaders());
		clientInboundChannel.send(subscribeMessage);*/

		// A partir de ahora esperamos mensajes enviados a /user/topic/messages
		brokerChannelInterceptor.setIncludedDestinations("/user/topic/messages");

		// Enviamos la URL a acortar via SEND /app/chat
		MessageInternal sendPayload = new MessageInternal(send,"http://localhost:8080/");
		StompHeaderAccessor sendHeaders = StompHeaderAccessor.create(StompCommand.SEND);
		sendHeaders.setDestination("/app/chat");
		sendHeaders.setSessionId("");
		sendHeaders.setSessionAttributes(Collections.emptyMap());
		Message<MessageInternal> sendMessage = MessageBuilder.createMessage(sendPayload, sendHeaders.getMessageHeaders());
		clientInboundChannel.send(sendMessage);

		// Esperamos a recibir una respuesta
		Message<?> positionUpdate = brokerChannelInterceptor.awaitMessage(300);
		assertNotNull(positionUpdate);

		// Nos aseguramos que podemos procesar su contenido
		assertEquals(MimeType.valueOf("application/json"), positionUpdate.getHeaders().get("contentType"));
		assertEquals(positionUpdate.getPayload().getClass(), byte[].class);
		MessageInternal receivedMessage = new ObjectMapper().readValue((byte[]) positionUpdate.getPayload(), MessageInternal.class);

		assertEquals(expected, receivedMessage.getAnswer());
	}
}
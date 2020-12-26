package urlshortener.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

import org.springframework.stereotype.Component;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/topic");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/chat").addInterceptors(new IpHandshakeInterceptor()).withSockJS();
  }

  @Component
  public static class IpHandshakeInterceptor implements HandshakeInterceptor {

      @Override
      public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                     WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
          // Set ip attribute to WebSocket session
          if (request instanceof ServletServerHttpRequest) {
              ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
              String ipAddress = servletRequest.getServletRequest().getHeader("X-FORWARDED-FOR");
              if (ipAddress == null) {
                  ipAddress = servletRequest.getServletRequest().getRemoteAddr();
              }
              attributes.put("ip", ipAddress);
          }
          return true;
      }

      public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Exception exception) {
      }
  }

}

package com.smallstore.dashboard.configuration;

import com.smallstore.dashboard.interceptor.IpHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // messages go to subscribers
        config.setApplicationDestinationPrefixes("/app"); // messages sent from client
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat") // WebSocket endpoint
                .addInterceptors(new IpHandshakeInterceptor())
                .setAllowedOriginPatterns("https://smallstore.in","https://*.smallstore.in","http://localhost:3000") // allow all origins
                .withSockJS(); // fallback for browsers
    }
}


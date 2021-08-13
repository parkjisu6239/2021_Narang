package com.exp.narang.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/to");
        registry.enableSimpleBroker("/from/chat", "/from/mafia/start", "/from/mafia/role", "/from/mafia/vote", "/from/mafia/mafias", "/from/mafia/players",
                "/from/call/start", "/from/call/chat", "/from/call/addPlayer");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/narang").setAllowedOriginPatterns("*").withSockJS();
    }
}
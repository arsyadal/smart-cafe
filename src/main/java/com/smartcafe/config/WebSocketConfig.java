package com.smartcafe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig - Configuration for WebSocket Communication
 * 
 * Enables STOMP (Simple Text Oriented Messaging Protocol) over WebSocket
 * for real-time communication between server and clients.
 * 
 * Key concepts:
 * - Message Broker: Routes messages to subscribers
 * - STOMP Endpoint: Entry point for WebSocket connections
 * - Topics: Pub/sub destinations (e.g., /topic/kitchen)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure the message broker
     * 
     * - /topic: Prefix for pub/sub destinations (broadcast to multiple subscribers)
     * - /app: Prefix for messages bound for @MessageMapping methods
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker for broadcasting messages
        // Messages sent to /topic/* will be broadcast to all subscribers
        config.enableSimpleBroker("/topic");

        // Prefix for messages FROM clients TO server (@MessageMapping methods)
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Register STOMP endpoints for WebSocket connections
     * 
     * Clients will connect to: ws://localhost:8080/ws
     * SockJS fallback enabled for browsers without WebSocket support
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Main WebSocket endpoint
        // withSockJS() provides fallback options for browsers that don't support
        // WebSocket
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allow all origins for development
                .withSockJS();
    }
}

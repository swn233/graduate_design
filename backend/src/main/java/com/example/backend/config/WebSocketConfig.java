package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PythonWebSocketHandler pythonWebSocketHandler;

    public WebSocketConfig(PythonWebSocketHandler pythonWebSocketHandler) {
        this.pythonWebSocketHandler = pythonWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(pythonWebSocketHandler, "/ws/python").setAllowedOrigins("*");
    }
}
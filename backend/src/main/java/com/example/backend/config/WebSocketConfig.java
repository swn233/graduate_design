package com.example.backend.config;

import com.example.backend.handler.PythonWebSocketHandler;
import com.example.backend.websocket.FileSystemWebSocketHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    @Resource
    private final PythonWebSocketHandler pythonWebSocketHandler;
    
    @Resource
    private final FileSystemWebSocketHandler fileSystemWebSocketHandler;

    public WebSocketConfig(PythonWebSocketHandler pythonWebSocketHandler, 
                          FileSystemWebSocketHandler fileSystemWebSocketHandler) {
        this.pythonWebSocketHandler = pythonWebSocketHandler;
        this.fileSystemWebSocketHandler = fileSystemWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(pythonWebSocketHandler, "/ws/python").setAllowedOrigins("*");
        registry.addHandler(fileSystemWebSocketHandler, "/ws/files").setAllowedOrigins("*");
    }
}
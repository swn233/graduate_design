package com.example.backend.handler;

import com.example.backend.service.PythonExecuteService;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class PythonWebSocketHandler extends TextWebSocketHandler {

    @Resource
    private final PythonExecuteService pythonService;

    public PythonWebSocketHandler(PythonExecuteService pythonService) {
        this.pythonService = pythonService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String code = message.getPayload();
        new Thread(() -> {
            try {
                String result = pythonService.executePythonCode(code);
                System.out.println("后端handler执行");
                session.sendMessage(new TextMessage(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
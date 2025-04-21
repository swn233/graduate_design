package com.example.backend.handler;

import com.example.backend.service.PythonExecuteService;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class PythonWebSocketHandler extends TextWebSocketHandler {

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
                session.sendMessage(new TextMessage(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
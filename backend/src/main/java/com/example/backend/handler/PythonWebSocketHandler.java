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
    public void afterConnectionEstablished(WebSocketSession session) {
        // 连接建立时启动Python进程
        pythonService.startPythonProcess(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String code = message.getPayload();
        String sessionId = session.getId();
        
        // 执行Python代码
        pythonService.executePythonCode(sessionId, code, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) {
        // 连接关闭时停止Python进程
        pythonService.stopPythonProcess(session.getId());
    }
}
package com.example.graduate_design.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JupyterService {
    private final ConcurrentHashMap<String, Process> kernelProcesses = new ConcurrentHashMap<>();

    public void executeCode(String sessionId, String code, WebSocketSession session) {
        try {
            // 创建Python进程
            ProcessBuilder processBuilder = new ProcessBuilder("python", "-c", code);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            kernelProcesses.put(sessionId, process);

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                session.sendMessage(new TextMessage(line));
            }

            // 等待进程结束
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                session.sendMessage(new TextMessage("执行出错，退出码: " + exitCode));
            }

            // 清理进程
            kernelProcesses.remove(sessionId);
        } catch (Exception e) {
            try {
                session.sendMessage(new TextMessage("执行错误: " + e.getMessage()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void stopKernel(String sessionId) {
        Process process = kernelProcesses.get(sessionId);
        if (process != null) {
            process.destroy();
            kernelProcesses.remove(sessionId);
        }
    }
} 
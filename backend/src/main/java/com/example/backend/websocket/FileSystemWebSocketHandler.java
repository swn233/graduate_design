package com.example.backend.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 文件系统WebSocket处理器
 * 提供实时文件操作和监控功能
 */
@Component
public class FileSystemWebSocketHandler extends TextWebSocketHandler {

    // 工作目录
    private final String WORKSPACE_DIR = System.getProperty("user.home") + "/python";
    
    // 存储所有活跃的WebSocket会话
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    // 文件系统监视器
    private WatchService watchService;
    
    // JSON处理器
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 监控线程
    private Thread watchThread;
    
    // 初始化目录和监控
    public FileSystemWebSocketHandler() {
        try {
            // 确保工作目录存在
            Files.createDirectories(Paths.get(WORKSPACE_DIR));
            
            // 初始化文件监控服务
            watchService = FileSystems.getDefault().newWatchService();
            
            // 注册目录及其子目录，监控文件变化
            registerDirectoryRecursively(Paths.get(WORKSPACE_DIR));
            
            // 启动监控线程
            startWatchThread();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 递归注册目录用于监控
     */
    private void registerDirectoryRecursively(Path directory) throws IOException {
        directory.register(watchService, 
            StandardWatchEventKinds.ENTRY_CREATE, 
            StandardWatchEventKinds.ENTRY_DELETE, 
            StandardWatchEventKinds.ENTRY_MODIFY);
        
        // 递归注册子目录
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, 
                    StandardWatchEventKinds.ENTRY_CREATE, 
                    StandardWatchEventKinds.ENTRY_DELETE, 
                    StandardWatchEventKinds.ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
     * 启动文件监控线程
     */
    private void startWatchThread() {
        watchThread = new Thread(() -> {
            try {
                while (true) {
                    WatchKey key = watchService.take();
                    Path dir = (Path) key.watchable();
                    
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        
                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                        Path filename = pathEvent.context();
                        Path fullPath = dir.resolve(filename);
                        
                        // 如果创建了新目录，也要注册它
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(fullPath)) {
                            registerDirectoryRecursively(fullPath);
                        }
                        
                        // 向所有客户端发送文件变化通知
                        Map<String, Object> notification = new HashMap<>();
                        notification.put("type", "fileSystemChange");
                        notification.put("action", kind.name());
                        notification.put("path", fullPath.toString());
                        notification.put("isDirectory", Files.isDirectory(fullPath));
                        
                        broadcastMessage(notification);
                    }
                    
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        watchThread.setDaemon(true);
        watchThread.start();
    }
    
    /**
     * 向所有连接的客户端广播消息
     */
    private void broadcastMessage(Object message) throws IOException {
        String messageJson = objectMapper.writeValueAsString(message);
        TextMessage textMessage = new TextMessage(messageJson);
        
        for (WebSocketSession session : sessions.values()) {
            if (session.isOpen()) {
                session.sendMessage(textMessage);
            }
        }
    }
    
    /**
     * 处理客户端连接建立
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        System.out.println("[FileSystemWebSocket] 新的连接: " + session.getId());
        
        // 向客户端发送初始文件列表
        sendFileTree(session);
    }
    
    /**
     * 处理客户端消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Map<String, Object> request = objectMapper.readValue(message.getPayload(), Map.class);
            String action = (String) request.get("action");
            
            switch (action) {
                case "getFileTree":
                    sendFileTree(session);
                    break;
                case "getFileContent":
                    getFileContent(session, (String) request.get("path"));
                    break;
                case "saveFile":
                    saveFile(session, (String) request.get("path"), (String) request.get("content"));
                    break;
                case "createFile":
                    createFile(session, (String) request.get("path"), (String) request.get("content"));
                    break;
                case "createDirectory":
                    createDirectory(session, (String) request.get("path"));
                    break;
                case "deleteFile":
                    deleteFile(session, (String) request.get("path"));
                    break;
                case "renameFile":
                    renameFile(session, (String) request.get("oldPath"), (String) request.get("newPath"));
                    break;
                default:
                    sendErrorMessage(session, "未知操作: " + action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorMessage(session, "处理请求时出错: " + e.getMessage());
        }
    }
    
    /**
     * 处理连接关闭
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        System.out.println("[FileSystemWebSocket] 连接关闭: " + session.getId());
    }
    
    /**
     * 发送文件树到客户端
     */
    private void sendFileTree(WebSocketSession session) throws IOException {
        Path workspacePath = Paths.get(WORKSPACE_DIR);
        Map<String, Object> fileTree = getFileTreeData(workspacePath);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "fileTree");
        response.put("data", fileTree);
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
    
    /**
     * 获取文件树结构数据
     */
    private Map<String, Object> getFileTreeData(Path path) throws IOException {
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("name", path.getFileName() != null ? path.getFileName().toString() : path.toString());
        fileInfo.put("path", path.toString());
        fileInfo.put("isDirectory", Files.isDirectory(path));
        
        if (Files.isDirectory(path)) {
            List<Map<String, Object>> children = new ArrayList<>();
            Files.list(path).forEach(childPath -> {
                try {
                    children.add(getFileTreeData(childPath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            fileInfo.put("children", children);
        }
        
        return fileInfo;
    }
    
    /**
     * 获取文件内容
     */
    private void getFileContent(WebSocketSession session, String path) throws IOException {
        Path filePath = validatePath(Paths.get(path));
        if (filePath == null) {
            sendErrorMessage(session, "无效的文件路径: " + path);
            return;
        }
        
        if (!Files.exists(filePath)) {
            sendErrorMessage(session, "文件不存在: " + path);
            return;
        }
        
        if (Files.isDirectory(filePath)) {
            sendErrorMessage(session, "不能读取目录内容: " + path);
            return;
        }
        
        String content = Files.readString(filePath);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "fileContent");
        response.put("path", path);
        response.put("content", content);
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
    
    /**
     * 保存文件内容
     */
    private void saveFile(WebSocketSession session, String path, String content) throws IOException {
        Path filePath = validatePath(Paths.get(path));
        if (filePath == null) {
            sendErrorMessage(session, "无效的文件路径: " + path);
            return;
        }
        
        if (!Files.exists(filePath)) {
            sendErrorMessage(session, "文件不存在: " + path);
            return;
        }
        
        Files.writeString(filePath, content);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "fileSaved");
        response.put("path", path);
        response.put("success", true);
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
    
    /**
     * 创建新文件
     */
    private void createFile(WebSocketSession session, String path, String content) throws IOException {
        Path filePath = validatePath(Paths.get(path));
        if (filePath == null) {
            sendErrorMessage(session, "无效的文件路径: " + path);
            return;
        }
        
        if (Files.exists(filePath)) {
            sendErrorMessage(session, "文件已存在: " + path);
            return;
        }
        
        Files.createDirectories(filePath.getParent());
        Files.writeString(filePath, content != null ? content : "");
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "fileCreated");
        response.put("path", path);
        response.put("success", true);
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
    
    /**
     * 创建新目录
     */
    private void createDirectory(WebSocketSession session, String path) throws IOException {
        Path dirPath = validatePath(Paths.get(path));
        if (dirPath == null) {
            sendErrorMessage(session, "无效的目录路径: " + path);
            return;
        }
        
        if (Files.exists(dirPath)) {
            sendErrorMessage(session, "目录已存在: " + path);
            return;
        }
        
        Files.createDirectories(dirPath);
        
        // 注册新目录用于监控
        registerDirectoryRecursively(dirPath);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "directoryCreated");
        response.put("path", path);
        response.put("success", true);
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
    
    /**
     * 删除文件或目录
     */
    private void deleteFile(WebSocketSession session, String path) throws IOException {
        Path itemPath = validatePath(Paths.get(path));
        if (itemPath == null) {
            sendErrorMessage(session, "无效的路径: " + path);
            return;
        }
        
        if (!Files.exists(itemPath)) {
            sendErrorMessage(session, "文件或目录不存在: " + path);
            return;
        }
        
        if (Files.isDirectory(itemPath)) {
            Files.walk(itemPath)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        } else {
            Files.delete(itemPath);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "fileDeleted");
        response.put("path", path);
        response.put("success", true);
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
    
    /**
     * 重命名文件或目录
     */
    private void renameFile(WebSocketSession session, String oldPath, String newPath) throws IOException {
        Path source = validatePath(Paths.get(oldPath));
        Path target = validatePath(Paths.get(newPath));
        
        if (source == null || target == null) {
            sendErrorMessage(session, "无效的路径");
            return;
        }
        
        if (!Files.exists(source)) {
            sendErrorMessage(session, "源文件不存在: " + oldPath);
            return;
        }
        
        if (Files.exists(target)) {
            sendErrorMessage(session, "目标文件已存在: " + newPath);
            return;
        }
        
        Files.move(source, target);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "fileRenamed");
        response.put("oldPath", oldPath);
        response.put("newPath", newPath);
        response.put("success", true);
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
    
    /**
     * 验证路径是否在工作目录内
     */
    private Path validatePath(Path path) {
        Path workspacePath = Paths.get(WORKSPACE_DIR);
        Path absolutePath = path.isAbsolute() ? path : workspacePath.resolve(path);
        
        if (!absolutePath.startsWith(workspacePath)) {
            return null;
        }
        
        return absolutePath;
    }
    
    /**
     * 发送错误消息
     */
    private void sendErrorMessage(WebSocketSession session, String error) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("type", "error");
        response.put("message", error);
        
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }
} 
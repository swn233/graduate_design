package com.example.backend.service;

import org.springframework.stereotype.Service;
import java.io.*;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
// import java.util.HashSet;
// import java.util.Set;

/**
 * PythonExecuteService 用于管理和执行Python代码，支持WebSocket通信。
 * 主要功能：
 * 1. 启动/停止Python解释器进程
 * 2. 向Python进程发送代码并获取输出
 * 3. 处理图片输出（如matplotlib生成的图片）
 * 4. 通过WebSocket与前端实时通信
 */
@Service
public class PythonExecuteService {
    // 存储每个会话对应的Python进程
    private final ConcurrentHashMap<String, Process> pythonProcesses = new ConcurrentHashMap<>();
    // 存储每个会话对应的进程输入流（用于写入代码）
    private final ConcurrentHashMap<String, BufferedWriter> processWriters = new ConcurrentHashMap<>();
    // 存储每个会话对应的进程输出流（用于读取输出）
    private final ConcurrentHashMap<String, BufferedReader> processReaders = new ConcurrentHashMap<>();
    // 标记每个会话是否是第一次输出（可用于欢迎信息过滤）
    private final ConcurrentHashMap<String, AtomicBoolean> isFirstOutput = new ConcurrentHashMap<>();
    // 工作目录（用户主目录下的python文件夹）
    private final String WORKSPACE_DIR = System.getProperty("user.home") + "/python";
    // 图片输出目录
    private final String IMAGE_DIR = WORKSPACE_DIR + "/images";
    // 存储每个会话已经处理过的图片文件名，避免重复发送
    // private final ConcurrentHashMap<String, Set<String>> processedImages = new ConcurrentHashMap<>();

    /**
     * 构造方法，初始化图片目录
     */
    public PythonExecuteService() {
        try {
            // 创建图片目录（如果不存在）
            Files.createDirectories(Paths.get(IMAGE_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动一个新的Python解释器进程，并为该WebSocket会话建立输入输出流
     * @param sessionId WebSocket会话ID
     * @param session   WebSocketSession对象，用于通信
     */
    public void startPythonProcess(String sessionId, WebSocketSession session) {
        System.out.println("[PythonExecuteService] 开始启动Python进程，会话ID: " + sessionId);
        try {
            // 创建ProcessBuilder，指定Python解释器路径和-i参数（交互模式）
            ProcessBuilder pb = new ProcessBuilder("/opt/homebrew/anaconda3/bin/python", "-i");
            pb.redirectErrorStream(true); // 合并标准输出和错误输出
            pb.directory(new File(WORKSPACE_DIR)); // 设置工作目录

            // 设置环境变量
            Map<String, String> env = pb.environment();
            env.put("MPLBACKEND", "Agg");  // matplotlib使用非交互式后端，防止弹窗
            env.put("PYTHONPATH", WORKSPACE_DIR);

            // 启动进程
            Process process = pb.start();
            pythonProcesses.put(sessionId, process);
            isFirstOutput.put(sessionId, new AtomicBoolean(true));
            System.out.println("[PythonExecuteService] Python进程已启动，PID: " + process.pid());

            // 获取进程的输入（写入代码）和输出（读取结果）流
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            processWriters.put(sessionId, writer);
            processReaders.put(sessionId, reader);
            System.out.println("[PythonExecuteService] 已获取进程的输入输出流");

            // 启动新线程，持续读取Python进程的输出，并通过WebSocket发送到前端
            new Thread(() -> {
                System.out.println("[PythonExecuteService] 开始读取Python进程输出");
                try {
                    String line;
                    boolean skipWelcome = true; // 是否跳过欢迎信息
                    boolean isErrorOutput = false; // 是否正在收集错误输出
                    StringBuilder errorBuffer = new StringBuilder(); // 错误信息缓冲区

                    // 持续读取输出流
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[PythonExecuteService] 收到Python输出: " + line);

                        // 跳过Python解释器的欢迎信息（如版本、help提示等）
                        if (skipWelcome) {
                            if (line.contains("Type \"help\"") || line.contains(">>>")) {
                                System.out.println("[PythonExecuteService] 跳过欢迎信息");
                                skipWelcome = false;
                                continue;
                            }
                            if (line.contains("Python") || line.contains("Anaconda") || line.contains("darwin")) {
                                System.out.println("[PythonExecuteService] 跳过系统信息: " + line);
                                continue;
                            }
                        }

                        // 检测是否为错误输出的开始（Traceback）
                        if (line.startsWith("Traceback")) {
                            isErrorOutput = true;
                            errorBuffer = new StringBuilder();
                            errorBuffer.append(line).append("\n");
                            continue;
                        }

                        // 如果正在收集错误输出，直到遇到>>>为止
                        if (isErrorOutput) {
                            errorBuffer.append(line).append("\n");
                            if (line.startsWith(">>>")) {
                                isErrorOutput = false;
                                System.out.println("[PythonExecuteService] 发送错误信息: " + errorBuffer.toString());
                                session.sendMessage(new TextMessage(errorBuffer.toString()));
                                continue;
                            }
                            continue;
                        }

                        // 去除Python输出中的>>>提示符
                        if (line.contains(">>>")) {
                            line = line.replaceAll("^>>>\\s*", "");
                            line = line.replaceAll(">>>\\s*", "");
                        }

                        // 去除Python输出中的...延续提示符
                        if (line.contains("...")) {
                            line = line.replaceAll("\\.\\.\\.\\s*", "");
                        }

                        // 过滤掉matplotlib等库的对象描述性输出（如<Figure ...>等）
                        if (line.contains("<Figure") || line.contains("matplotlib.lines.Line2D") ||
                            line.contains("Text(") || line.contains("Axes(")) {
                            System.out.println("[PythonExecuteService] 过滤掉matplotlib输出: " + line);
                            continue;
                        }

                        // 检查是否为图片文件输出（新方式，前端通过print("IMAGE_FILE:xxx.png")通知）
                        if (line.startsWith("IMAGE_FILE:")) {
                            try {
                                String filename = line.substring("IMAGE_FILE:".length()).trim();
                                Path imagePath = Paths.get(WORKSPACE_DIR, filename);
                                System.out.println("[PythonExecuteService] 检查该图片是否已经处理过: " + imagePath);
                                
                                if (Files.exists(imagePath)) {
                                    System.out.println("[PythonExecuteService] 找到图片文件");
                                    byte[] imageBytes = Files.readAllBytes(imagePath);
                                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                                    String imageType = filename.endsWith(".png") ? "png" : "jpeg";
                                    // 发送图片数据，格式：IMAGE:类型:base64字符串
                                    String imageMessage = String.format("IMAGE:%s:%s", imageType, base64Image);
                                    System.out.println("[PythonExecuteService] 发送图片数据，大小: " + imageBytes.length + " 字节");
                                    session.sendMessage(new TextMessage(imageMessage));
                                } else {
                                    System.out.println("[PythonExecuteService] 图片文件不存在: " + imagePath);
                                }
                                continue;
                            } catch (Exception e) {
                                System.err.println("[PythonExecuteService] 读取图片失败: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        // 检查是否为图片文件输出（旧方式，直接输出文件名）
                        if (line.endsWith(".png") || line.endsWith(".jpg") || line.endsWith(".jpeg")) {
                            try {
                                String filename = line.trim();
                                Path imagePath = Paths.get(WORKSPACE_DIR, filename);
                                System.out.println("[PythonExecuteService] 检查该图片是否已经处理过: " + imagePath);
                                
                                if (Files.exists(imagePath)) {
                                    System.out.println("[PythonExecuteService] 找到图片文件");
                                    byte[] imageBytes = Files.readAllBytes(imagePath);
                                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                                    String imageType = filename.endsWith(".png") ? "png" : "jpeg";
                                    String imageMessage = String.format("IMAGE:%s:%s", imageType, base64Image);
                                    System.out.println("[PythonExecuteService] 发送图片数据，大小: " + imageBytes.length + " 字节");
                                    session.sendMessage(new TextMessage(imageMessage));
                                } else {
                                    System.out.println("[PythonExecuteService] 图片文件不存在: " + imagePath);
                                }
                                continue;
                            } catch (IOException e) {
                                System.err.println("[PythonExecuteService] 读取图片失败: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        // 发送普通输出到前端
                        System.out.println("[PythonExecuteService] 发送到前端: " + line);
                        session.sendMessage(new TextMessage(line + "\n"));
                    }
                    System.out.println("[PythonExecuteService] Python进程输出流已关闭");
                } catch (IOException e) {
                    System.err.println("[PythonExecuteService] 读取输出失败: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

            // 等待Python解释器完全启动（可根据实际情况调整时间）
            Thread.sleep(1000);
            System.out.println("[PythonExecuteService] Python进程启动完成");
        } catch (Exception e) {
            System.err.println("[PythonExecuteService] 启动Python进程失败: " + e.getMessage());
            e.printStackTrace();
            try {
                session.sendMessage(new TextMessage("启动Python进程错误: " + e.getMessage() + "\n"));
            } catch (IOException ex) {
                System.err.println("[PythonExecuteService] 发送错误消息失败: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * 向指定会话的Python进程发送代码，并处理特殊情况（如plt.show()）
     * @param sessionId WebSocket会话ID
     * @param code      要执行的Python代码
     * @param session   WebSocketSession对象
     */
    public void executePythonCode(String sessionId, String code, WebSocketSession session) {
        System.out.println("[PythonExecuteService] 执行Python代码，会话ID: " + sessionId);
        System.out.println("[PythonExecuteService] 代码内容:\n" + code);
        try {
            // 获取该会话的进程输入流
            BufferedWriter writer = processWriters.get(sessionId);
            if (writer == null) {
                // 如果进程不存在，自动重启
                System.out.println("[PythonExecuteService] 未找到Python进程，重新启动");
                startPythonProcess(sessionId, session);
                writer = processWriters.get(sessionId);
            }

            // 发送代码到Python进程
            System.out.println("[PythonExecuteService] 发送代码到Python进程");

            // 检查代码中是否包含plt.show()，如有则替换为保存图片并通知前端
            String modifiedCode = code;
            if (modifiedCode.contains("plt.show()")) {
                // 智能替换plt.show()，保持原有的缩进
                StringBuilder newCode = new StringBuilder();
                String[] lines = modifiedCode.split("\n");
                
                // 生成唯一的时间戳和文件名
                String baseTimestamp = String.valueOf(System.currentTimeMillis());
                int imgCounter = 0; // 图片计数器
                
                for (String line : lines) {
                    if (line.contains("plt.show()")) {
                        // 提取当前行的缩进
                        String indentation = "";
                        for (int i = 0; i < line.length(); i++) {
                            if (line.charAt(i) == ' ' || line.charAt(i) == '\t') {
                                indentation += line.charAt(i);
                            } else {
                                break;
                            }
                        }
                        
                        // 为每个plt.show()生成唯一的时间戳和文件名
                        imgCounter++;
                        String timestamp = baseTimestamp + "_" + imgCounter;
                        String filename = "plot_" + timestamp + ".png";
                        
                        // 替换plt.show()，保持原有缩进
                        newCode.append(indentation).append("plt.savefig('").append(filename).append("')").append("\n");
                        newCode.append(indentation).append("print('IMAGE_FILE:").append(filename).append("')").append("\n");
                        newCode.append(indentation).append("plt.close()").append("\n");
                    } else {
                        newCode.append(line).append("\n");
                    }
                }
                
                modifiedCode = newCode.toString();
            }

            // 使用更可靠的方式执行多行代码
            // 将代码保存到临时文件，然后执行该文件
            String timestamp = String.valueOf(System.currentTimeMillis());
            String tempFilename = "temp_" + timestamp + ".py";
            Path tempFile = Paths.get(WORKSPACE_DIR, tempFilename);
            
            try {
                // 写入代码到临时文件
                Files.write(tempFile, modifiedCode.getBytes());
                System.out.println("[PythonExecuteService] 写入代码到临时文件: " + tempFile);
                System.out.println("[PythonExecuteService] 临时文件内容:\n" + modifiedCode);
                
                // 执行文件而不是直接发送代码
                writer.write("exec(open('" + tempFilename + "').read())\n");
                writer.flush();
                
                // 延迟一点时间后删除临时文件
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        Files.deleteIfExists(tempFile);
                        System.out.println("[PythonExecuteService] 已删除临时文件: " + tempFile);
                    } catch (Exception e) {
                        System.err.println("[PythonExecuteService] 删除临时文件失败: " + e.getMessage());
                    }
                }).start();
            } catch (IOException e) {
                System.err.println("[PythonExecuteService] 创建临时文件失败: " + e.getMessage());
                // 如果创建临时文件失败，回退到直接发送代码
                String wrappedCode = "exec('''" + modifiedCode.replace("'''", "\\'\\'\\'") + "''')\n";
                writer.write(wrappedCode);
                writer.flush();
            }

            System.out.println("[PythonExecuteService] 代码发送完成");
        } catch (Exception e) {
            System.err.println("[PythonExecuteService] 执行代码失败: " + e.getMessage());
            e.printStackTrace();
            try {
                session.sendMessage(new TextMessage("执行错误: " + e.getMessage() + "\n"));
            } catch (IOException ex) {
                System.err.println("[PythonExecuteService] 发送错误消息失败: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * 停止指定会话的Python进程，释放资源
     * @param sessionId WebSocket会话ID
     */
    public void stopPythonProcess(String sessionId) {
        System.out.println("[PythonExecuteService] 停止Python进程，会话ID: " + sessionId);
        try {
            Process process = pythonProcesses.get(sessionId);
            if (process != null) {
                BufferedWriter writer = processWriters.get(sessionId);
                if (writer != null) {
                    // 向Python进程发送exit()命令，优雅退出
                    System.out.println("[PythonExecuteService] 发送exit()命令");
                    writer.write("exit()\n");
                    writer.flush();
                }
                // 强制销毁进程
                System.out.println("[PythonExecuteService] 销毁进程");
                process.destroy();
                // 移除所有相关资源
                pythonProcesses.remove(sessionId);
                processWriters.remove(sessionId);
                processReaders.remove(sessionId);
                isFirstOutput.remove(sessionId);
                System.out.println("[PythonExecuteService] Python进程已停止");
            } else {
                System.out.println("[PythonExecuteService] 未找到要停止的Python进程");
            }
        } catch (Exception e) {
            System.err.println("[PythonExecuteService] 停止Python进程失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
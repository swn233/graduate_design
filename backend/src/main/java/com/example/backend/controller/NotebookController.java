package com.example.backend.controller;

import com.example.backend.entity.RestBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Supplier;

@Controller
@RequestMapping("/api/notebook")
public class NotebookController {

    private final String WORKSPACE_DIR = System.getProperty("user.home") + "/python";

    public NotebookController() {
        // 确保工作目录存在
        try {
            Files.createDirectories(Paths.get(WORKSPACE_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/files")
    @ResponseBody
    public RestBean<List<Map<String, Object>>> getFiles() {
        return this.messageHandle(() -> {
            try {
                List<Map<String, Object>> files = Files.walk(Paths.get(WORKSPACE_DIR))
                    .filter(path -> !path.equals(Paths.get(WORKSPACE_DIR)))
                    .map(path -> {
                        Map<String, Object> fileInfo = new HashMap<>();
                        fileInfo.put("label", path.getFileName().toString());
                        fileInfo.put("path", path.toString());
                        fileInfo.put("type", Files.isDirectory(path) ? "directory" : "file");
                        if (Files.isDirectory(path)) {
                            try {
                                fileInfo.put("children", Files.list(path)
                                    .map(p -> {
                                        Map<String, Object> child = new HashMap<>();
                                        child.put("label", p.getFileName().toString());
                                        child.put("path", p.toString());
                                        child.put("type", Files.isDirectory(p) ? "directory" : "file");
                                        return child;
                                    })
                                    .collect(Collectors.toList()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return fileInfo;
                    })
                    .collect(Collectors.toList());
                return files;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @GetMapping("/file")
    @ResponseBody
    public RestBean<String> getFileContent(@RequestParam String path) {
        return this.messageHandle(() -> {
            try {
                // 验证路径是否在工作目录内
                Path filePath = Paths.get(path);
                Path workspacePath = Paths.get(WORKSPACE_DIR);
                if (!filePath.startsWith(workspacePath)) {
                    return "访问被拒绝：文件不在工作目录内";
                }
                
                return Files.readString(filePath);
            } catch (IOException e) {
                e.printStackTrace();
                return "无法读取文件";
            }
        });
    }

    @PostMapping("/save")
    @ResponseBody
    public RestBean<String> saveNotebook(@RequestBody Map<String, Object> notebook) {
        return this.messageHandle(() -> {
            try {
                String filename = "notebook_" + System.currentTimeMillis() + ".ipynb";
                Path filePath = Paths.get(WORKSPACE_DIR, filename);
                Files.writeString(filePath, notebook.toString());
                return "保存成功";
            } catch (IOException e) {
                e.printStackTrace();
                return "保存失败";
            }
        });
    }

    private <T> RestBean<T> messageHandle(Supplier<T> action) {
        T result = action.get();
        return result != null ? RestBean.success(result) : RestBean.failure(400, "操作失败");
    }
} 
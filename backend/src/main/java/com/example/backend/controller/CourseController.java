package com.example.backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@RestController
@RequestMapping("/api/videos")
public class CourseController {

    private final Path videoStoragePath = Paths.get("videos");

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getVideoList() {
        try {
            List<Map<String, Object>> videoList = new ArrayList<>();
            Arrays.stream(videoStoragePath.toFile().listFiles((dir, name) -> name.endsWith(".mp4")))
                .forEach(file -> {
                    Map<String, Object> videoInfo = new HashMap<>();
                    videoInfo.put("id", file.getName().hashCode());
                    videoInfo.put("title", file.getName().replace(".mp4", ""));
                    videoInfo.put("duration", "00:00"); // 这里可以添加获取视频时长的逻辑
                    videoInfo.put("isFree", true); // 可以根据需要设置是否免费
                    videoList.add(videoInfo);
                });
            return ResponseEntity.ok(videoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stream/{videoId}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String videoId) {
        try {
            // 根据videoId找到对应的视频文件
            String fileName = Arrays.stream(videoStoragePath.toFile().listFiles((dir, name) -> 
                String.valueOf(name.hashCode()).equals(videoId)))
                .findFirst()
                .orElseThrow(() -> new IOException("Video not found"))
                .getName();
            
            Path videoPath = videoStoragePath.resolve(fileName);
            Resource videoResource = new UrlResource(videoPath.toUri());

            if (videoResource.exists() && videoResource.isReadable()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(videoResource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 
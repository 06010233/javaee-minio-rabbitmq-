// 完整文件: src/main/java/com/example/flight/controller/FileController.java

package com.example.flight.controller;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "file";
            String fileExtension = originalFilename.contains(".") ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String presignedUrl = generatePresignedUrl(uniqueFileName);

            // --- ★★★ 核心修改点在这里 ★★★ ---
            // 构建前端需要的返回格式: { "data": { "url": "...", "name": "..." } }
            Map<String, String> fileData = new HashMap<>();
            fileData.put("url", presignedUrl);
            fileData.put("name", originalFilename); // 前端需要的是原始文件名，而不是UUID

            Map<String, Object> response = new HashMap<>();
            response.put("data", fileData); // 将 fileData 包裹在 "data" 对象中

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    // 你的预览URL生成逻辑非常棒，保持不变
    @GetMapping("/preview/{fileName}")
    public ResponseEntity<Void> getPreviewUrl(@PathVariable String fileName) {
        try {
            String url = generatePresignedUrl(fileName);
            // 使用 302 重定向
            return ResponseEntity.status(302).header("Location", url).build();
        } catch (Exception e) {
            e.printStackTrace();
            // 注意：这里返回一个空的500响应体，或者一个JSON错误对象
            return ResponseEntity.status(500).build();
        }
    }

    private String generatePresignedUrl(String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(fileName)
                        .expiry(1, TimeUnit.HOURS)
                        .build()
        );
    }
}
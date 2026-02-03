// 文件路径: src/main/java/com/example/flight/service/AirlineService.java
package com.example.flight.service;

import com.example.flight.entity.Airline;
import com.example.flight.entity.File;
import com.example.flight.repository.AirlineRepository;
import com.example.flight.repository.FileRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs; // ★ 新增：导入 RemoveObjectArgs
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL; // ★ 新增：导入 URL
import java.nio.file.Paths; // ★ 新增：导入 Paths
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final FileRepository fileRepository;
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Autowired
    public AirlineService(AirlineRepository airlineRepository, FileRepository fileRepository, MinioClient minioClient) {
        this.airlineRepository = airlineRepository;
        this.fileRepository = fileRepository;
        this.minioClient = minioClient;
    }

    // --- 您原有的、工作正常的方法，全部保留 ---
    public List<Airline> findAll() {
        return airlineRepository.findAll();
    }

    public Optional<Airline> findById(Integer id) {
        return airlineRepository.findById(id);
    }

    public Optional<Airline> findByAirlineCode(String airlineCode) {
        return airlineRepository.findByAirlineCode(airlineCode);
    }

    public Airline saveAirline(Airline airline) {
        if (airline.getCreatedAt() == null) {
            airline.setCreatedAt(LocalDateTime.now());
        }
        return airlineRepository.save(airline);
    }

    public boolean existsByAirlineCode(String airlineCode) {
        return airlineRepository.existsByAirlineCode(airlineCode);
    }

    public void deleteAirline(Integer id) {
        airlineRepository.deleteById(id);
    }

    // --- 您已有的、工作正常的上传方法，完全保留 ---
    @Transactional
    public void uploadAndAssociateFile(Integer airlineId, MultipartFile multipartFile) throws Exception {
        Airline airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("找不到ID为 " + airlineId + " 的航空公司"));

        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(uniqueFileName)
                        .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                        .contentType(multipartFile.getContentType())
                        .build()
        );

        GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(uniqueFileName)
                .expiry(7, TimeUnit.DAYS)
                .build();

        String fileUrl = minioClient.getPresignedObjectUrl(args);

        File newFile = new File();
        newFile.setFileName(originalFilename);
        newFile.setFileUrl(fileUrl);
        newFile.setUploadDate(LocalDateTime.now());
        newFile.setAirline(airline);

        fileRepository.save(newFile);
    }


    // --- ★★★ 新增的核心方法：实现文件删除逻辑 ★★★ ---

    /**
     * 从 MinIO 和数据库中删除一个文件.
     *
     * @param fileId 要删除的文件的ID
     * @throws Exception 如果删除过程中发生错误
     */
    @Transactional
    public void deleteFile(Integer fileId) throws Exception {
        // 1. 首先从数据库中找到这个文件记录
        File fileToDelete = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("数据库中找不到ID为 " + fileId + " 的文件记录"));

        // 2. 从文件的 URL 中解析出它在 MinIO 中的对象名称 (objectName)
        URL url = new URL(fileToDelete.getFileUrl());
        // 从路径中提取文件名，例如从 "/flight-files/uuid-name.png" 中提取 "uuid-name.png"
        String objectName = Paths.get(url.getPath()).getFileName().toString();

        // 3. 从 MinIO 中删除该文件对象
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );

        // 4. 从数据库中删除该文件的记录
        fileRepository.deleteById(fileId);
    }
}
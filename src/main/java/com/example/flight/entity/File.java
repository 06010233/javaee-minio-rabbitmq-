// 文件路径: src/main/java/com/example/flight/entity/File.java
package com.example.flight.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "files") // 明确指定映射到我们刚刚创建的 "files" 表
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 512)
    private String fileUrl;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    // ★ 核心关系映射：多对一
    // 多个文件(File)可以属于一个航空公司(Airline)
    @ManyToOne(fetch = FetchType.LAZY) // 使用LAZY懒加载，提高性能
    @JoinColumn(name = "airline_id", nullable = false) // 指定外键列是 "airline_id"
    @JsonBackReference // 防止在序列化为JSON时产生无限循环
    private Airline airline;

    // --- Getters and Setters ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }
}
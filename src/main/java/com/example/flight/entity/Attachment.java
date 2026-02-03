package com.example.flight.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName; // 原始文件名

    // ★★★ 核心修复：使用 @Column(length = 1024) 或 @Lob 来解决字段长度不足的问题 ★★★
    // 方案一：指定一个足够大的长度，例如 1024。对于大多数 URL 来说都够了。
    @Column(length = 1024)
    private String filePath; // 文件URL

    // 方案二（更通用）：使用 @Lob (Large Object)，这会将其映射为数据库的 TEXT 类型，几乎没有长度限制。
    // 如果方案一还不够，就使用这个。
    // @Lob
    // private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    @JsonBackReference
    private Announcement announcement;

    // --- Getters and Setters (无需修改) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public Announcement getAnnouncement() { return announcement; }
    public void setAnnouncement(Announcement announcement) { this.announcement = announcement; }
}
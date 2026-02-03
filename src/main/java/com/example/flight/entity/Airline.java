// 文件路径: src/main/java/com/example/flight/entity/Airline.java
package com.example.flight.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List; // ★ 需要导入 List

@Entity
@Table(name = "airlines")
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer airlineId;

    @Column(name = "airline_code", length = 2, unique = true, nullable = false)
    private String airlineCode;

    @Column(name = "airline_name", length = 50, nullable = false)
    private String airlineName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "website", length = 100)
    private String website;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // --- ★★★ 新增的核心关系映射 ★★★ ---
    /**
     * 定义与 File 实体的一对多关系.
     * 一个航空公司 (Airline) 可以拥有多个文件 (File).
     * mappedBy = "airline": 指定这个关系是由 File 实体中的 "airline" 字段来维护的.
     * cascade = CascadeType.ALL: 级联操作，当保存/更新/删除航空公司时，也会对其关联的文件执行相应操作.
     * orphanRemoval = true: 当一个文件从这个列表中被移除时，它也会从数据库中被删除.
     */
    @OneToMany(mappedBy = "airline", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // 防止在序列化为JSON时，因双向引用导致无限循环
    private List<File> files;


    // --- Getters and Setters ---

    public Integer getAirlineId() {
        return airlineId;
    }

    public void setAirlineId(Integer airlineId) {
        this.airlineId = airlineId;
    }

    public String getAirlineCode() {
        return airlineCode;
    }

    public void setAirlineCode(String airlineCode) {
        this.airlineCode = airlineCode;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // --- ★ 新增 files 字段的 Getter and Setter ★ ---
    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
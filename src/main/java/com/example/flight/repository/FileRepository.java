// 文件路径: src/main/java/com/example/flight/repository/FileRepository.java
package com.example.flight.repository;

import com.example.flight.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * File 实体的数据库操作接口.
 * JpaRepository 提供了所有基础的CRUD (创建, 读取, 更新, 删除) 方法.
 */
@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    // JpaRepository<File, Integer>
    // 第一个参数: 它管理的实体类 (File)
    // 第二个参数: 实体类主键的类型 (Integer)

    // 目前不需要自定义查询方法，JpaRepository 提供的默认方法已足够使用。
}

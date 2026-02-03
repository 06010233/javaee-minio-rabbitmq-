// src/main/java/com/example/flight/repository/AnnouncementRepository.java

package com.example.flight.repository;

import com.example.flight.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    // Spring Data JPA 会自动为我们实现所有基础的增删改查方法
}
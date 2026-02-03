// 最终的、完整正确的文件: src/main/java/com/example/flight/service/AnnouncementService.java

package com.example.flight.service;

import com.example.flight.entity.Announcement;
import com.example.flight.entity.Attachment;
import com.example.flight.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }
    
    // createAnnouncement 方法保持不变，它是正确的
    @Transactional
    public Announcement createAnnouncement(Announcement announcement) {
        // 先保存 Announcement 对象，使其获得 ID
        Announcement tempAnnouncement = new Announcement();
        tempAnnouncement.setTitle(announcement.getTitle());
        tempAnnouncement.setContent(announcement.getContent());

        Announcement savedAnnouncement = announcementRepository.save(tempAnnouncement);
        
        // 关键：设置每个附件与公告的双向关联
        if (announcement.getAttachments() != null) {
            for (Attachment attachment : announcement.getAttachments()) {
                attachment.setAnnouncement(savedAnnouncement);
            }
            savedAnnouncement.getAttachments().addAll(announcement.getAttachments());
        }
        // 再次保存，此时附件会级联保存并关联外键
        return announcementRepository.save(savedAnnouncement);
    }

    // ★★★ 核心修复：重写 updateAnnouncement 方法 ★★★
    @Transactional
    public Optional<Announcement> updateAnnouncement(Long id, Announcement announcementDetails) {
        // 1. 先通过 ID 查找数据库中已存在的公告
        Optional<Announcement> existingAnnouncementOptional = announcementRepository.findById(id);

        // 如果找不到，直接返回空 Optional
        if (existingAnnouncementOptional.isEmpty()) {
            return Optional.empty();
        }

        // 2. 获取持久化状态的公告实体
        Announcement existingAnnouncement = existingAnnouncementOptional.get();

        // 3. 更新公告的标题和内容
        existingAnnouncement.setTitle(announcementDetails.getTitle());
        existingAnnouncement.setContent(announcementDetails.getContent());

        // 4. 以安全的方式更新附件列表
        // 首先，清空当前在数据库中与该公告关联的附件列表
        existingAnnouncement.getAttachments().clear();

        // 然后，如果前端传来了新的附件列表，则将它们添加进去
        if (announcementDetails.getAttachments() != null) {
            for (Attachment newAttachment : announcementDetails.getAttachments()) {
                // 关键：必须重新建立附件和持久化实体的关联
                newAttachment.setAnnouncement(existingAnnouncement);
                existingAnnouncement.getAttachments().add(newAttachment);
            }
        }

        // 5. 保存更新后的公告。
        // 由于 existingAnnouncement 是持久化状态的，JPA 会自动处理更新、删除旧附件、插入新附件的操作。
        Announcement updatedAnnouncement = announcementRepository.save(existingAnnouncement);
        return Optional.of(updatedAnnouncement);
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }
}
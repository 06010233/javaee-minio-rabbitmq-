// 文件路径: src/main/java/com/example/flight/controller/AirlineController.java
package com.example.flight.controller;

import com.example.flight.entity.Airline;
import com.example.flight.service.AirlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airlines")
public class AirlineController {

    private final AirlineService airlineService;

    @Autowired
    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }

    // --- 您原有的、用于管理航空公司的 CRUD 方法，全部保留 ---

    @GetMapping
    public List<Airline> getAllAirlines() {
        return airlineService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Airline> getAirlineById(@PathVariable Integer id) {
        Optional<Airline> airline = airlineService.findById(id);
        return airline.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{airlineCode}")
    public ResponseEntity<Airline> getAirlineByCode(@PathVariable String airlineCode) {
        Optional<Airline> airline = airlineService.findByAirlineCode(airlineCode);
        return airline.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createAirline(@RequestBody Airline airline) {
        if (airlineService.existsByAirlineCode(airline.getAirlineCode())) {
            return ResponseEntity.badRequest().body("Airline with this code already exists");
        }
        airline.setCreatedAt(LocalDateTime.now());
        Airline savedAirline = airlineService.saveAirline(airline);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAirline);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAirline(@PathVariable Integer id, @RequestBody Airline airline) {
        Optional<Airline> existingAirline = airlineService.findById(id);
        if (existingAirline.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!existingAirline.get().getAirlineCode().equals(airline.getAirlineCode()) &&
            airlineService.existsByAirlineCode(airline.getAirlineCode())) {
            return ResponseEntity.badRequest().body("Airline with this code already exists");
        }
        airline.setAirlineId(id);
        airline.setCreatedAt(existingAirline.get().getCreatedAt());
        return ResponseEntity.ok(airlineService.saveAirline(airline));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirline(@PathVariable Integer id) {
        if (!airlineService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        airlineService.deleteAirline(id);
        return ResponseEntity.noContent().build();
    }


    // --- 新增的、用于处理文件上传和删除的方法 ---

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAirlineFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("airlineId") Integer airlineId) {
        
        try {
            airlineService.uploadAndAssociateFile(airlineId, file);
            return ResponseEntity.ok().body("文件上传成功并已关联到航空公司");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("文件上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<?> deleteAirlineFile(@PathVariable Integer fileId) {
        try {
            airlineService.deleteFile(fileId);
            return ResponseEntity.ok().body("文件删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("文件删除失败: " + e.getMessage());
        }
    }
}
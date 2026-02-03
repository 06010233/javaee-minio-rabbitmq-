// 文件路径: src/main/java/com/example/flight/controller/PassengerController.java
package com.example.flight.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.flight.entity.Passenger;
import com.example.flight.service.PassengerService;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    @Autowired
    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }
    
    @GetMapping
    public List<Passenger> getAllPassengers() { 
        return passengerService.findAll(); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Passenger> getPassengerById(@PathVariable Integer id) { 
        return passengerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); 
    }

    @GetMapping("/idcard/{idCard}")
    public ResponseEntity<Passenger> getPassengerByIdCard(@PathVariable String idCard) { 
        return passengerService.findByIdCard(idCard)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); 
    }

    @GetMapping("/membership/{level}")
    public List<Passenger> getPassengersByMembershipLevel(@PathVariable Passenger.MembershipLevel level) { 
        // 这一行现在不会报错了，因为我们在Service里补上了方法
        return passengerService.findByMembershipLevel(level); 
    }

    @PostMapping
    public ResponseEntity<?> createPassenger(@RequestBody Passenger passenger) { 
        if (passengerService.findByIdCard(passenger.getIdCard()).isPresent()) { 
            return ResponseEntity.badRequest().body("该身份证号已被注册"); 
        } 
        if (passengerService.findByPhone(passenger.getPhone()).isPresent()) { 
            return ResponseEntity.badRequest().body("该联系电话已被注册"); 
        } 
        if (passenger.getEmail() != null && !passenger.getEmail().trim().isEmpty()) { 
            if (passengerService.findByEmail(passenger.getEmail()).isPresent()) { 
                return ResponseEntity.badRequest().body("该电子邮箱已被注册"); 
            } 
        } 
        passenger.setCreatedAt(LocalDateTime.now()); 
        Passenger savedPassenger = passengerService.savePassenger(passenger); 
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPassenger); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable Integer id) { 
        if (!passengerService.findById(id).isPresent()) { 
            return ResponseEntity.notFound().build(); 
        } 
        passengerService.deletePassenger(id); 
        return ResponseEntity.noContent().build(); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePassenger(@PathVariable Integer id, @RequestBody Passenger passengerUpdateData) {
        Optional<Passenger> existingPassengerOpt = passengerService.findById(id);
        
        if (existingPassengerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Passenger existingPassenger = existingPassengerOpt.get();

        // 校验电话号码
        String newPhone = passengerUpdateData.getPhone();
        if (newPhone != null && !newPhone.equals(existingPassenger.getPhone())) {
            Optional<Passenger> passengerWithSamePhone = passengerService.findByPhone(newPhone);
            if (passengerWithSamePhone.isPresent() && !passengerWithSamePhone.get().getPassengerId().equals(id)) {
                return ResponseEntity.badRequest().body("该联系电话已被其他用户注册");
            }
            existingPassenger.setPhone(newPhone);
        }

        // 校验电子邮箱
        String newEmail = passengerUpdateData.getEmail();
        if (newEmail != null && !newEmail.equals(existingPassenger.getEmail())) {
            if (!newEmail.trim().isEmpty()) {
                 Optional<Passenger> passengerWithSameEmail = passengerService.findByEmail(newEmail);
                if (passengerWithSameEmail.isPresent() && !passengerWithSameEmail.get().getPassengerId().equals(id)) {
                    return ResponseEntity.badRequest().body("该电子邮箱已被其他用户注册");
                }
            }
            existingPassenger.setEmail(newEmail);
        }
        
        // 更新其他非唯一性字段
        existingPassenger.setEducation(passengerUpdateData.getEducation());
        existingPassenger.setOccupation(passengerUpdateData.getOccupation());
        
        // 保存更新后的实体
        return ResponseEntity.ok(passengerService.savePassenger(existingPassenger));
    }
}
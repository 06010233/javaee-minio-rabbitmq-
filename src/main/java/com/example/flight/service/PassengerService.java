// 文件路径: src/main/java/com/example/flight/service/PassengerService.java
package com.example.flight.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flight.entity.Passenger;
import com.example.flight.repository.PassengerRepository;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;

    @Autowired
    public PassengerService(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    public List<Passenger> findAll() { return passengerRepository.findAll(); }
    public Optional<Passenger> findById(Integer id) { return passengerRepository.findById(id); }
    public Optional<Passenger> findByIdCard(String idCard) { return passengerRepository.findByIdCard(idCard); }
    public Optional<Passenger> findByPhone(String phone) { return passengerRepository.findByPhone(phone); }
    public Optional<Passenger> findByEmail(String email) { return passengerRepository.findByEmail(email); }
    
    // ★★★ 补上了这个缺失的方法，解决了 Controller 的报错 ★★★
    public List<Passenger> findByMembershipLevel(Passenger.MembershipLevel level) {
        return passengerRepository.findByMembershipLevel(level);
    }

    public Passenger savePassenger(Passenger passenger) {
        if (passenger.getCreatedAt() == null) {
            passenger.setCreatedAt(LocalDateTime.now());
        }
        return passengerRepository.save(passenger);
    }

    public void deletePassenger(Integer id) {
        passengerRepository.deleteById(id);
    }

    /**
     * 获取 or 创建乘客
     */
    @Transactional
    public Passenger createOrGetPassenger(String name, String idCard, String phone, String email) {
        Optional<Passenger> existingOpt = passengerRepository.findByIdCard(idCard);

        if (existingOpt.isPresent()) {
            Passenger existing = existingOpt.get();
            // 可选：校验姓名是否匹配
            if (!existing.getName().equals(name)) {
                // 这里为了流程顺畅，如果名字不对，可以选择抛错，或者仅仅打印日志
                // throw new RuntimeException("身份证号 " + idCard + " 已被注册，但姓名不匹配");
            }
            return existing;
        } else {
            // 创建新乘客
            Passenger newPassenger = new Passenger();
            newPassenger.setName(name);
            newPassenger.setIdCard(idCard);
            newPassenger.setPhone(phone);
            newPassenger.setEmail(email);
            
            // 简单的身份证解析
            try {
                if (idCard.length() == 18) {
                    String birthStr = idCard.substring(6, 14);
                    LocalDate birthDate = LocalDate.parse(birthStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
                    newPassenger.setBirthDate(birthDate);
                    
                    int genderCode = Integer.parseInt(idCard.substring(16, 17));
                    newPassenger.setGender(genderCode % 2 != 0 ? Passenger.Gender.MALE : Passenger.Gender.FEMALE);
                } else {
                    newPassenger.setBirthDate(LocalDate.of(2000, 1, 1)); 
                    newPassenger.setGender(Passenger.Gender.OTHER);
                }
            } catch (Exception e) {
                newPassenger.setBirthDate(LocalDate.of(2000, 1, 1));
                newPassenger.setGender(Passenger.Gender.OTHER);
            }

            newPassenger.setCreatedAt(LocalDateTime.now());
            newPassenger.setMembershipLevel(Passenger.MembershipLevel.REGULAR);
            return passengerRepository.save(newPassenger);
        }
    }
}
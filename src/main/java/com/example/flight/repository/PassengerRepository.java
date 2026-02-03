package com.example.flight.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flight.entity.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    Optional<Passenger> findByIdCard(String idCard);
    List<Passenger> findByMembershipLevel(Passenger.MembershipLevel level);
    boolean existsByIdCard(String idCard);

    // --- 核心修改：将 existsBy... 改为 findBy... 以便获取ID进行比较 ---
    Optional<Passenger> findByPhone(String phone);
    Optional<Passenger> findByEmail(String email);
}
// repository/SystemUserRepository.java
package com.example.flight.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.flight.entity.Passenger; // 引入 Passenger
import com.example.flight.entity.SystemUser;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Integer> {
    
    // ... (原有方法保持不变) ...
    @Query("SELECT su FROM SystemUser su LEFT JOIN FETCH su.passenger WHERE su.username = :username")
    Optional<SystemUser> findByUsernameWithPassenger(@Param("username") String username);
    Optional<SystemUser> findByUsername(String username);
    List<SystemUser> findByRole(SystemUser.UserRole role);
    boolean existsByUsername(String username);

    // --- ★★★ 新增方法 ★★★ ---
    boolean existsByPassenger(Passenger passenger);
}
package com.example.flight.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.flight.entity.Flight;
import com.example.flight.entity.FlightSeatAvailability;

@Repository
// 确认主键类型为 Integer
public interface FlightSeatAvailabilityRepository extends JpaRepository<FlightSeatAvailability, Integer> {
    
    // 这个方法可以保留，但我们更推荐使用 findById
    Optional<FlightSeatAvailability> findByFlight(Flight flight);
    
    @Query("SELECT fsa FROM FlightSeatAvailability fsa WHERE fsa.availableBusinessSeats > 0")
    List<FlightSeatAvailability> findWithAvailableBusinessSeats();
    
    @Query("SELECT fsa FROM FlightSeatAvailability fsa WHERE fsa.availableEconomySeats > 0")
    List<FlightSeatAvailability> findWithAvailableEconomySeats();
    
    // 下面的 Modifying 查询可以保留，但更好的实践是在 Service 层通过事务来处理更新
    @Modifying
    @Query("UPDATE FlightSeatAvailability fsa SET fsa.availableBusinessSeats = ?2 WHERE fsa.flight = ?1")
    void updateBusinessSeats(Flight flight, Integer availableSeats);
    
    @Modifying
    @Query("UPDATE FlightSeatAvailability fsa SET fsa.availableEconomySeats = ?2 WHERE fsa.flight = ?1")
    void updateEconomySeats(Flight flight, Integer availableSeats);
}
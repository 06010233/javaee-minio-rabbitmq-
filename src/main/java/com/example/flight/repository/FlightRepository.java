// 文件路径: src/main/java/com/example/flight/repository/FlightRepository.java

package com.example.flight.repository;

import com.example.flight.entity.Airline;
import com.example.flight.entity.Airport;
import com.example.flight.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    @Query("SELECT f FROM Flight f " +
           "JOIN FETCH f.airline " +
           "JOIN FETCH f.departureAirport " +
           "JOIN FETCH f.arrivalAirport " +
           "WHERE f.flightId = :id")
    Optional<Flight> findByIdWithDetails(@Param("id") Integer id);
    
    // --- 基础查询 ---
    List<Flight> findByAirline(Airline airline);
    List<Flight> findByDepartureAirport(Airport departureAirport);
    List<Flight> findByArrivalAirport(Airport arrivalAirport);
    List<Flight> findByFlightNumber(String flightNumber);
    
    @Modifying
    long deleteByDepartureTimeBefore(LocalDateTime cutoffDate);

    // --- ★★★ 核心修改 1：搜索专用 (排除已取消) ★★★ ---
    @Query("SELECT f FROM Flight f WHERE " +
           "f.departureAirport.airportCode = :depCode AND " +
           "f.arrivalAirport.airportCode = :arrCode AND " +
           "f.departureTime BETWEEN :start AND :end AND " +
           "f.status != 'CANCELLED'")
    List<Flight> searchAvailableFlights(
        @Param("depCode") String depCode,
        @Param("arrCode") String arrCode,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    // --- ★★★ 修复点：补回丢失的方法 (FlightRouteService 需要用到它) ★★★ ---
    List<Flight> findByDepartureAirportAndArrivalAirportAndDepartureTimeBetween(
        Airport departureAirport, Airport arrivalAirport, 
        LocalDateTime start, LocalDateTime end);

    // --- 辅助方法 ---
    List<Flight> findByFlightNumberAndDepartureAirportAndArrivalAirportAndDepartureTimeAfter(
            String flightNumber, Airport departureAirport, Airport arrivalAirport, LocalDateTime startDate
    );

    // --- ★★★ 核心修改 2：查找某天某航班号的记录 (用于防重) ★★★ ---
    @Query("SELECT f FROM Flight f WHERE f.flightNumber = :flightNumber " +
           "AND f.departureTime BETWEEN :start AND :end")
    Optional<Flight> findExistingFlightOnDay(
        @Param("flightNumber") String flightNumber, 
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end
    );
    
    // 状态查询
    List<Flight> findByStatusIn(Collection<Flight.FlightStatus> statuses);

    // 日历查询
   @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :start AND :end " +
           "AND f.departureTime > CURRENT_TIMESTAMP " +
           "AND f.status IN :statuses")
    List<Flight> findByDepartureTimeBetweenAndStatusIn(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end, 
        @Param("statuses") Collection<Flight.FlightStatus> statuses
    );
    
    // 查找最后一次航班（用于排班）
    Optional<Flight> findTopByFlightNumberAndDepartureAirportAndArrivalAirportOrderByDepartureTimeDesc(
            String flightNumber, Airport departureAirport, Airport arrivalAirport
    );
}
package com.example.flight.repository;

import com.example.flight.entity.FlightRoute;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRouteRepository extends JpaRepository<FlightRoute, Integer> {
    
    // 查找所有启用的航线用于生成航班
    List<FlightRoute> findByActive(boolean active);

    // 重写 findAll 方法，在查询时总是立刻加载关联对象，避免N+1问题
    @Override
    @EntityGraph(attributePaths = {"airline", "departureAirport", "arrivalAirport"})
    List<FlightRoute> findAll();
}
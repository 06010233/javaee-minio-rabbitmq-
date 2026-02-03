// controller/StatisticsController.java
package com.example.flight.controller;

import com.example.flight.controller.dto.AirlineRevenueDTO;
import com.example.flight.controller.dto.MonthlyRevenueDTO;
import com.example.flight.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/monthly-revenue")
    public ResponseEntity<List<MonthlyRevenueDTO>> getMonthlyRevenue() {
        return ResponseEntity.ok(statisticsService.getMonthlyRevenue());
    }

    @GetMapping("/airline-revenue")
    public ResponseEntity<List<AirlineRevenueDTO>> getAirlineRevenue() {
        return ResponseEntity.ok(statisticsService.getAirlineRevenue());
    }
}
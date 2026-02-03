package com.example.flight.controller;

import com.example.flight.entity.Flight;
import com.example.flight.entity.FlightRoute;
import com.example.flight.service.FlightRouteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // ★ 1. 新增导入

@RestController
@RequestMapping("/api/flight-routes") // 你的基础路径是 /api/flight-routes
public class FlightRouteController {

    private final FlightRouteService flightRouteService;

    public FlightRouteController(FlightRouteService flightRouteService) {
        this.flightRouteService = flightRouteService;
    }

    @GetMapping
    public List<FlightRoute> getAllFlightRoutes() {
        return flightRouteService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightRoute> getFlightRouteById(@PathVariable Integer id) {
        return flightRouteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/future-flights")
    public List<Flight> getFutureFlights(@PathVariable Integer id) {
        return flightRouteService.getFutureFlightsForRoute(id);
    }

    @PostMapping
    public ResponseEntity<FlightRoute> createFlightRoute(@RequestBody FlightRoute flightRoute) {
        FlightRoute savedRoute = flightRouteService.create(flightRoute);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoute);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlightRoute> updateFlightRoute(@PathVariable Integer id, @RequestBody FlightRoute flightRouteDetails) {
        FlightRoute updatedRoute = flightRouteService.update(id, flightRouteDetails);
        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlightRoute(@PathVariable Integer id) {
        if (!flightRouteService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        flightRouteService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // ★★★ 新增API端点：为航线发布航班计划 ★★★
    /**
     * @param id 航线模板的ID
     * @param payload JSON对象，应包含一个 "days" 字段，例如: { "days": 7 }
     * @return 返回操作结果
     */
    @PostMapping("/{id}/schedule")
    public ResponseEntity<?> scheduleFlights(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> payload) {
        
        Integer days = payload.get("days");
        if (days == null || days <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message", "'days' parameter is required and must be positive."));
        }
        
        try {
            List<Flight> scheduledFlights = flightRouteService.scheduleFlightsForRoute(id, days);
            String message = scheduledFlights.isEmpty() 
                ? "No new flights were scheduled. They might already exist for the specified days."
                : "Successfully scheduled " + scheduledFlights.size() + " new flights.";
            
            return ResponseEntity.ok(Map.of(
                "message", message,
                "count", scheduledFlights.size()
            ));
        } catch (Exception e) {
            // 捕获可能发生的异常，例如航线不存在或航线为停用状态
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}
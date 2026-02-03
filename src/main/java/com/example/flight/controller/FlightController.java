// 文件路径: src/main/java/com/example/flight/controller/FlightController.java
package com.example.flight.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.flight.entity.Flight;
import com.example.flight.entity.FlightSeatAvailability;
import com.example.flight.service.FlightSeatAvailabilityService;
import com.example.flight.service.FlightService;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;
    private final FlightSeatAvailabilityService availabilityService;

    @Autowired
    public FlightController(FlightService flightService, 
                           FlightSeatAvailabilityService availabilityService) {
        this.flightService = flightService;
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public List<Flight> getAllFlights() { 
        return flightService.findAll(); 
    }

    @GetMapping("/calendar")
    public List<Flight> getCalendarFlights(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        return flightService.getVisibleFlightsForCalendar(start, end);
    }
    
    @GetMapping("/search")
    public List<Flight> searchFlights( 
            @RequestParam String departureCode, 
            @RequestParam String arrivalCode, 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) { 
        return flightService.searchFlights(departureCode, arrivalCode, date); 
    }
    
    @GetMapping("/{id}/availability")
    public ResponseEntity<FlightSeatAvailability> getFlightAvailability(@PathVariable Integer id) { 
        return availabilityService.findByFlightId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Integer id) { 
        return flightService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); 
    }

    // ★★★ 核心修复：安全的更新逻辑 ★★★
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFlight(@PathVariable Integer id, @RequestBody Flight flightUpdate) { 
        Optional<Flight> existingFlightOpt = flightService.findById(id);
        
        if (existingFlightOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Flight existingFlight = existingFlightOpt.get();

        // 只更新前端允许修改的字段，保护 flightRoute 不被清空
        if (flightUpdate.getDepartureTime() != null) existingFlight.setDepartureTime(flightUpdate.getDepartureTime());
        if (flightUpdate.getArrivalTime() != null) existingFlight.setArrivalTime(flightUpdate.getArrivalTime());
        if (flightUpdate.getStatus() != null) existingFlight.setStatus(flightUpdate.getStatus());
        if (flightUpdate.getAircraftModel() != null) existingFlight.setAircraftModel(flightUpdate.getAircraftModel());
        
        // 保存修改后的实体
        Flight savedFlight = flightService.saveFlight(existingFlight);
        return ResponseEntity.ok(savedFlight); 
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateFlightStatus( @PathVariable Integer id, @RequestParam Flight.FlightStatus status) { 
        Flight updatedFlight = flightService.updateFlightStatus(id, status); 
        return updatedFlight != null ? ResponseEntity.ok().build() : ResponseEntity.notFound().build(); 
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Integer id) { 
        if (flightService.findById(id).isEmpty()) { 
            return ResponseEntity.notFound().build(); 
        } 
        availabilityService.deleteByFlightId(id); 
        flightService.deleteFlight(id); 
        return ResponseEntity.noContent().build(); 
    }

    @PostMapping
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flightRequest) {
        Flight savedFlight = flightService.createFlight(flightRequest);
        availabilityService.initializeAvailability(savedFlight);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFlight);
    }
}
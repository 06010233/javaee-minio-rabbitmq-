package com.example.flight.controller;

import com.example.flight.entity.Airport;
import com.example.flight.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService airportService;

    @Autowired
    public AirportController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping
    public List<Airport> getAllAirports() {
        return airportService.findAll();
    }

    @GetMapping("/{code}")
    public ResponseEntity<Airport> getAirportByCode(@PathVariable String code) {
        Optional<Airport> airport = airportService.findById(code);
        return airport.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/country/{country}")
    public List<Airport> getAirportsByCountry(@PathVariable String country) {
        return airportService.findByCountry(country);
    }

    @GetMapping("/city/{city}")
    public List<Airport> getAirportsByCity(@PathVariable String city) {
        return airportService.findByCity(city);
    }

    @PostMapping
    public ResponseEntity<Airport> createAirport(@RequestBody Airport airport) {
        // Check if airport already exists
        if (airportService.findById(airport.getAirportCode()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        airport.setCreatedAt(LocalDateTime.now());
        Airport savedAirport = airportService.saveAirport(airport);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAirport);
    }

    @PutMapping("/{code}")
    public ResponseEntity<Airport> updateAirport(@PathVariable String code, @RequestBody Airport airport) {
        Optional<Airport> existingAirport = airportService.findById(code);
        
        if (existingAirport.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Ensure the code matches the path variable
        airport.setAirportCode(code);
        airport.setCreatedAt(existingAirport.get().getCreatedAt());
        return ResponseEntity.ok(airportService.saveAirport(airport));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteAirport(@PathVariable String code) {
        if (!airportService.findById(code).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Note: In a real application, you might want to check for references before deleting
        airportService.deleteAirport(code);
        return ResponseEntity.noContent().build();
    }
}

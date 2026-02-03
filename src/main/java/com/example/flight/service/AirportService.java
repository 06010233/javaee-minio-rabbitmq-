package com.example.flight.service;

import com.example.flight.entity.Airport;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AirportService {

    private final com.example.flight.repository.AirportRepository airportRepository;

    public AirportService(com.example.flight.repository.AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    public List<Airport> findAll() {
        return airportRepository.findAll();
    }

    public Optional<Airport> findById(String code) {
        return airportRepository.findById(code);
    }

    public List<Airport> findByCountry(String country) {
        return airportRepository.findByCountry(country);
    }

    public List<Airport> findByCity(String city) {
        return airportRepository.findByCity(city);
    }

    public Airport saveAirport(Airport airport) {
        if (airport.getCreatedAt() == null) {
            airport.setCreatedAt(LocalDateTime.now());
        }
        return airportRepository.save(airport);
    }
    
    // 添加缺失的deleteAirport方法
    public void deleteAirport(String code) {
        airportRepository.deleteById(code);
    }
}

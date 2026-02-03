package com.example.flight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.flight.entity.Airport;

@Repository
public interface AirportRepository extends JpaRepository<Airport, String> {
    List<Airport> findByCountry(String country);
    List<Airport> findByCity(String city);
}

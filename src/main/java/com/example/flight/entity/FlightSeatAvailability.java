package com.example.flight.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "flight_seat_availability")
public class FlightSeatAvailability {

    // 主键与 flight 表的 flight_id 关联
    @Id
    @Column(name = "flight_id")
    private Integer flightId;

    // 使用 @MapsId 和 @OneToOne 实现共享主键
    @OneToOne
    @MapsId
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @Column(name = "available_business_seats", nullable = false)
    private Integer availableBusinessSeats;

    @Column(name = "available_economy_seats", nullable = false)
    private Integer availableEconomySeats;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
    
    // Getters and Setters
    public Integer getFlightId() { return flightId; }
    public void setFlightId(Integer flightId) { this.flightId = flightId; }
    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }
    public Integer getAvailableBusinessSeats() { return availableBusinessSeats; }
    public void setAvailableBusinessSeats(Integer availableBusinessSeats) { this.availableBusinessSeats = availableBusinessSeats; }
    public Integer getAvailableEconomySeats() { return availableEconomySeats; }
    public void setAvailableEconomySeats(Integer availableEconomySeats) { this.availableEconomySeats = availableEconomySeats; }
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
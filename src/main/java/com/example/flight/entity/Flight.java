package com.example.flight.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

// 引入 Jackson 注解，用于格式化时间
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "flights")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flightId;

    // 与 FlightRoute 的多对一关联
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private FlightRoute flightRoute;
    
    @Column(name = "flight_number", length = 10, nullable = false)
    private String flightNumber;
    
    @ManyToOne
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;
    
    @ManyToOne
    @JoinColumn(name = "departure_airport", nullable = false)
    private Airport departureAirport;
    
    @ManyToOne
    @JoinColumn(name = "arrival_airport", nullable = false)
    private Airport arrivalAirport;
    
    // ★★★ 核心修复：添加 @JsonFormat 注解 ★★★
    // 这告诉后端：“前端传来的 yyyy-MM-dd HH:mm:ss 这种格式我也认！”
    @Column(name = "departure_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime departureTime;
    
    @Column(name = "arrival_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime arrivalTime;
    
    @Column(name = "aircraft_model", length = 50, nullable = false)
    private String aircraftModel;
    
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;
    
    @Column(name = "business_seats", nullable = false)
    private Integer businessSeats;
    
    @Column(name = "business_seat_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal businessSeatPrice;
    
    @Column(name = "economy_seats", nullable = false)
    private Integer economySeats;
    
    @Column(name = "economy_seat_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal economySeatPrice;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.PLANNED;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Transient
    private Integer availableEconomySeats;

    @Transient
    private Integer availableBusinessSeats;
    
    public enum FlightStatus {
        PLANNED("计划中"),
        BOARDING("登机中"),
        IN_FLIGHT("飞行中"),
        ARRIVED("已到达"),
        CANCELLED("已取消");
        
        private final String value;
        
        FlightStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    // --- Getters and Setters ---
    
    public FlightRoute getFlightRoute() {
        return flightRoute;
    }

    public void setFlightRoute(FlightRoute flightRoute) {
        this.flightRoute = flightRoute;
    }
    
    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Airport getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(Airport departureAirport) {
        this.departureAirport = departureAirport;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getBusinessSeats() {
        return businessSeats;
    }

    public void setBusinessSeats(Integer businessSeats) {
        this.businessSeats = businessSeats;
    }

    public BigDecimal getBusinessSeatPrice() {
        return businessSeatPrice;
    }

    public void setBusinessSeatPrice(BigDecimal businessSeatPrice) {
        this.businessSeatPrice = businessSeatPrice;
    }

    public Integer getEconomySeats() {
        return economySeats;
    }

    public void setEconomySeats(Integer economySeats) {
        this.economySeats = economySeats;
    }

    public BigDecimal getEconomySeatPrice() {
        return economySeatPrice;
    }

    public void setEconomySeatPrice(BigDecimal economySeatPrice) {
        this.economySeatPrice = economySeatPrice;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getAvailableEconomySeats() {
        return availableEconomySeats;
    }

    public void setAvailableEconomySeats(Integer availableEconomySeats) {
        this.availableEconomySeats = availableEconomySeats;
    }

    public Integer getAvailableBusinessSeats() {
        return availableBusinessSeats;
    }

    public void setAvailableBusinessSeats(Integer availableBusinessSeats) {
        this.availableBusinessSeats = availableBusinessSeats;
    }
}
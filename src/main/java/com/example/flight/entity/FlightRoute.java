package com.example.flight.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
// ★★★ 核心修改 1：使用 @Table 和 @UniqueConstraint 定义复合唯一键 ★★★
// 这意味着 “航空公司ID” + “基础航班号” 的组合必须是唯一的。
@Table(name = "flight_routes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"airline_id", "flightNumber"})
})
public class FlightRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer routeId;

    // ★★★ 核心修改 2：移除了这里的 unique = true 属性 ★★★
    @Column(nullable = false)
    private String flightNumber; // 基础航班号，不含航空公司代码，如 "5101"

    @ManyToOne(optional = false)
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @ManyToOne(optional = false)
    @JoinColumn(name = "departure_airport_code")
    private Airport departureAirport;

    @ManyToOne(optional = false)
    @JoinColumn(name = "arrival_airport_code")
    private Airport arrivalAirport;

    @Column(nullable = false)
    private LocalTime departureTime; // 每天的起飞时间，如 08:00

    @Column(nullable = false)
    private LocalTime arrivalTime; // 每天的到达时间，如 10:30

    @Column(nullable = false)
    private Integer flightDurationMinutes; // 飞行时长（分钟），用于跨天计算

    @Column(nullable = false)
    private String aircraftModel;

    @Column(nullable = false)
    private Integer businessSeats;

    @Column(nullable = false)
    private BigDecimal businessSeatPrice;

    @Column(nullable = false)
    private Integer economySeats;

    @Column(nullable = false)
    private BigDecimal economySeatPrice;

    @Column(nullable = false)
    private boolean active = true; // 航线是否启用

    // --- Getters and Setters (保持不变) ---
    public Integer getRouteId() { return routeId; }
    public void setRouteId(Integer routeId) { this.routeId = routeId; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public Airline getAirline() { return airline; }
    public void setAirline(Airline airline) { this.airline = airline; }
    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport departureAirport) { this.departureAirport = departureAirport; }
    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport arrivalAirport) { this.arrivalAirport = arrivalAirport; }
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public Integer getFlightDurationMinutes() { return flightDurationMinutes; }
    public void setFlightDurationMinutes(Integer flightDurationMinutes) { this.flightDurationMinutes = flightDurationMinutes; }
    public String getAircraftModel() { return aircraftModel; }
    public void setAircraftModel(String aircraftModel) { this.aircraftModel = aircraftModel; }
    public Integer getBusinessSeats() { return businessSeats; }
    public void setBusinessSeats(Integer businessSeats) { this.businessSeats = businessSeats; }
    public BigDecimal getBusinessSeatPrice() { return businessSeatPrice; }
    public void setBusinessSeatPrice(BigDecimal businessSeatPrice) { this.businessSeatPrice = businessSeatPrice; }
    public Integer getEconomySeats() { return economySeats; }
    public void setEconomySeats(Integer economySeats) { this.economySeats = economySeats; }
    public BigDecimal getEconomySeatPrice() { return economySeatPrice; }
    public void setEconomySeatPrice(BigDecimal economySeatPrice) { this.economySeatPrice = economySeatPrice; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
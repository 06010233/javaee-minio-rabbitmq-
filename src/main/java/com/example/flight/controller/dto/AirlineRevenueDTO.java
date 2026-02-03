package com.example.flight.controller.dto;

import java.math.BigDecimal;

public class AirlineRevenueDTO {
    private String airlineName;
    private BigDecimal revenue;

    public AirlineRevenueDTO(String airlineName, BigDecimal revenue) {
        this.airlineName = airlineName;
        this.revenue = revenue;
    }

    // Getters and Setters
    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
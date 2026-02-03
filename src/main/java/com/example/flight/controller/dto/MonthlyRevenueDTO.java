package com.example.flight.controller.dto;

import java.math.BigDecimal;

public class MonthlyRevenueDTO {
    private String month; // 格式 "YYYY-MM"
    private BigDecimal revenue;

    public MonthlyRevenueDTO(Integer year, Integer month, BigDecimal revenue) {
        this.month = String.format("%d-%02d", year, month);
        this.revenue = revenue;
    }

    // Getters and Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
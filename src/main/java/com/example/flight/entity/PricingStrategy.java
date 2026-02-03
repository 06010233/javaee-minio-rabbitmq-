package com.example.flight.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_strategies")
public class PricingStrategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer strategyId;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "base_discount", precision = 5, scale = 2)
    private BigDecimal baseDiscount = BigDecimal.ZERO;
    
    @Column(name = "advance_days")
    private Integer advanceDays = 0;
    
    @Column(name = "min_occupancy", precision = 5, scale = 2)
    private BigDecimal minOccupancy = BigDecimal.ZERO;
    
    @Column(name = "max_occupancy", precision = 5, scale = 2)
    private BigDecimal maxOccupancy = new BigDecimal("100.00");
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getBaseDiscount() {
        return baseDiscount;
    }

    public void setBaseDiscount(BigDecimal baseDiscount) {
        this.baseDiscount = baseDiscount;
    }

    public Integer getAdvanceDays() {
        return advanceDays;
    }

    public void setAdvanceDays(Integer advanceDays) {
        this.advanceDays = advanceDays;
    }

    public BigDecimal getMinOccupancy() {
        return minOccupancy;
    }

    public void setMinOccupancy(BigDecimal minOccupancy) {
        this.minOccupancy = minOccupancy;
    }

    public BigDecimal getMaxOccupancy() {
        return maxOccupancy;
    }

    public void setMaxOccupancy(BigDecimal maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

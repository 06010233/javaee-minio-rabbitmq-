package com.example.flight.service;

import com.example.flight.entity.PricingStrategy;
import com.example.flight.repository.PricingStrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class PricingStrategyService {

    private final PricingStrategyRepository pricingStrategyRepository;

    @Autowired
    public PricingStrategyService(PricingStrategyRepository pricingStrategyRepository) {
        this.pricingStrategyRepository = pricingStrategyRepository;
    }

    public List<PricingStrategy> findAll() {
        return pricingStrategyRepository.findAll();
    }

    public Optional<PricingStrategy> findById(Integer id) {
        return pricingStrategyRepository.findById(id);
    }

    public PricingStrategy savePricingStrategy(PricingStrategy pricingStrategy) {
        if (pricingStrategy.getCreatedAt() == null) {
            pricingStrategy.setCreatedAt(LocalDateTime.now());
        }
        return pricingStrategyRepository.save(pricingStrategy);
    }

    public List<PricingStrategy> findActiveStrategies() {
        LocalDate currentDate = LocalDate.now();
        return pricingStrategyRepository.findActiveStrategiesForDate(currentDate);
    }

    public BigDecimal calculateDiscount(BigDecimal basePrice, LocalDateTime flightDateTime, BigDecimal occupancyRate) {
        LocalDate currentDate = LocalDate.now();
        LocalDate flightDate = flightDateTime.toLocalDate();
        
        // Calculate days in advance
        long daysInAdvance = ChronoUnit.DAYS.between(currentDate, flightDate);
        
        // Find applicable strategies
        List<PricingStrategy> activeStrategies = pricingStrategyRepository.findActiveStrategiesForDate(currentDate);
        
        BigDecimal maxDiscount = BigDecimal.ZERO;
        
        for (PricingStrategy strategy : activeStrategies) {
            // Check if strategy applies based on advance days
            if (daysInAdvance >= strategy.getAdvanceDays()) {
                // Check if strategy applies based on occupancy
                if (occupancyRate.compareTo(strategy.getMinOccupancy()) >= 0 && 
                    occupancyRate.compareTo(strategy.getMaxOccupancy()) <= 0) {
                    
                    // Take the highest discount
                    if (strategy.getBaseDiscount().compareTo(maxDiscount) > 0) {
                        maxDiscount = strategy.getBaseDiscount();
                    }
                }
            }
        }
        
        // Calculate discount amount
        return basePrice.multiply(maxDiscount).divide(new BigDecimal(100));
    }

    public void deletePricingStrategy(Integer id) {
        pricingStrategyRepository.deleteById(id);
    }
}

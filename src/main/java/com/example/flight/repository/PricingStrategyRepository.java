package com.example.flight.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.flight.entity.PricingStrategy;

@Repository
public interface PricingStrategyRepository extends JpaRepository<PricingStrategy, Integer> {
    List<PricingStrategy> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
        LocalDate currentDate, LocalDate currentDate2);
    
    @Query("SELECT p FROM PricingStrategy p WHERE ?1 BETWEEN p.startDate AND p.endDate")
    List<PricingStrategy> findActiveStrategiesForDate(LocalDate date);
    
    List<PricingStrategy> findByAdvanceDaysLessThanEqual(Integer advanceDays);
}

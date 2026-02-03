package com.example.flight.controller;

import com.example.flight.entity.PricingStrategy;
import com.example.flight.service.PricingStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pricing-strategies")
public class PricingStrategyController {

    private final PricingStrategyService pricingStrategyService;

    @Autowired
    public PricingStrategyController(PricingStrategyService pricingStrategyService) {
        this.pricingStrategyService = pricingStrategyService;
    }

    @GetMapping
    public List<PricingStrategy> getAllPricingStrategies() {
        return pricingStrategyService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PricingStrategy> getPricingStrategyById(@PathVariable Integer id) {
        Optional<PricingStrategy> pricingStrategy = pricingStrategyService.findById(id);
        return pricingStrategy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public List<PricingStrategy> getActiveStrategies() {
        return pricingStrategyService.findActiveStrategies();
    }

    @GetMapping("/calculate-discount")
    public ResponseEntity<BigDecimal> calculateDiscount(
            @RequestParam BigDecimal basePrice,
            @RequestParam LocalDateTime flightDateTime,
            @RequestParam BigDecimal occupancyRate) {
        
        BigDecimal discount = pricingStrategyService.calculateDiscount(basePrice, flightDateTime, occupancyRate);
        return ResponseEntity.ok(discount);
    }

    @PostMapping
    public ResponseEntity<PricingStrategy> createPricingStrategy(@RequestBody PricingStrategy pricingStrategy) {
        pricingStrategy.setCreatedAt(LocalDateTime.now());
        PricingStrategy savedStrategy = pricingStrategyService.savePricingStrategy(pricingStrategy);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStrategy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PricingStrategy> updatePricingStrategy(
            @PathVariable Integer id,
            @RequestBody PricingStrategy pricingStrategy) {
        
        Optional<PricingStrategy> existingStrategy = pricingStrategyService.findById(id);
        
        if (existingStrategy.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        pricingStrategy.setStrategyId(id);
        pricingStrategy.setCreatedAt(existingStrategy.get().getCreatedAt());
        return ResponseEntity.ok(pricingStrategyService.savePricingStrategy(pricingStrategy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricingStrategy(@PathVariable Integer id) {
        if (!pricingStrategyService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        pricingStrategyService.deletePricingStrategy(id);
        return ResponseEntity.noContent().build();
    }
}

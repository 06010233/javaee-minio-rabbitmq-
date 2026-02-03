package com.example.flight.controller;

import com.example.flight.entity.Flight;
import com.example.flight.entity.FlightSeatAvailability;
import com.example.flight.entity.Order;
import com.example.flight.service.FlightSeatAvailabilityService;
import com.example.flight.service.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/seat-availability")
public class FlightSeatAvailabilityController {

    private final FlightSeatAvailabilityService availabilityService;
    private final FlightService flightService;

    // 使用构造函数注入 (推荐，移除了不必要的 @Autowired 注解)
    public FlightSeatAvailabilityController(FlightSeatAvailabilityService availabilityService,
                                           FlightService flightService) {
        this.availabilityService = availabilityService;
        this.flightService = flightService;
    }

    @GetMapping
    public List<FlightSeatAvailability> getAllAvailability() {
        return availabilityService.findAll();
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<FlightSeatAvailability> getAvailabilityByFlight(@PathVariable Integer flightId) {
        return availabilityService.findByFlightId(flightId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/business-available")
    public List<FlightSeatAvailability> getFlightsWithBusinessSeats() {
        return availabilityService.findWithAvailableBusinessSeats();
    }

    @GetMapping("/economy-available")
    public List<FlightSeatAvailability> getFlightsWithEconomySeats() {
        return availabilityService.findWithAvailableEconomySeats();
    }

    @PutMapping("/flight/{flightId}")
    public ResponseEntity<FlightSeatAvailability> updateAvailability(
            @PathVariable Integer flightId,
            @RequestBody FlightSeatAvailability availabilityUpdatePayload) {
        
        Optional<FlightSeatAvailability> existingOpt = availabilityService.findByFlightId(flightId);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        FlightSeatAvailability existingAvailability = existingOpt.get();
        Flight flight = existingAvailability.getFlight();

        // 从请求体中获取要更新的座位数
        Integer newBusinessSeats = availabilityUpdatePayload.getAvailableBusinessSeats();
        Integer newEconomySeats = availabilityUpdatePayload.getAvailableEconomySeats();

        // 验证新的座位数是否有效
        if (newBusinessSeats < 0 || newBusinessSeats > flight.getBusinessSeats() ||
            newEconomySeats < 0 || newEconomySeats > flight.getEconomySeats()) {
            return ResponseEntity.badRequest().build();
        }

        // 更新数据库中的实体
        existingAvailability.setAvailableBusinessSeats(newBusinessSeats);
        existingAvailability.setAvailableEconomySeats(newEconomySeats);
        
        // 保存更新后的实体
        FlightSeatAvailability savedAvailability = availabilityService.saveAvailability(existingAvailability);
        return ResponseEntity.ok(savedAvailability);
    }

    @PutMapping("/flight/{flightId}/update-seats")
    public ResponseEntity<Boolean> updateSeatAvailability(
            @PathVariable Integer flightId,
            @RequestParam Order.SeatClass seatClass,
            @RequestParam Integer delta) {
        
        // 直接调用 Service 层方法，参数类型匹配
        boolean updated = availabilityService.updateSeatAvailability(flightId, seatClass, delta);
        
        return updated ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body(false);
    }

    @GetMapping("/flight/{flightId}/check")
    public ResponseEntity<Boolean> checkSeatAvailability(
            @PathVariable Integer flightId,
            @RequestParam Order.SeatClass seatClass,
            @RequestParam Integer requiredSeats) {
        
        // 直接调用 Service 层方法，参数类型匹配
        boolean available = availabilityService.checkSeatAvailability(flightId, seatClass, requiredSeats);
        return ResponseEntity.ok(available);
    }

    @PostMapping("/flight/{flightId}/initialize")
    public ResponseEntity<FlightSeatAvailability> initializeAvailability(@PathVariable Integer flightId) {
        Optional<Flight> flightOpt = flightService.findById(flightId);
        if (flightOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // 检查座位信息是否已存在
        if (availabilityService.findByFlightId(flightId).isPresent()) {
            return ResponseEntity.badRequest().body(null); // 或者返回 409 Conflict
        }
        
        // 调用 Service 初始化
        availabilityService.initializeAvailability(flightOpt.get());
        
        // 再次查找并返回新创建的实体
        return availabilityService.findByFlightId(flightId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.internalServerError().build());
    }
}
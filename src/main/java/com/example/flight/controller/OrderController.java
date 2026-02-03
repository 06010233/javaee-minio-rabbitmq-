// 文件路径: com/example/flight/controller/OrderController.java

package com.example.flight.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.flight.entity.Order;
import com.example.flight.exception.ResourceNotFoundException;
import com.example.flight.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<Order> getAllOrders() { return orderService.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        return orderService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Order>> getOrdersByPassenger(@PathVariable Integer passengerId) {
        return ResponseEntity.ok(orderService.findByPassengerId(passengerId));
    }

    @GetMapping("/trips/passenger/{passengerId}")
    public ResponseEntity<List<Order>> getTripsByPassenger(@PathVariable Integer passengerId) {
        return ResponseEntity.ok(orderService.findTripsByPassengerId(passengerId));
    }

    @GetMapping("/{id}/refund-preview")
    public ResponseEntity<Map<String, Object>> getRefundPreview(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getRefundPreview(id));
    }
    
    @GetMapping("/{id}/payment-qrcode")
    public ResponseEntity<Map<String, String>> getPaymentQrCode(@PathVariable Integer id) {
        try {
            Map<String, String> result = orderService.generateQrCodeUrlForOrder(id);
            return ResponseEntity.ok(result);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Order order) {
        Map<String, Object> result = orderService.createOrder(order);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Integer id, @RequestParam Order.OrderStatus status) {
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    // ★★★ 新增：为用户行程日历提供数据的API端点 ★★★
    @GetMapping("/calendar/passenger/{passengerId}")
    public ResponseEntity<List<Map<String, Object>>> getCalendarEventsForPassenger(@PathVariable Integer passengerId) {
        List<Map<String, Object>> events = orderService.getCalendarEventsForPassenger(passengerId);
        return ResponseEntity.ok(events);
    }
}
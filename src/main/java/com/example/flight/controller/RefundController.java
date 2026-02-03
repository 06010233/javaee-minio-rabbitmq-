package com.example.flight.controller;

import com.example.flight.entity.Order;
import com.example.flight.entity.Refund;
import com.example.flight.entity.Ticket;
import com.example.flight.service.OrderService;
import com.example.flight.service.RefundService;
import com.example.flight.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/refunds")
public class RefundController {

    private final RefundService refundService;
    private final OrderService orderService;
    private final TicketService ticketService;

    @Autowired
    public RefundController(RefundService refundService,
                           OrderService orderService,
                           TicketService ticketService) {
        this.refundService = refundService;
        this.orderService = orderService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<Refund> getAllRefunds() {
        return refundService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Refund> getRefundById(@PathVariable Integer id) {
        Optional<Refund> refund = refundService.findById(id);
        return refund.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Refund>> getRefundsByOrder(@PathVariable Integer orderId) {
        Optional<Order> order = orderService.findById(orderId);
        
        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Refund> refunds = refundService.findByOrder(order.get());
        return ResponseEntity.ok(refunds);
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<Refund> getRefundByTicket(@PathVariable Integer ticketId) {
        Optional<Ticket> ticket = ticketService.findById(ticketId);
        
        if (ticket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Refund> refund = refundService.findByTicket(ticket.get());
        return refund.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public List<Refund> getRefundsByStatus(@PathVariable Refund.RefundStatus status) {
        return refundService.findByStatus(status);
    }

    @GetMapping("/date-range")
    public List<Refund> getRefundsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return refundService.findByRefundTimeBetween(start, end);
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestRefund(
            @RequestParam Integer orderId,
            @RequestParam Integer ticketId,
            @RequestParam(required = false) String reason) {
        
        Optional<Order> order = orderService.findById(orderId);
        Optional<Ticket> ticket = ticketService.findById(ticketId);
        
        if (order.isEmpty() || ticket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if ticket belongs to order
        if (!ticket.get().getOrder().getOrderId().equals(orderId)) {
            return ResponseEntity.badRequest().body("Ticket does not belong to this order");
        }
        
        try {
            Refund refund = refundService.createRefund(order.get(), ticket.get(), reason);
            return ResponseEntity.status(HttpStatus.CREATED).body(refund);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Refund> updateRefundStatus(
            @PathVariable Integer id,
            @RequestParam Refund.RefundStatus status) {
        
        Refund updatedRefund = refundService.updateRefundStatus(id, status);
        
        if (updatedRefund != null) {
            return ResponseEntity.ok(updatedRefund);
        }
        
        return ResponseEntity.notFound().build();
    }
}

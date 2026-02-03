package com.example.flight.service;

import com.example.flight.entity.Order;
import com.example.flight.entity.Refund;
import com.example.flight.entity.Ticket;
import com.example.flight.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class RefundService {

    private final RefundRepository refundRepository;
    private final TicketService ticketService;
    private final OrderService orderService;

    @Autowired
    public RefundService(RefundRepository refundRepository, 
                        TicketService ticketService,
                        OrderService orderService) {
        this.refundRepository = refundRepository;
        this.ticketService = ticketService;
        this.orderService = orderService;
    }

    public List<Refund> findAll() {
        return refundRepository.findAll();
    }

    public Optional<Refund> findById(Integer id) {
        return refundRepository.findById(id);
    }

    public List<Refund> findByOrder(Order order) {
        return refundRepository.findByOrder(order);
    }

    public Optional<Refund> findByTicket(Ticket ticket) {
        return refundRepository.findByTicket(ticket);
    }

    @Transactional
    public Refund createRefund(Order order, Ticket ticket, String reason) {
        // Check if ticket is already refunded
        if (ticket.getStatus() == Ticket.TicketStatus.REFUNDED) {
            throw new IllegalStateException("Ticket is already refunded");
        }
        
        // Calculate refund amount based on time until flight
        BigDecimal refundAmount = calculateRefundAmount(ticket);
        
        Refund refund = new Refund();
        refund.setOrder(order);
        refund.setTicket(ticket);
        refund.setRefundAmount(refundAmount);
        refund.setRefundTime(LocalDateTime.now());
        refund.setReason(reason);
        refund.setStatus(Refund.RefundStatus.PENDING);
        refund.setCreatedAt(LocalDateTime.now());
        
        // Update ticket status
        ticket.setStatus(Ticket.TicketStatus.REFUNDED);
        ticketService.saveTicket(ticket);
        
        // Update order status if all tickets are refunded
        List<Ticket> orderTickets = ticketService.findByOrder(order);
        boolean allRefunded = orderTickets.stream()
                .allMatch(t -> t.getStatus() == Ticket.TicketStatus.REFUNDED);
        
        if (allRefunded) {
            orderService.updateOrderStatus(order.getOrderId(), Order.OrderStatus.REFUNDED);
        }
        
        return refundRepository.save(refund);
    }

    private BigDecimal calculateRefundAmount(Ticket ticket) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = ticket.getFlight().getDepartureTime();
        
        // Calculate hours until departure
        long hoursUntilDeparture = ChronoUnit.HOURS.between(now, departureTime);
        
        // Refund policy: 
        // - More than 48 hours: 90% refund
        // - 24-48 hours: 70% refund
        // - 12-24 hours: 50% refund
        // - Less than 12 hours: 30% refund
        BigDecimal refundRate;
        
        if (hoursUntilDeparture > 48) {
            refundRate = new BigDecimal("0.90");
        } else if (hoursUntilDeparture > 24) {
            refundRate = new BigDecimal("0.70");
        } else if (hoursUntilDeparture > 12) {
            refundRate = new BigDecimal("0.50");
        } else {
            refundRate = new BigDecimal("0.30");
        }
        
        return ticket.getPrice().multiply(refundRate);
    }

    public Refund updateRefundStatus(Integer refundId, Refund.RefundStatus status) {
        Optional<Refund> refundOpt = refundRepository.findById(refundId);
        if (refundOpt.isPresent()) {
            Refund refund = refundOpt.get();
            refund.setStatus(status);
            return refundRepository.save(refund);
        }
        return null;
    }

    public List<Refund> findByRefundTimeBetween(LocalDateTime start, LocalDateTime end) {
        return refundRepository.findByRefundTimeBetween(start, end);
    }

    public List<Refund> findByStatus(Refund.RefundStatus status) {
        return refundRepository.findByStatus(status);
    }
}

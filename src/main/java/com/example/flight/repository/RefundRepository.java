package com.example.flight.repository;

import com.example.flight.entity.Order;
import com.example.flight.entity.Refund;
import com.example.flight.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Integer> {
    List<Refund> findByOrder(Order order);
    Optional<Refund> findByTicket(Ticket ticket);
    List<Refund> findByRefundTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Refund> findByStatus(Refund.RefundStatus status);
}

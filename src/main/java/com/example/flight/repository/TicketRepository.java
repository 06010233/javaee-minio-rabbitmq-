package com.example.flight.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.flight.entity.Flight;
import com.example.flight.entity.Order;
import com.example.flight.entity.Passenger;
import com.example.flight.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByOrder(Order order);
    List<Ticket> findByFlight(Flight flight);
    List<Ticket> findByPassenger(Passenger passenger);
    List<Ticket> findByStatus(Ticket.TicketStatus status);
    
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.flight = ?1 AND t.seatClass = ?2 AND t.status = 'UNUSED'")
    Long countActiveTicketsByFlightAndClass(Flight flight, Order.SeatClass seatClass);
    
    @Query("SELECT t FROM Ticket t WHERE t.flight = ?1 AND t.status = 'UNUSED'")
    List<Ticket> findActiveTicketsByFlight(Flight flight);
    
    List<Ticket> findByFlightAndSeatClass(Flight flight, Order.SeatClass seatClass);
}

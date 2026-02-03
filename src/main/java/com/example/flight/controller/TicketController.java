package com.example.flight.controller;

import com.example.flight.entity.Flight;
import com.example.flight.entity.Order;
import com.example.flight.entity.Passenger;
import com.example.flight.entity.Ticket;
import com.example.flight.service.FlightService;
import com.example.flight.service.OrderService;
import com.example.flight.service.PassengerService;
import com.example.flight.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final OrderService orderService;
    private final FlightService flightService;
    private final PassengerService passengerService;

    @Autowired
    public TicketController(TicketService ticketService, 
                           OrderService orderService,
                           FlightService flightService,
                           PassengerService passengerService) {
        this.ticketService = ticketService;
        this.orderService = orderService;
        this.flightService = flightService;
        this.passengerService = passengerService;
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Integer id) {
        Optional<Ticket> ticket = ticketService.findById(id);
        return ticket.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Ticket>> getTicketsByOrder(@PathVariable Integer orderId) {
        Optional<Order> order = orderService.findById(orderId);
        
        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Ticket> tickets = ticketService.findByOrder(order.get());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Ticket>> getTicketsByFlight(@PathVariable Integer flightId) {
        Optional<Flight> flight = flightService.findById(flightId);
        
        if (flight.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Ticket> tickets = ticketService.findByFlight(flight.get());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Ticket>> getTicketsByPassenger(@PathVariable Integer passengerId) {
        Optional<Passenger> passenger = passengerService.findById(passengerId);
        
        if (passenger.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Ticket> tickets = ticketService.findByPassenger(passenger.get());
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/flight/{flightId}/active")
    public ResponseEntity<List<Ticket>> getActiveTicketsByFlight(@PathVariable Integer flightId) {
        Optional<Flight> flight = flightService.findById(flightId);
        
        if (flight.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        List<Ticket> tickets = ticketService.findActiveTicketsByFlight(flight.get());
        return ResponseEntity.ok(tickets);
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        ticket.setCreatedAt(LocalDateTime.now());
        Ticket savedTicket = ticketService.saveTicket(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTicket);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Integer id, @RequestBody Ticket ticket) {
        Optional<Ticket> existingTicket = ticketService.findById(id);
        
        if (existingTicket.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        ticket.setTicketId(id);
        ticket.setCreatedAt(existingTicket.get().getCreatedAt());
        return ResponseEntity.ok(ticketService.saveTicket(ticket));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Ticket> updateTicketStatus(
            @PathVariable Integer id,
            @RequestParam Ticket.TicketStatus status) {
        
        Ticket updatedTicket = ticketService.updateTicketStatus(id, status);
        
        if (updatedTicket != null) {
            return ResponseEntity.ok(updatedTicket);
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Integer id) {
        if (!ticketService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}

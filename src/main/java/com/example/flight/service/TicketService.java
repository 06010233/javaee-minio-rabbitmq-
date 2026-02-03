package com.example.flight.service;

import com.example.flight.entity.Flight;
import com.example.flight.entity.Order;
import com.example.flight.entity.Passenger;
import com.example.flight.entity.Ticket;
import com.example.flight.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 确保引入

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * [新增] 根据一个已支付的订单创建一张机票。
     * @param order 一个状态为PAID的订单对象。
     * @return 新创建的机票对象。
     */
    @Transactional
    public Ticket createTicketForOrder(Order order) {
        if (order.getStatus() != Order.OrderStatus.PAID) {
            throw new IllegalStateException("只有已支付的订单才能创建机票");
        }
        
        Ticket ticket = new Ticket();
        ticket.setOrder(order);
        ticket.setFlight(order.getFlight());
        ticket.setPassenger(order.getTicketPassenger()); // 使用订单中的乘机人信息
        ticket.setSeatClass(order.getSeatClass());
        ticket.setPrice(order.getActualPrice());
        ticket.setStatus(Ticket.TicketStatus.UNUSED); // 初始状态为未使用
        ticket.setCreatedAt(LocalDateTime.now());
        
        return ticketRepository.save(ticket);
    }

    // --- 其他方法保持不变 ---

    public List<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> findById(Integer id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> findByOrder(Order order) {
        return ticketRepository.findByOrder(order);
    }

    public List<Ticket> findByFlight(Flight flight) {
        return ticketRepository.findByFlight(flight);
    }

    public List<Ticket> findByPassenger(Passenger passenger) {
        return ticketRepository.findByPassenger(passenger);
    }

    public Ticket saveTicket(Ticket ticket) {
        if (ticket.getCreatedAt() == null) {
            ticket.setCreatedAt(LocalDateTime.now());
        }
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicketStatus(Integer ticketId, Ticket.TicketStatus status) {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setStatus(status);
            return ticketRepository.save(ticket);
        }
        return null;
    }

    public Long countActiveTicketsByFlightAndClass(Flight flight, Order.SeatClass seatClass) {
        return ticketRepository.countActiveTicketsByFlightAndClass(flight, seatClass);
    }

    public List<Ticket> findActiveTicketsByFlight(Flight flight) {
        return ticketRepository.findActiveTicketsByFlight(flight);
    }

    public void deleteTicket(Integer id) {
        ticketRepository.deleteById(id);
    }
}
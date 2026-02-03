// 文件路径: src/main/java/com/example/flight/repository/OrderRepository.java

package com.example.flight.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.flight.controller.dto.AirlineRevenueDTO;
import com.example.flight.controller.dto.MonthlyRevenueDTO;
import com.example.flight.entity.Flight;
import com.example.flight.entity.Order;
import com.example.flight.entity.Passenger;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query("SELECT o FROM Order o " +
           "LEFT JOIN FETCH o.flight f " +
           "LEFT JOIN FETCH f.airline " +
           "LEFT JOIN FETCH f.departureAirport " +
           "LEFT JOIN FETCH f.arrivalAirport " +
           "LEFT JOIN FETCH o.passenger " +
           "LEFT JOIN FETCH o.ticketPassenger")
    List<Order> findAllWithDetails();
    
    @Query("SELECT o FROM Order o WHERE o.ticketPassenger.passengerId = :passengerId AND o.status IN ('PAID', 'TICKETED', 'COMPLETED') ORDER BY o.flight.departureTime DESC")
    List<Order> findTripsByTicketPassengerId(@Param("passengerId") Integer passengerId);

    @Query("SELECT o FROM Order o WHERE o.flight = :flight AND o.ticketPassenger = :ticketPassenger AND o.status NOT IN ('CANCELLED', 'REFUNDED')")
    List<Order> findActiveOrdersByFlightAndPassenger(@Param("flight") Flight flight, @Param("ticketPassenger") Passenger ticketPassenger);

    List<Order> findByPassenger(Passenger passenger);
    List<Order> findByFlight(Flight flight);
    List<Order> findByStatus(Order.OrderStatus status);
    List<Order> findByOrderTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new com.example.flight.controller.dto.MonthlyRevenueDTO(YEAR(o.orderTime), MONTH(o.orderTime), SUM(o.actualPrice)) " +
           "FROM Order o WHERE o.status IN :statuses " +
           "GROUP BY YEAR(o.orderTime), MONTH(o.orderTime) " +
           "ORDER BY YEAR(o.orderTime), MONTH(o.orderTime)")
    List<MonthlyRevenueDTO> findMonthlyRevenue(@Param("statuses") List<Order.OrderStatus> statuses);

    @Query("SELECT new com.example.flight.controller.dto.AirlineRevenueDTO(a.airlineName, SUM(o.actualPrice)) " +
           "FROM Order o JOIN o.flight f JOIN f.airline a " +
           "WHERE o.status IN :statuses " +
           "GROUP BY a.airlineName " +
           "ORDER BY SUM(o.actualPrice) DESC")
    List<AirlineRevenueDTO> findAirlineRevenue(@Param("statuses") List<Order.OrderStatus> statuses);

    // ★★★ 最终核心修复：将 o.ticketPassenger.id 修改为 o.ticketPassenger.passengerId ★★★
    @Query("SELECT o FROM Order o WHERE o.ticketPassenger.passengerId = :passengerId AND o.status IN ('PAID', 'TICKETED')")
    List<Order> findUpcomingTripsByPassengerId(@Param("passengerId") Integer passengerId);
}
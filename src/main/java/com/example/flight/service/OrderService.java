// 文件路径: src/main/java/com/example/flight/service/OrderService.java

package com.example.flight.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.flight.entity.Flight;
import com.example.flight.entity.Order;
import com.example.flight.entity.Passenger;
import com.example.flight.exception.ResourceNotFoundException;
import com.example.flight.repository.FlightRepository;
import com.example.flight.repository.OrderRepository;
import com.example.flight.repository.PassengerRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final FlightSeatAvailabilityService availabilityService;
    private final TicketService ticketService;
    private final RabbitTemplate rabbitTemplate;
    private final PassengerService passengerService; // ★ 新增注入

    @Autowired
    public OrderService(OrderRepository orderRepository, 
                        FlightRepository flightRepository, 
                        PassengerRepository passengerRepository, 
                        FlightSeatAvailabilityService availabilityService, 
                        TicketService ticketService, 
                        RabbitTemplate rabbitTemplate,
                        PassengerService passengerService) { // ★ 构造函数注入
        this.orderRepository = orderRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.availabilityService = availabilityService;
        this.ticketService = ticketService;
        this.rabbitTemplate = rabbitTemplate;
        this.passengerService = passengerService;
    }

    public List<Order> findAll() { return orderRepository.findAllWithDetails(); }
    public Optional<Order> findById(Integer id) { return orderRepository.findById(id); }
    public List<Order> findByPassengerId(Integer passengerId) { Passenger p = passengerRepository.findById(passengerId).orElseThrow(() -> new ResourceNotFoundException("找不到ID为 " + passengerId + " 的乘客")); return orderRepository.findByPassenger(p); }
    public List<Order> findTripsByPassengerId(Integer passengerId) { return orderRepository.findTripsByTicketPassengerId(passengerId); }

    @Transactional
    public Map<String, Object> createOrder(Order orderRequest) {
        Flight flight = flightRepository.findByIdWithDetails(orderRequest.getFlight().getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID为 " + orderRequest.getFlight().getFlightId() + " 的航班"));
        
        Passenger orderingPassenger = passengerRepository.findById(orderRequest.getPassenger().getPassengerId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID为 " + orderRequest.getPassenger().getPassengerId() + " 的下单乘客"));
        
        // ★★★ 核心修改：不再手动校验，而是调用 Service 的“获取或创建”逻辑 ★★★
        // 这实现了：如果乘机人B不存在，系统自动注册B
        Passenger reqPassenger = orderRequest.getTicketPassenger();
        Passenger ticketPassenger = passengerService.createOrGetPassenger(
            reqPassenger.getName(),
            reqPassenger.getIdCard(),
            reqPassenger.getPhone(),
            // 如果前端没传邮箱，这里给个null
            reqPassenger.getEmail()
        );

        if (!availabilityService.checkSeatAvailability(flight.getFlightId(), orderRequest.getSeatClass(), 1)) { 
            throw new IllegalStateException("预订失败：所选舱位已售罄。"); 
        }
        
        Order newOrder = new Order();
        newOrder.setFlight(flight);
        newOrder.setPassenger(orderingPassenger);
        newOrder.setTicketPassenger(ticketPassenger); // 设置处理后的乘机人
        newOrder.setSeatClass(orderRequest.getSeatClass());
        newOrder.setContactPhone(orderRequest.getContactPhone());
        newOrder.setStatus(Order.OrderStatus.PENDING_PAYMENT);
        newOrder.setOrderTime(LocalDateTime.now());
        newOrder.setCreatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(newOrder);
        
        String serverAddress = "http://192.168.102.253:8080";
        String qrCodeUrl = serverAddress + "/payment/confirm?orderId=" + savedOrder.getOrderId();
        
        Map<String, Object> result = new HashMap<>();
        result.put("order", savedOrder);
        result.put("qrCodeUrl", qrCodeUrl);
        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, String> generateQrCodeUrlForOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到ID为 " + orderId + " 的订单"));

        if (order.getStatus() != Order.OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("此订单状态不正确，无法支付。");
        }

        String serverAddress = "http://192.168.102.253:8080";
        String qrCodeUrl = serverAddress + "/payment/confirm?orderId=" + order.getOrderId();

        Map<String, String> result = new HashMap<>();
        result.put("qrCodeUrl", qrCodeUrl);
        return result;
    }

    // ★★★ 核心修改：已删除 validateTicketPassenger 方法，因为不再需要抛出异常 ★★★

    public Map<String, Object> getRefundPreview(Integer orderId) { 
        Order o = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("找不到ID为 " + orderId + " 的订单")); 
        long h = ChronoUnit.HOURS.between(LocalDateTime.now(), o.getFlight().getDepartureTime()); 
        BigDecimal r; 
        if (h > 48) r = new BigDecimal("1.00"); else if (h > 24) r = new BigDecimal("0.80"); else if (h > 2) r = new BigDecimal("0.50"); else r = new BigDecimal("0.00"); 
        BigDecimal p = o.getActualPrice(); 
        BigDecimal ra = p.multiply(r).setScale(2, RoundingMode.HALF_UP); 
        BigDecimal f = p.subtract(ra); 
        Map<String, Object> pv = new HashMap<>(); 
        pv.put("refundable", r.compareTo(BigDecimal.ZERO) > 0); 
        pv.put("refundAmount", ra); 
        pv.put("fee", f); 
        String m; 
        if (r.compareTo(BigDecimal.ZERO) > 0) m = String.format("距离起飞还剩约 %d 小时，按政策可退款 %d%%。", h, r.multiply(new BigDecimal(100)).intValue()); else m = "距离起飞不足2小时，按政策已无法退票。"; 
        pv.put("policy", m); 
        return pv; 
    }
    
    @Transactional
    public Order updateOrderStatus(Integer orderId, Order.OrderStatus status) { 
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("找不到ID为 " + orderId + " 的订单")); 
        Order.OrderStatus oldStatus = order.getStatus(); 
        if (oldStatus == status) return order; 
        order.setStatus(status); 
        if (oldStatus == Order.OrderStatus.PENDING_PAYMENT && status == Order.OrderStatus.PAID) { 
            if (!availabilityService.updateSeatAvailability(order.getFlight().getFlightId(), order.getSeatClass(), -1)) { 
                throw new IllegalStateException("更新座位库存失败"); 
            } 
            ticketService.createTicketForOrder(order); 
            try { 
                String smsContent = "尊敬的" + order.getPassenger().getName() + "，您的订单 " + order.getOrderId() + " 已出票。"; 
                rabbitTemplate.convertAndSend("sms.queue", smsContent); 
            } catch (Exception e) { 
                System.err.println("消息通知发送失败: " + e.getMessage()); 
            } 
        } 
        if (status == Order.OrderStatus.CANCELLED && (oldStatus == Order.OrderStatus.PAID || oldStatus == Order.OrderStatus.TICKETED)) { 
            availabilityService.updateSeatAvailability(order.getFlight().getFlightId(), order.getSeatClass(), 1); 
        } 
        return orderRepository.save(order); 
    }

    public List<Map<String, Object>> getCalendarEventsForPassenger(Integer passengerId) {
        List<Order> upcomingOrders = orderRepository.findUpcomingTripsByPassengerId(passengerId);
        return upcomingOrders.stream().map(order -> {
            Map<String, Object> event = new HashMap<>();
            event.put("title", "航班 " + order.getFlight().getFlightNumber()); 
            event.put("start", order.getFlight().getDepartureTime());
            event.put("end", order.getFlight().getArrivalTime());
            event.put("extendedProps", Map.of(
                "orderId", order.getOrderId(),
                "from", order.getFlight().getDepartureAirport().getCity(),
                "to", order.getFlight().getArrivalAirport().getCity()
            ));
            return event;
        }).collect(Collectors.toList());
    }
}
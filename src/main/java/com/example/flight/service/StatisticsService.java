// service/StatisticsService.java
package com.example.flight.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.flight.controller.dto.AirlineRevenueDTO;
import com.example.flight.controller.dto.MonthlyRevenueDTO;
import com.example.flight.entity.Order;
import com.example.flight.repository.OrderRepository;

@Service
public class StatisticsService {

    private final OrderRepository orderRepository;

    @Autowired
    public StatisticsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 定义哪些状态算作“有效收益”（已支付、已出票、已完成）
    private final List<Order.OrderStatus> revenueStatuses = Arrays.asList(
        Order.OrderStatus.PAID,
        Order.OrderStatus.TICKETED,
        Order.OrderStatus.COMPLETED
    );

    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        // ★★★ 修复：调用时传入状态列表 ★★★
        return orderRepository.findMonthlyRevenue(revenueStatuses);
    }

    public List<AirlineRevenueDTO> getAirlineRevenue() {
        // ★★★ 修复：调用时传入状态列表 ★★★
        return orderRepository.findAirlineRevenue(revenueStatuses);
    }
}
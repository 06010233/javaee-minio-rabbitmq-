package com.example.flight.controller;

import com.example.flight.entity.Order;
import com.example.flight.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller // 使用 @Controller 而不是 @RestController，因为我们需要返回HTML页面
public class PaymentController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // 用于发送 WebSocket 消息

    @Autowired
    private OrderService orderService; // 注入你的订单服务

    /**
     * 手机端扫描二维码后打开的确认页面
     * @param orderId 订单ID，从URL参数中获取
     * @param model Spring MVC model，用于向HTML模板传递数据
     * @return 页面的模板名称 (thymeleaf会自动寻找 "payment_confirm.html")
     */
    @GetMapping("/payment/confirm")
    public String paymentConfirmPage(@RequestParam Integer orderId, Model model) {
        // 从数据库中根据 orderId 查询订单信息
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderId));

        model.addAttribute("orderId", order.getOrderId());
        model.addAttribute("amount", order.getActualPrice());
        model.addAttribute("flightInfo", "航班 " + order.getFlight().getFlightNumber());

        return "payment_confirm"; // 返回 src/main/resources/templates/payment_confirm.html
    }

    /**
     * 手机端点击“确认支付”后调用的接口
     * @param orderId 订单ID，从请求体中获取
     * @return 返回一个JSON对象，表示操作结果
     */
    @PostMapping("/payment/process")
    @ResponseBody // 表示此方法的返回值直接作为HTTP响应体，而不是视图名
    public Map<String, String> processPayment(@RequestParam Integer orderId) {
        // 1. 调用 OrderService 更新订单状态为“已支付”
        //    这个方法内部会处理库存扣减、生成机票、发送通知等逻辑
        orderService.updateOrderStatus(orderId, Order.OrderStatus.PAID);

        // 2. 通过 WebSocket 向订阅了该订单主题的前端页面发送“支付成功”消息
        //    主题地址格式为 /topic/payment-status/{orderId}
        String destination = "/topic/payment-status/" + orderId;
        messagingTemplate.convertAndSend(destination, "{\"status\":\"SUCCESS\"}");

        // 3. 返回成功信息给手机端
        Map<String, String> response = new HashMap<>();
        response.put("message", "支付已确认");
        return response;
    }
}
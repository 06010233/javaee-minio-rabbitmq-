package com.example.flight.service;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "sms.queue")
public class SmsListener {

    @RabbitHandler
    public void process(String message) {
        // 模拟调用短信接口
        System.out.println("【短信发送服务】收到消息，正在发送短信: " + message);
        // 这里可以接入阿里云/腾讯云短信SDK
    }
}
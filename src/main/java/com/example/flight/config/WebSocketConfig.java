package com.example.flight.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // 启用 WebSocket 消息代理
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册一个 STOMP 的端点(endpoint)，并指定使用 SockJS 协议。
        // 客户端将连接到 "/ws" 这个地址来建立 WebSocket 连接。
        // setAllowedOriginPatterns("*") 允许所有域进行跨域连接，在生产环境中应配置为你的前端域名。
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 配置消息代理(Message Broker)
        // "/topic" 是用于发布/订阅模式的前缀，服务器将消息广播给所有订阅了对应主题的客户端。
        registry.enableSimpleBroker("/topic");
        // 定义了客户端发送消息的目标前缀。客户端发送的消息目标地址若以 "/app" 开头，
        // 将会被路由到带有 @MessageMapping 注解的控制器方法中。
        registry.setApplicationDestinationPrefixes("/app");
    }
}
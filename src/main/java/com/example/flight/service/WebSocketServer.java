package com.example.flight.service;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 服务端
 * 用于向客户端（前端）推送实时消息，例如支付成功通知
 */
@ServerEndpoint("/ws/{userId}")
@Component
public class WebSocketServer {

    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static ConcurrentHashMap<String, Session> sessionPool = new ConcurrentHashMap<>();

    // 建立连接成功调用
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        try {
            sessionPool.put(userId, session);
            System.out.println("【WebSocket消息】有新的连接，总数:" + sessionPool.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭连接时调用
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        try {
            sessionPool.remove(userId);
            System.out.println("【WebSocket消息】连接断开，总数:" + sessionPool.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 收到客户端消息后调用的方法
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("【WebSocket消息】收到客户端消息:" + message);
    }

    // 发生错误时调用
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("App 用户错误原因:" + error.getMessage());
        error.printStackTrace();
    }

    // 此为单点消息，发送给指定用户
    public static void sendInfo(String userId, String message) {
        Session session = sessionPool.get(userId);
        if (session != null && session.isOpen()) {
            try {
                System.out.println("【WebSocket消息】推送消息到窗口" + userId + "，推送内容:" + message);
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("【WebSocket消息】用户 " + userId + " 不在线，消息未发送");
        }
    }
}
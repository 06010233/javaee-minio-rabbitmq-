package com.example.flight.controller.dto;

public class RegisterRequest {
    // 乘客基本信息
    private String name;
    private String idCard;
    private String phone;
    private String email;
    
    // 账户信息
    private String username;
    private String password;

    // Getters 和 Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
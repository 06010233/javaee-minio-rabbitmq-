package com.example.flight.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 我们用 422 Unprocessable Entity 状态码，它非常适合表示“服务器理解请求，但内容有语义错误”
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
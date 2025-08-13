package com.example.exception;

public class NotFoundNewsException extends BusinessException {
    public NotFoundNewsException() {
        super(ErrorCode.NOT_FOUND_NEWS);
    }
}

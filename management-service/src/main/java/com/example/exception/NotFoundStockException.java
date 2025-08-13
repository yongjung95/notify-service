package com.example.exception;

public class NotFoundStockException extends BusinessException {
    public NotFoundStockException() {
        super(ErrorCode.NOT_FOUND_KOREA_STOCK);
    }
}

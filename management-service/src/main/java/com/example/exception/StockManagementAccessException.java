package com.example.exception;

public class StockManagementAccessException extends BusinessException {
    public StockManagementAccessException() {
        super(ErrorCode.STOCK_MANAGEMENT_ACCESS_FORBIDDEN);
    }
}

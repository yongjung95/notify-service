package com.example.exception;

public class StockPriceManagementAccessException extends BusinessException {
    public StockPriceManagementAccessException() {
        super(ErrorCode.STOCK_MANAGEMENT_ACCESS_FORBIDDEN);
    }
}

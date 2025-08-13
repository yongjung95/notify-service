package com.example.exception;

public class DuplicateStockPriceManagementException extends BusinessException {
    public DuplicateStockPriceManagementException() {
        super(ErrorCode.DUPLICATE_STOCK_MANAGEMENT);
    }
}

package com.example.exception;

public class DuplicateStockManagementException extends BusinessException {
    public DuplicateStockManagementException() {
        super(ErrorCode.DUPLICATE_STOCK_MANAGEMENT);
    }
}

package com.example.exception;

public class DuplicateNewsManagementException extends BusinessException {
    public DuplicateNewsManagementException() {
        super(ErrorCode.DUPLICATE_NEWS_MANAGEMENT);
    }
}

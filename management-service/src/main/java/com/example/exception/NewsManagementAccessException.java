package com.example.exception;

public class NewsManagementAccessException extends BusinessException {
    public NewsManagementAccessException() {
        super(ErrorCode.NEWS_MANAGEMENT_ACCESS_FORBIDDEN);
    }
}

package com.example.dto;

import lombok.Builder;

@Builder
public record CommonResultRecord<T>(
        int code,
        String message,
        T data
) {

    public static <T> CommonResultRecord<T> failResult(int code, String message) {
        return CommonResultRecord.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }

    public static <T> CommonResultRecord<T> successResult(int code, String message, T data) {
        return CommonResultRecord.<T>builder()
                .code(code)
                .message(message)
                .data(data)
                .build();
    }
}

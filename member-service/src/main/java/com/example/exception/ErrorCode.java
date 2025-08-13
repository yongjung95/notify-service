package com.example.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    DUPLICATE_MEMBER_ID(HttpStatus.CONFLICT, "중복된 ID입니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}

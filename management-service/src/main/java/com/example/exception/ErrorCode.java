package com.example.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND_KOREA_STOCK(HttpStatus.NOT_FOUND, "해당 주식 종목을 찾을 수 없습니다."),
    DUPLICATE_STOCK_MANAGEMENT(HttpStatus.CONFLICT, "이미 등록된 관심 종목입니다."),
    DUPLICATE_STOCK_PRICE_MANAGEMENT(HttpStatus.CONFLICT, "동일한 종목에 대한 가격 알림은 한 번만 설정할 수 있습니다."),
    STOCK_MANAGEMENT_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 관심 종목에 접근할 권한이 없습니다."),
    STOCK_PRICE_MANAGEMENT_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 종목의 가격 알림에 접근할 권한이 없습니다."),

    DUPLICATE_NEWS_MANAGEMENT(HttpStatus.CONFLICT, "이미 등록된 뉴스 키워드입니다."),
    NEWS_MANAGEMENT_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 뉴스 키워드에 접근할 권한이 없습니다."),
    NOT_FOUND_NEWS(HttpStatus.NOT_FOUND, "해당 뉴스 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}

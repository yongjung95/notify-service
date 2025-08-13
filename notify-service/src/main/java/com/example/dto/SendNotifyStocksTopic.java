package com.example.dto;

import lombok.Builder;

@Builder
public record SendNotifyStocksTopic(
        String ticker,
        String name,
        Double price,
        String memberUUID,
        String nickname,
        String fcmToken,
        Boolean isOpening
) {
    public static SendNotifyStocksTopic of(String ticker, String name, Double price, String memberUUID,
                                           String nickname, String fcmToken, Boolean isOpening) {
        return SendNotifyStocksTopic.builder()
                .ticker(ticker)
                .name(name)
                .price(price)
                .memberUUID(memberUUID)
                .nickname(nickname)
                .fcmToken(fcmToken)
                .isOpening(isOpening)
                .build();
    }
}

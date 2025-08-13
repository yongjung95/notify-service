package com.example.dto;

public record StocksManagementNotify(
        String ticker,
        String name,
        String exchangeCountry,
        String exchange,
        String memberUUID,
        String fcmToken,
        String nickname
) {
}

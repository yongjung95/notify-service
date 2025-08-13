package com.example.dto;

public record StocksRecord(
        Long id,
        String ticker,
        String name,
        String exchangeCountry,
        String exchange,
        String stocksType,
        boolean managementYn
) {
}

package com.example.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stocks extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ticker;

    private String name;

    private String exchangeCountry;

    private String exchange;

    @Enumerated(EnumType.STRING)
    private StocksType stocksType;

    public static Stocks of(String ticker, String name, String exchangeCountry, String exchange, StocksType stocksType) {
        return Stocks.builder()
                .ticker(ticker)
                .name(name)
                .exchangeCountry(exchangeCountry)
                .exchange(exchange)
                .stocksType(stocksType)
                .build();
    }
}

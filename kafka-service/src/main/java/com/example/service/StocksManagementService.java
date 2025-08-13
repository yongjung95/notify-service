package com.example.service;

import reactor.core.publisher.Mono;

public interface StocksManagementService {
    Mono<Void> sendKoreaStockManagement(Boolean isOpening);
    Mono<Void> sendAmericaStockManagement(Boolean isOpening);
}

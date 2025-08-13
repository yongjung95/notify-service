package com.example.service;

import reactor.core.publisher.Mono;

public interface NewsManagementService {
    Mono<Void> sendNewsManagement();
}

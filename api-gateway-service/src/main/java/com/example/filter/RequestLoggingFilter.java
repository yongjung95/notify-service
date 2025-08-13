package com.example.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        // 요청 시작 시간 기록
        long startTime = System.currentTimeMillis();

        // 요청 로깅
        logger.info("🔵 [REQUEST] ID: {} | {} {} | IP: {} | Time: {}",
                requestId,
                request.getMethod(),
                request.getURI(),
                getClientIp(request),
                LocalDateTime.now());

        // 요청 헤더에 추적 ID 추가
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-Request-Id", requestId)
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build())
                .doFinally(signalType -> {
                    // 응답 로깅
                    ServerHttpResponse response = exchange.getResponse();
                    long duration = System.currentTimeMillis() - startTime;

                    String statusEmoji = getStatusEmoji(response.getStatusCode().value());

                    logger.info("{} [RESPONSE] ID: {} | Status: {} | Duration: {}ms",
                            statusEmoji,
                            requestId,
                            response.getStatusCode(),
                            duration);
                });
    }

    private String getClientIp(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddress() != null ?
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    private String getStatusEmoji(int status) {
        if (status >= 200 && status < 300) return "🟢";
        if (status >= 300 && status < 400) return "🟡";
        if (status >= 400 && status < 500) return "🟠";
        if (status >= 500) return "🔴";
        return "⚪";
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}

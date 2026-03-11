package fr.matteofierquin.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@Slf4j
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        Instant startTime = Instant.now();
        String requestId = java.util.UUID.randomUUID().toString().substring(0, 8);
        
        log.info("[{}] {} {} {} started", 
                requestId, 
                request.getMethod(), 
                request.getPath(), 
                request.getRemoteAddress());

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            long duration = java.time.Duration.between(startTime, Instant.now()).toMillis();
            
            log.info("[{}] {} {} {} completed - Status: {} - Duration: {}ms",
                    requestId,
                    response.getStatusCode() != null ? response.getStatusCode() : "UNKNOWN",
                    request.getMethod(),
                    request.getPath(),
                    response.getStatusCode(),
                    duration);
        }));
    }

    @Override
    public int getOrder() {
        return -200; // Run before authentication filter
    }
}

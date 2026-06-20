package com.example.apigateway.service.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RateLimitMetricsService {

    private final MeterRegistry meterRegistry;

    private Counter exceededCounter;
    private Counter whitelistCounter;
    private Counter fallbackCounter;

    @PostConstruct
    public void init() {

        exceededCounter =
                Counter.builder("rate_limit_exceeded_total")
                        .description("Rate limit exceeded")
                        .register(meterRegistry);

        whitelistCounter =
                Counter.builder("rate_limit_whitelist_total")
                        .description("Whitelisted requests")
                        .register(meterRegistry);

        fallbackCounter =
                Counter.builder("rate_limit_fallback_total")
                        .description("Redis fallback count")
                        .register(meterRegistry);
    }

    public void incrementExceeded() {
        exceededCounter.increment();
    }

    public void incrementWhitelist() {
        whitelistCounter.increment();
    }

    public void incrementFallback() {
        fallbackCounter.increment();
    }

}

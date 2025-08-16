package com.positivewand.tourin.infrastructure.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TrendRateLimiter implements RateLimiter {
    private final RedisOperations<String, String> rateLimiterOps;

    @Override
    public boolean tryConsume(String key, int cost) {
        return Boolean.TRUE.equals(rateLimiterOps.opsForValue().setIfAbsent(
                getBucketKey(key),
                "1",
                Duration.between(
                        LocalDateTime.now(),
                        LocalDate.now().plusDays(1).atStartOfDay()
                )
        ));
    }

    private String getBucketKey(String key) {
        return "ratelimit:trend:"
                + LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.BASIC_ISO_DATE)
                + ":"
                + key;
    }
}

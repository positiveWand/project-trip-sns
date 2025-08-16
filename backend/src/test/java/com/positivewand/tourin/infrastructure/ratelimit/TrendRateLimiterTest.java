package com.positivewand.tourin.infrastructure.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrendRateLimiterTest {
    @Autowired
    TrendRateLimiter trendRateLimiter;

    @Test
    void 하루에_최대_한번_컨슘한다() {
        assertTrue(trendRateLimiter.tryConsume("test", 1));
        for (int i = 0; i < 5; i++) {
            assertFalse(trendRateLimiter.tryConsume("test", 1));
        }
    }
}
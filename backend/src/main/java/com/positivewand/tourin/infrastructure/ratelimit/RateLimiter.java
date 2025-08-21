package com.positivewand.tourin.infrastructure.ratelimit;

public interface RateLimiter {
    boolean tryConsume(String key, int cost);
}

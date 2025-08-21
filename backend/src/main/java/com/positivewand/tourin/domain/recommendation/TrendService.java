package com.positivewand.tourin.domain.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TrendService {
    private final RedisOperations<String, String> redisOps;

    private static final String TREND_TOPK_KEY = "recommendation:trend:topk";

    public static final int WINDOW_SIZE = 3;

    public void resetTrend() {
        ScanOptions options = ScanOptions.scanOptions().match("recommendation:trend:*").build();
        try (Cursor<String> cursor = redisOps.scan(options)) {
            while (cursor.hasNext()) {
                redisOps.delete(cursor.next());
            }
        }
    }

    public void incrementTrendScore(long tourSpotId, double delta) {
        redisOps.opsForZSet().incrementScore(TREND_TOPK_KEY, String.valueOf(tourSpotId), delta);
    }

    public List<Long> getTrendTopkIds(int kLimit) {
        return topkOperations.opsForZSet().reverseRange(TREND_TOPK_KEY, 0, kLimit-1)
                .stream().map(Long::valueOf).toList();
    }
}

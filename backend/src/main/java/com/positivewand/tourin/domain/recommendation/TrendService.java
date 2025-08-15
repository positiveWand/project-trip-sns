package com.positivewand.tourin.domain.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.positivewand.tourin.domain.RedisConfig.TREND_TOPK_KEY;

@Service
@RequiredArgsConstructor
public class TrendService {
    private final RedisOperations<String, String> topkOperations;

    public void resetTrendScore() {
        topkOperations.delete(TREND_TOPK_KEY);
    }

    public void incrementTrendScore(long tourSpotId, int delta) {
        topkOperations.opsForZSet().incrementScore(TREND_TOPK_KEY, String.valueOf(tourSpotId), delta);
    }

    public List<Long> getTrendTopkIds(int kLimit) {
        return topkOperations.opsForZSet().reverseRange(TREND_TOPK_KEY, 0, kLimit-1)
                .stream().map(Long::valueOf).toList();
    }
}

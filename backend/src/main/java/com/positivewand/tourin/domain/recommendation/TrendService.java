package com.positivewand.tourin.domain.recommendation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TrendService {
    private final RedisOperations<String, String> redisOps;
    private final ObjectMapper objectMapper;

    public static final String TREND_TOPK = "recommendation:trend:topk";
    public static final String TREND_TOPK_WINDOW = TREND_TOPK + ":window";
    public static final String TREND_TOPK_WINDOW_EPOCH = TREND_TOPK_WINDOW + ":epoch";
    public static final String TREND_TOPK_WINDOW_CURRENT_EPOCH = TREND_TOPK_WINDOW_EPOCH + ":current";

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
        redisOps.opsForZSet().incrementScore(TREND_TOPK_WINDOW_CURRENT_EPOCH, String.valueOf(tourSpotId), delta);
    }

    public List<Long> getTrendTopkIds(int kLimit) {
        try {
            String jsonString = redisOps.opsForValue().get(TREND_TOPK);
            if (jsonString == null) return Collections.emptyList();

            List<TrendItem> topkItems = objectMapper.readValue(jsonString, new TypeReference<List<TrendItem>>() {});
            return topkItems.stream().map(TrendItem::itemId).toList().subList(0, kLimit);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void slideTrendTopkWindow() throws JsonProcessingException {
        // current epoch에 epoch id 부여
        String lastEpochKey = redisOps.opsForList().getLast(TREND_TOPK_WINDOW);
        String newEpochKey = getNewEpochKey(lastEpochKey);

        // 새로운 epoch를 sliding window에 삽입
        redisOps.rename(TREND_TOPK_WINDOW_CURRENT_EPOCH, newEpochKey);
        redisOps.opsForList().rightPush(TREND_TOPK_WINDOW, newEpochKey);

        // 오래된 epoch을 sliding window에서 제거
        List<String> oldEpochKeys = redisOps.opsForList().range(TREND_TOPK_WINDOW, 0, -(WINDOW_SIZE+1));
        redisOps.delete(oldEpochKeys);
        redisOps.opsForList().trim(TREND_TOPK_WINDOW, -WINDOW_SIZE,-1);

        // window 집계
        aggregateAndStoreWindow();
    }

    public void aggregateAndStoreWindow() throws JsonProcessingException {
        List<TrendItem> topkIds = aggregateWindow();
        redisOps.opsForValue().set(TREND_TOPK, objectMapper.writeValueAsString(topkIds));
    }

    public List<TrendItem> aggregateWindow() {
        Map<Long, Double> score = new HashMap<>();
        
        // window를 구성하고 있는 epoch들을 집계
        List<String> epochList = redisOps.opsForList().range(TREND_TOPK_WINDOW, 0, -1);
        for (String epochKey: epochList) {
            ScanOptions options = ScanOptions.scanOptions().build();
            try (Cursor<ZSetOperations.TypedTuple<String>> cursor = redisOps.opsForZSet().scan(epochKey, options);) {
                while (cursor.hasNext()) {
                    ZSetOperations.TypedTuple<String> tuple = cursor.next();
                    long tourSpotId = Long.parseLong(tuple.getValue());

                    if (!score.containsKey(tourSpotId))
                        score.put(tourSpotId, 0.);

                    score.put(tourSpotId, score.get(tourSpotId) + tuple.getScore());
                }
            }
        }

        List<TrendItem> items = new ArrayList<>(score.entrySet()
                .stream()
                .map(entry -> new TrendItem(entry.getKey(), entry.getValue()))
                .toList()
        );
        items.sort(Comparator.reverseOrder());

        return items;
    }

    private int getNewId(int latestId) {
        return (latestId + 1) % 100;
    }

    private String getEpochKey(int id) {
        return TREND_TOPK_WINDOW_EPOCH + ":" + id;
    }

    private int parseEpochId(String epochKey) {
        return Integer.parseInt(epochKey.replace(TREND_TOPK_WINDOW_EPOCH+":", ""));
    }

    private String getNewEpochKey(String oldEpochKey) {
        if (oldEpochKey == null)
            return getEpochKey(0);

        int lastId = parseEpochId(oldEpochKey);
        int newId = getNewId(lastId);
        return getEpochKey(newId);
    }
}

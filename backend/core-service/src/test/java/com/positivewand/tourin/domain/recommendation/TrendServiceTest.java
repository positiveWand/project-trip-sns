package com.positivewand.tourin.domain.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisOperations;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.positivewand.tourin.domain.recommendation.TrendService.*;
import static com.positivewand.tourin.domain.recommendation.TrendService.TREND_TOPK_WINDOW_CURRENT_EPOCH;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TrendServiceTest {
    @Autowired
    private TrendService trendService;
    @Autowired
    private RedisOperations<String, String> redisOps;
    @Autowired
    private ObjectMapper objectMapper;

    Map<Long, Double> setTrendTopkWindow() throws Exception {
        // 관련 엔트리 초기화
        trendService.resetTrend();

        Map<Long, Double> scores = new HashMap<>();

        // 첫번째 epoch
        redisOps.opsForZSet().incrementScore(getEpochKey(0), "100000", 4000);
        scores.put(100000L, 4000.);
        redisOps.opsForZSet().incrementScore(getEpochKey(0), "100001", 3000);
        scores.put(100001L, 3000.);
        redisOps.opsForZSet().incrementScore(getEpochKey(0), "100002", 2000);
        scores.put(100002L, 2000.);
        redisOps.opsForZSet().incrementScore(getEpochKey(0), "100003", 1000);
        scores.put(100003L, 1000.);

        redisOps.opsForList().rightPush(TREND_TOPK_WINDOW, getEpochKey(0));

        // sliding window 내 다른 epoch
        for (int windowId = 1; windowId < WINDOW_SIZE; windowId++) {
            for (long tourSpotId = 0; tourSpotId < 100; tourSpotId++) {
                int score =  (int) (Math.random() * 100) + 1;
                redisOps.opsForZSet().incrementScore(getEpochKey(windowId), String.valueOf(tourSpotId), score);

                if (!scores.containsKey(tourSpotId))
                    scores.put(tourSpotId, 0.);
                scores.put(tourSpotId, scores.get(tourSpotId) + score);
            }
            redisOps.opsForList().rightPush(TREND_TOPK_WINDOW, getEpochKey(windowId));
        }

        trendService.aggregateAndStoreWindow();

        return scores;
    }

    String getEpochKey(int id) {
        return TREND_TOPK_WINDOW_EPOCH + ":" + id;
    }

    int parseEpochId(String epochKey) {
        return Integer.parseInt(epochKey.replace(TREND_TOPK_WINDOW_EPOCH+":", ""));
    }

    List<TrendItem> rankItems(Map<Long, Double> scores) {
        List<TrendItem> rank = new ArrayList<>(scores.entrySet()
                .stream()
                .map(entry -> new TrendItem(entry.getKey(), entry.getValue()))
                .toList()
        );
        rank.sort(Comparator.reverseOrder());

        return rank;
    }

    @Test
    void 트렌드_상위_k개_관광지가_선정된다() throws Exception {
        // given - 트렌드 점수 초기화
        trendService.resetTrend();
        
        // when - 50개의 관광지의 트렌드 점수를 무작위적으로 부여
        Map<Long, Long> score = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            for (long id = 0; id < 50; id++) {
                int inc = (int) (Math.random() * 100) + 1;
                trendService.incrementTrendScore(id, inc);
                if (!score.containsKey(id))
                    score.put(id, 0L);
                score.put(id, score.get(id) + inc);
            }
        }
        trendService.slideTrendTopkWindow();
        
        // then - 기대되는 순위와 Redis Sorted Set을 이용해 얻은 순위가 일치
        List<Map.Entry<Long, Long>> sortedList = new ArrayList<>(score.entrySet());
        // 내림차순 정렬(Redis의 Sorted Set은 score가 동점인 경우 value를 기준으로 정렬)
        sortedList.sort((e1, e2) -> {
            int c = e2.getValue().compareTo(e1.getValue());
            if (c != 0) return c;
            return e2.getKey().compareTo(e1.getKey());
        });
        System.out.println("[기대 관광지 점수 순 정렬]");
        System.out.println(sortedList);

        List<Long> recommended = trendService.getTrendTopkIds(50);
        System.out.println("[실제 관광지 점수 순 정렬]");
        System.out.println(recommended);
        
        assertEquals(sortedList.stream().map(Map.Entry::getKey).toList(), recommended);
    }

    @Test
    void 슬라이딩_윈도우_집계가_정확히_이뤄진다() throws Exception {
        // 트렌드 윈도우 상태 초기화
        Map<Long, Double> scores = setTrendTopkWindow();

        List<TrendItem> expected = rankItems(scores);

        List<TrendItem> actual = trendService.aggregateWindow();
        assertEquals(expected, actual);
    }

    @Test
    void 슬라이딩_윈도우_전진_전후_상태가_일관적이다() throws Exception {
        // 트렌드 윈도우 상태 초기화
        Map<Long, Double> windowScores = setTrendTopkWindow();

        // current epoch 상태 초기화
        Map<Long, Double> currentEpochScores = new HashMap<>();
        for (long tourSpotId = 0; tourSpotId < 100; tourSpotId++) {
            int score =  (int) (Math.random() * 100) + 1;
            redisOps.opsForZSet().incrementScore(TREND_TOPK_WINDOW_CURRENT_EPOCH, String.valueOf(tourSpotId), score);

            currentEpochScores.put(tourSpotId, (double) score);
        }

        // [before] - 슬라이딩 윈도우 전진 이전 상태 확인
        // 순위
        List<Long> beforeAggregatedRanking = rankItems(windowScores).stream().map(TrendItem::itemId).toList();
        List<Long> beforeRanking = trendService.getTrendTopkIds(100);
        assertEquals(beforeAggregatedRanking.subList(0, 100), beforeRanking);

        // epoch 순서
        List<String> beforeEpochList = redisOps.opsForList().range(TREND_TOPK_WINDOW, 0, -1);
        List<Integer> beforeEpochIdList = beforeEpochList.stream()
                .map(this::parseEpochId).toList();
        assertEquals(Arrays.asList(0, 1, 2), beforeEpochIdList);

        // [event] - 슬라이딩 윈도우 전진
        trendService.slideTrendTopkWindow();
        windowScores.remove(100000L);
        windowScores.remove(100001L);
        windowScores.remove(100002L);
        windowScores.remove(100003L);
        for (Map.Entry<Long, Double> entry: currentEpochScores.entrySet()) {
            long key = entry.getKey();
            windowScores.put(key, windowScores.get(key) + entry.getValue());
        }

        // [after] - 슬라이딩 윈도우 전진 이후 상태 확인
        // 순위 - 슬라이딩 윈도우를 집계한 순위 = 조회되는 순위
        List<Long> afterAggregatedRanking = rankItems(windowScores).stream().map(TrendItem::itemId).toList();
        List<Long> afterRanking = trendService.getTrendTopkIds(100);
        assertEquals(afterAggregatedRanking.subList(0, 100), afterRanking);

        // epoch 순서 - 빠져야할 epoch이 빠지고, 들어가야할 epoch이 들어갔는가
        List<String> afterEpochList = redisOps.opsForList().range(TREND_TOPK_WINDOW, 0, -1);
        List<Integer> afterEpochIdList = afterEpochList.stream()
                .map(this::parseEpochId).toList();
        assertEquals(Arrays.asList(1, 2, 3), afterEpochIdList);

        // 퇴출된 epoch는 제거됨
        boolean exist = redisOps.hasKey(getEpochKey(0));
        assertFalse(exist);

        // current epoch는 비어있음
        long cardinality = redisOps.opsForZSet().zCard(TREND_TOPK_WINDOW_CURRENT_EPOCH);
        assertEquals(0, cardinality);
    }

    @Test
    void 슬라이딩_윈도우가_전진해도_트렌드_점수는_누락되지_않는다() throws Exception {
        final int CLIENT_COUNT = 50;

        // 트렌드 윈도우 상태 초기화
        setTrendTopkWindow();

        // current epoch 상태 초기화
        for (long tourSpotId = 0; tourSpotId < 100; tourSpotId++) {
            int score = (int) (Math.random() * 100) + 1;
            redisOps.opsForZSet().incrementScore(TREND_TOPK_WINDOW_CURRENT_EPOCH, String.valueOf(tourSpotId), score);
        }

        final ExecutorService executorService = Executors.newFixedThreadPool(CLIENT_COUNT);
        final CountDownLatch latch = new CountDownLatch(CLIENT_COUNT);

        for (int i = 0; i < CLIENT_COUNT; i++) {
            log.info("{}번 클라이언트 실행", i);
            executorService.execute(() -> {
                for (int j = 0; j < 300; j++) {
                    long tourSpotId = (long) (Math.random() * 100) + 1;
                    int score = (int) (Math.random() * 50) + 1;
                    trendService.incrementTrendScore(tourSpotId, score);
                }
                latch.countDown();
            });

            // 윈도우 전진 중 경쟁상태가 발생할 수 있도록
            if (i == CLIENT_COUNT / 3) {
                // 슬라이딩 윈도우 전진
                log.info("윈도우 슬라이드!");
                trendService.slideTrendTopkWindow();
            }
        }

        latch.await();

        // 순위 - 슬라이딩 윈도우를 집계한 순위 = 조회되는 순위
        List<Long> aggregatedRanking = trendService.aggregateWindow().stream().map(TrendItem::itemId).toList();
        List<Long> ranking = trendService.getTrendTopkIds(100);
        assertEquals(aggregatedRanking.subList(0, 100), ranking);
    }
}
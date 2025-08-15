package com.positivewand.tourin.domain.recommendation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrendServiceTest {
    @Autowired
    private TrendService trendService;

    @Test
    void 트렌드_상위_k개_관광지가_선정된다() {
        // given - 트렌드 점수 초기화
        trendService.resetTrendScore();
        
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
        
        // then - 기대되는 순위와 Redis Sorted Set을 이용해 얻은 순위가 일치
        List<Map.Entry<Long, Long>> sortedList = new ArrayList<>(score.entrySet());
        // 내림차순 정렬(Redis의 Sorted Set은 score가 동점인 경우 value를 기준으로 정렬)
        sortedList.sort((e1, e2) -> {
            int c = e2.getValue().compareTo(e1.getValue());
            if (c != 0) return c;
            return String.valueOf(e2.getKey()).compareTo(String.valueOf(e1.getKey()));
        });
        System.out.println("[기대 관광지 점수 순 정렬]");
        System.out.println(sortedList);

        List<Long> recommended = trendService.getTrendTopkIds(50);
        System.out.println("[실제 관광지 점수 순 정렬]");
        System.out.println(recommended);
        
        assertEquals(sortedList.stream().map(Map.Entry::getKey).toList(), recommended);
    }
}
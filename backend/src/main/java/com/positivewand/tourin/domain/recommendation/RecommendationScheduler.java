package com.positivewand.tourin.domain.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationScheduler {
    private final TrendService trendService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    void slideTrendTopkWindow() {
        try {
            trendService.slideTrendTopkWindow();
        } catch (Exception e) {
            log.error("Job: Trend Top-K Slide Window 오류", e);
        }
    }
}

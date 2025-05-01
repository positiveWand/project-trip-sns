package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotReviewDto;
import com.positivewand.tourin.domain.user.UserService;
import com.positivewand.tourin.domain.user.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TourSpotReviewLikeServiceTest {
    @Autowired
    private TourSpotReviewLikeService tourSpotReviewLikeService;
    @Autowired
    private UserService userService;
    @Autowired
    private TourSpotReviewService tourSpotReviewService;
    @Autowired
    private TourSpotScheduler tourSpotScheduler;

    TourSpotReviewDto testTourSpotReview;

    @BeforeEach
    void 테스트_후기_삽입() {
        testTourSpotReview = tourSpotReviewService.addTourSpotReview(2751854L, "testuser", "너무 좋은 풍경입니다~!");
        log.info("테스트 관광지 후기 삽입 - ID: {}", testTourSpotReview.id());
    }

    @AfterEach
    void 테스트_후기_제거() {
        tourSpotReviewService.deleteTourSpotReview(testTourSpotReview.id());
    }

    @Test
    void 후기_좋아요를_동시에_요청해도_모두_올바르게_처리된다() throws InterruptedException {
        final int REQUEST_COUNT = 500;
        final long TEST_REVIEW_ID = testTourSpotReview.id();

        List<UserDto> users = userService.findUsers(0, REQUEST_COUNT).stream().toList();
        long beforeCachedLikeCount = tourSpotReviewService.findTourSpotReview(TEST_REVIEW_ID).likeCount();

        final ExecutorService executorService = Executors.newFixedThreadPool(REQUEST_COUNT);
        final CountDownLatch countDownLatch = new CountDownLatch(REQUEST_COUNT);

        final AtomicInteger successCount = new AtomicInteger();
        final AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < REQUEST_COUNT; i++) {
            final int userIdx = i;
            executorService.execute(() -> {
                UserDto user = users.get(userIdx);

                try {
                    tourSpotReviewLikeService.addReviewLike(user.username(), TEST_REVIEW_ID);
                    successCount.getAndIncrement();
                    log.info("{}번 사용자 좋아요 요청 성공", userIdx);
                } catch(Exception e) {
                    failCount.getAndIncrement();
                    log.info("{}번 사용자 좋아요 요청 실패, 실패 원인: {}", userIdx, e.getMessage());
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        tourSpotScheduler.syncTourSpotReviewLike();

        long afterCachedLikeCount = tourSpotReviewService.findTourSpotReview(TEST_REVIEW_ID).likeCount();

        log.info("성공한 좋아요 요청 수 = {}", successCount);
        log.info("실패한 좋아요 요청 수 = {}", failCount);
        log.info("이전 캐시 좋아요 수 = {}, 이후 캐시 좋아요 수 = {}", beforeCachedLikeCount, afterCachedLikeCount);

        assertEquals(beforeCachedLikeCount + successCount.get(), afterCachedLikeCount);
    }
}
package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import com.positivewand.tourin.domain.user.UserRepository;
import com.positivewand.tourin.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TourSpotSchedulerTest {
    @Autowired
    TourSpotRepository tourSpotRepository;
    @Autowired
    TourSpotReviewRepository tourSpotReviewRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TourSpotScheduler tourSpotScheduler;

    @Test
    @Transactional
    void 좋아요_수_업데이트_소요시간_측정() {
        List<TourSpotReview> tourSpotReviews = new ArrayList<>();

        TourSpot testTourSpot = tourSpotRepository.save(new TourSpot(
                10000000000L,
                "테스트 관광지",
                "테스트 관광지입니다.",
                "https://www.google.com",
                "주소, 상세주소",
                "주소",
                "상세주소",
                1,
                1,
                1.0,
                1.0,
                null
        ));

        User testUser = userRepository.save(User.create(
                "test",
                "password",
                "테스트",
                "test@example.com"
        ));

        log.info("테스트 관광지 및 유저 삽입");

        for (int i = 0; i < 10000; i++) {
            tourSpotReviews.add(TourSpotReview.create(
                    testTourSpot,
                    testUser,
                    "너무 좋은 풍경입니다~",
                    LocalDateTime.now(),
                    0L
            ));
        }
        tourSpotReviews = tourSpotReviewRepository.saveAll(tourSpotReviews);

        log.info("테스트 후기 삽입");

        for(TourSpotReview tourSpotReview: tourSpotReviews) {
            tourSpotReview.incrementLikeCount();
        }

        log.info("테스트 후기 좋아요");

        long startTime = System.currentTimeMillis();
        tourSpotScheduler.syncTourSpotReviewLike();
        tourSpotReviewRepository.flush();
        long endTime = System.currentTimeMillis();

        log.info("업데이트 소요 시간={}ms", endTime - startTime);
        for(TourSpotReview tourSpotReview: tourSpotReviews) {
            assertEquals(tourSpotReview.getLikeCount(), 1);
        }
    }
}
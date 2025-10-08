package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TourSpotScheduler {
    private final TourSpotReviewLikeService tourSpotReviewLikeService;
    private final TourSpotReviewRepository tourSpotReviewRepository;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void syncTourSpotReviewLike() {
        Map<Long, Long> differenceSnapshot = TourSpotReview.flushLikeCountBuffer();

        List<Long> keys = new ArrayList<>(differenceSnapshot.keySet());
        int chunkSize = 500;

        for (int i = 0; i < keys.size(); i += chunkSize) {
            tourSpotReviewLikeService.syncTourSpotReviewLike(
                    keys.subList(i, Math.min(i + chunkSize, keys.size())),
                    differenceSnapshot
            );
            tourSpotReviewRepository.flush();
        }
    }
}

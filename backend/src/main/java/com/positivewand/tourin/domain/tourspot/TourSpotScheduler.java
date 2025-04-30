package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TourSpotScheduler {
    private final TourSpotReviewRepository tourSpotReviewRepository;

    @Scheduled(cron = "0 0/1 * * * ?")
    @Transactional
    public void syncTourSpotReviewLike() {
        Map<Long, Long> differenceSnapshot = TourSpotReview.flushLikeCountBuffer();

        List<TourSpotReview> tourSpotReviews = new ArrayList<>();
        List<Long> keys = new ArrayList<>(differenceSnapshot.keySet());
        int chunkSize = 500;

        for (int i = 0; i < keys.size(); i += chunkSize) {
            List<TourSpotReview> chunk = tourSpotReviewRepository.findByIdIn(keys.subList(i, Math.min(i + chunkSize, keys.size())));
            tourSpotReviews.addAll(chunk);
        }
        for(TourSpotReview tourSpotReview: tourSpotReviews) {
            tourSpotReview.setLikeCount(tourSpotReview.getLikeCount()+differenceSnapshot.get(tourSpotReview.getId()));
        }
    }
}

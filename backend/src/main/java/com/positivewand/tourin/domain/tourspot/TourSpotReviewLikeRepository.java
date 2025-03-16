package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReviewLike;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TourSpotReviewLikeRepository extends JpaRepository<TourSpotReviewLike, TourSpotReviewLikeId> {
    void deleteByUserId(Long userId);
    void deleteByTourSpotReviewId(Long tourSpotReviewId);
    Long countByTourSpotReview(TourSpotReview tourSpotReview);
}

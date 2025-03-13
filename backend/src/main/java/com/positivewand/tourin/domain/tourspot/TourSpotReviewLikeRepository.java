package com.positivewand.tourin.domain.tourspot;

import com.positivewand.tourin.domain.tourspot.entity.TourSpotReviewLike;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TourSpotReviewLikeRepository extends JpaRepository<TourSpotReviewLike, TourSpotReviewLikeId> {
}

package com.positivewand.tourin.web.tourspot.response;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotReviewDto;

import java.time.LocalDateTime;

public record TourSpotReviewResponse(
        String id,
        String tourSpotId,
        String userId,
        LocalDateTime createdAt,
        String content,
        Long likeCount
) {
    public static TourSpotReviewResponse createFromDto(TourSpotReviewDto tourSpotReview) {
        return new TourSpotReviewResponse(
                tourSpotReview.id().toString(),
                tourSpotReview.tourSpotId().toString(),
                tourSpotReview.username(),
                tourSpotReview.createdAt(),
                tourSpotReview.content(),
                tourSpotReview.likeCount()
        );
    }
}

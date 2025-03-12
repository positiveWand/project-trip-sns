package com.positivewand.tourin.web.tourspot.response;

import com.positivewand.tourin.domain.tourspot.dto.TourSpotReviewDto;

public record TourSpotReviewResponse(
        String id,
        String tourSpotId,
        String userId,
        String content,
        Long likes
) {
    public static TourSpotReviewResponse createFromDto(TourSpotReviewDto tourSpotReview) {
        return new TourSpotReviewResponse(
                tourSpotReview.id().toString(),
                tourSpotReview.tourSpotId().toString(),
                tourSpotReview.username(),
                tourSpotReview.content(),
                0L
        );
    }
}

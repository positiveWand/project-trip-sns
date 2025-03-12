package com.positivewand.tourin.domain.tourspot.dto;

import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;

public record TourSpotReviewDto(
    Long id,
    Long tourSpotId,
    String username,
    String content
) {
    public static TourSpotReviewDto createFromEntity(TourSpotReview tourSpotReview) {
        return new TourSpotReviewDto(
            tourSpotReview.getId(),
            tourSpotReview.getTourSpot().getId(),
            tourSpotReview.getUser().getUsername(),
            tourSpotReview.getContent()
        );
    }
}

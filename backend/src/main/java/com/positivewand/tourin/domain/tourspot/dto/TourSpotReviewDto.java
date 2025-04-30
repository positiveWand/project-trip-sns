package com.positivewand.tourin.domain.tourspot.dto;

import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;

import java.time.LocalDateTime;

public record TourSpotReviewDto(
    Long id,
    Long tourSpotId,
    String username,
    LocalDateTime createdAt,
    String content,
    Long likeCount
) {
    public static TourSpotReviewDto create(TourSpotReview tourSpotReview) {
        return new TourSpotReviewDto(
            tourSpotReview.getId(),
            tourSpotReview.getTourSpot().getId(),
            tourSpotReview.getUser().getUsername(),
            tourSpotReview.getCreatedAt(),
            tourSpotReview.getContent(),
            tourSpotReview.getLikeCount()
        );
    }
}

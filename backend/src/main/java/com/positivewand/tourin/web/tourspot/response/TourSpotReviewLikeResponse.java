package com.positivewand.tourin.web.tourspot.response;

public record TourSpotReviewLikeResponse(
        String userId,
        String tourSpotReviewId,
        boolean liked
) {
}

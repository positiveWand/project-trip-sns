package com.positivewand.tourin.web.user.response;

public record TourSpotReviewLikeResponse(
        String userId,
        String tourSpotReviewId,
        boolean liked
) {

}

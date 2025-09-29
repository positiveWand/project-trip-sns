package com.positivewand.tourin.web.tourspot.request;

public record PutTourSpotReviewLikeRequest(
        String userId,
        boolean liked
) {
}

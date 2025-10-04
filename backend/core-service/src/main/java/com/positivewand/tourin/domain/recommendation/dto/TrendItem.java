package com.positivewand.tourin.domain.recommendation.dto;

public record TrendItem(
        String itemId,
        Double trendScore
) {
}

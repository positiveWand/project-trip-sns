package com.positivewand.tourin.event.trend;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TrendEvent(
       String id,
       String type,
       TrendData data,
       LocalDateTime timestamp
) {
}

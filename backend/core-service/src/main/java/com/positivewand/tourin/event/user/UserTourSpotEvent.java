package com.positivewand.tourin.event.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserTourSpotEvent(
       String id,
       String type,
       UserTourSpotData data,
       LocalDateTime timestamp
) {
}

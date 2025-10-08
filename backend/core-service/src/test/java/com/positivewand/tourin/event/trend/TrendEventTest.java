package com.positivewand.tourin.event.trend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
class TrendEventTest {
    @Test
    void 레코드_직렬화_결과_확인() throws Exception {
        TrendEvent event = new TrendEvent(
                UUID.randomUUID().toString(),
                "event.test",
                new TrendData(
                        "testuser",
                        "2020202"
                ),
                LocalDateTime.now()
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        log.info(objectMapper.writeValueAsString(event));
    }
}
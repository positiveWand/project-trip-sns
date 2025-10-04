package com.positivewand.tourin.event.trend;

import com.positivewand.tourin.infrastructure.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrendEventService {
    private final RabbitTemplate rabbitTemplate;

    public void publishTourspotViewEvent(String userId, String tourspotId) {
        TrendEvent event = new TrendEvent(
                UUID.randomUUID().toString(),
                "user.viewTourspot",
                new TrendData(
                        userId,
                        tourspotId
                ),
                LocalDateTime.now()
        );

        try {
            rabbitTemplate.convertAndSend(RabbitConfig.USER_EVENT_EXCHANGE, event);
        } catch (AmqpException e) {
            log.error("[이벤트] 관광지 조회 이벤트 출판 실패", e);
        }
    }

    public void publishTourspotBookmarkEvent(String userId, String tourspotId) {
        TrendEvent event = new TrendEvent(
                UUID.randomUUID().toString(),
                "user.bookmarkTourspot",
                new TrendData(
                        userId,
                        tourspotId
                ),
                LocalDateTime.now()
        );

        try {
            rabbitTemplate.convertAndSend(RabbitConfig.USER_EVENT_EXCHANGE, event);
        } catch (AmqpException e) {
            log.error("[이벤트] 관광지 조회 이벤트 출판 실패", e);
        }
    }
}

package com.positivewand.tourin.infrastructure;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String USER_EVENT_EXCHANGE = "event.user.ex";

    @Bean
    FanoutExchange userEventExchange() {
        return new FanoutExchange(USER_EVENT_EXCHANGE, false, false);
    }
}

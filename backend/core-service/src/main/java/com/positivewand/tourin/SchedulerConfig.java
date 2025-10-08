package com.positivewand.tourin;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile("production")
@EnableScheduling
public class SchedulerConfig {
}

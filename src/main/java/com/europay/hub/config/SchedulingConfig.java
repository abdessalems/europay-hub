package com.europay.hub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Enables {@code @Scheduled} tasks (e.g. payment expiry). */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}

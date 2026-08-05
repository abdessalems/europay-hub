package com.europay.hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * EuroPay Hub — a modern European Merchant Payment Platform.
 *
 * <p>Modular monolith built with Clean Architecture + DDD. Each bounded context
 * (feature) is self-contained under {@code features/*} and split into
 * {@code domain / application / infrastructure / presentation} layers.</p>
 */
@SpringBootApplication
public class EuroPayHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(EuroPayHubApplication.class, args);
    }
}

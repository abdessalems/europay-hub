package com.europay.hub.integration;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base for integration tests. Spins up a real PostgreSQL via Testcontainers and wires it
 * into Spring Boot with {@code @ServiceConnection} — no manual datasource properties needed.
 * Flyway migrations run against this container, giving true database fidelity.
 *
 * <p>The container is a singleton, started once per JVM from the static initialiser and
 * never stopped explicitly — Testcontainers' Ryuk sidecar removes it when the JVM exits.
 * It is deliberately <em>not</em> managed by {@code @Testcontainers}/{@code @Container}:
 * that extension stops a static container after each test class, while Spring keeps the
 * application context — and its connection pool — cached across classes. The second class
 * to run would then hold a pool pointing at a container that no longer exists, and fail
 * with "connection refused".
 */
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();
    }
}

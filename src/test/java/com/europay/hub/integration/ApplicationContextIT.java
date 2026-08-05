package com.europay.hub.integration;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: the full Spring context boots against a real PostgreSQL and Flyway migrates
 * cleanly. Named {@code *IT} so it runs in the Failsafe (integration-test) phase via
 * {@code mvn verify} — not in the fast Surefire {@code mvn test} phase.
 */
@SpringBootTest
@DisplayName("Application context (integration)")
class ApplicationContextIT extends AbstractIntegrationTest {

    @Autowired
    DataSource dataSource;

    @Test
    @DisplayName("boots and connects to the database")
    void contextLoads() {
        assertThat(dataSource).isNotNull();
    }
}

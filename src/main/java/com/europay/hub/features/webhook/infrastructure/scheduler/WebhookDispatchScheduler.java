package com.europay.hub.features.webhook.infrastructure.scheduler;

import com.europay.hub.features.webhook.application.WebhookDispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Periodically delivers due webhook events from the outbox. */
@Component
public class WebhookDispatchScheduler {

    private static final Logger log = LoggerFactory.getLogger(WebhookDispatchScheduler.class);

    private final WebhookDispatchService dispatchService;

    public WebhookDispatchScheduler(WebhookDispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @Scheduled(fixedDelayString = "${europay.webhook.dispatch-interval-ms:10000}")
    public void dispatch() {
        int handled = dispatchService.dispatchDue();
        if (handled > 0) {
            log.info("Dispatched {} webhook event(s)", handled);
        }
    }
}

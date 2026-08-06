package com.europay.hub.features.webhook.application;

import com.europay.hub.features.webhook.application.dto.WebhookEventResponse;
import com.europay.hub.features.webhook.domain.WebhookEventRepository;
import com.europay.hub.shared.web.PageResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookQueryService {

    private final WebhookEventRepository eventRepository;

    public WebhookQueryService(WebhookEventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<WebhookEventResponse> listEvents(UUID merchantId, int page, int size) {
        return PageResponse.of(eventRepository.findByMerchantId(merchantId, page, size)
                .map(WebhookEventResponse::from));
    }
}

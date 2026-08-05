package com.europay.hub.shared.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for DDD aggregate roots. Aggregates record {@link DomainEvent}s as their
 * state changes; the application layer pulls and publishes them once the aggregate is
 * successfully persisted (see the transactional outbox in the webhook module).
 *
 * @param <ID> the aggregate's identity type
 */
public abstract class AggregateRoot<ID> {

    private final transient List<DomainEvent> domainEvents = new ArrayList<>();

    public abstract ID id();

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /** Returns and clears the recorded events — call once, after persistence. */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> pulled = List.copyOf(domainEvents);
        domainEvents.clear();
        return pulled;
    }
}

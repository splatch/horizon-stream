package org.opennms.netmgt.eventd;

import java.util.Objects;

import org.opennms.horizon.events.api.AnnotationBasedEventListenerFactory;
import org.opennms.horizon.events.api.EventSubscriptionService;

public class AnnotationBasedEventListenerFactoryImpl implements AnnotationBasedEventListenerFactory {

    private final EventSubscriptionService eventSubscriptionService;

    public AnnotationBasedEventListenerFactoryImpl(EventSubscriptionService eventSubscriptionService) {
        this.eventSubscriptionService = Objects.requireNonNull(eventSubscriptionService);
    }

    @Override
    public AnnotationBasedEventListener createAnnotationBasedEventListener(Object annotatedListener) {
        return new AnnotationBasedEventListenerAdapter(annotatedListener, eventSubscriptionService);
    }
}

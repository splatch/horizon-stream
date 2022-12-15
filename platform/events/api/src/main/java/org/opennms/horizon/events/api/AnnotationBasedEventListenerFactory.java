package org.opennms.horizon.events.api;

public interface AnnotationBasedEventListenerFactory {

    interface AnnotationBasedEventListener {
        void start();
        void stop();
    }

    AnnotationBasedEventListener createAnnotationBasedEventListener(Object annotatedListener);

}

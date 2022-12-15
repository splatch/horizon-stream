package org.opennms.netmgt.eventd;

import org.opennms.horizon.events.api.EventdConfig;

/**
 * FIXME: OOPS: We should use CM here
 */
public class MockEventdConfig implements EventdConfig {

    public boolean shouldLogEventSummaries() {
        return true;
    }

        @Override
    public String getTCPIpAddress() {
        return null;
    }

    @Override
    public int getTCPPort() {
        return 0;
    }

    @Override
    public String getUDPIpAddress() {
        return null;
    }

    @Override
    public int getUDPPort() {
        return 0;
    }

    @Override
    public int getReceivers() {
        return 1;
    }

    @Override
    public int getQueueLength() {
        return 1;
    }

    @Override
    public String getSocketSoTimeoutRequired() {
        return null;
    }

    @Override
    public int getSocketSoTimeoutPeriod() {
        return 0;
    }

    @Override
    public boolean hasSocketSoTimeoutPeriod() {
        return false;
    }

    @Override
    public String getGetNextEventID() {
        return null;
    }

    @Override
    public int getNumThreads() {
        return 0;
    }

    @Override
    public int getQueueSize() {
        return 0;
    }

    @Override
    public int getBatchSize() {
        return 0;
    }

    @Override
    public int getBatchIntervalMs() {
        return 0;
    }
}

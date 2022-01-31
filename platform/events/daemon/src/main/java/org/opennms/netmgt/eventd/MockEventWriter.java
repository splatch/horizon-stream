package org.opennms.netmgt.eventd;

import org.opennms.horizon.events.xml.Log;
import org.opennms.netmgt.eventd.processor.EventWriter;

public class MockEventWriter implements EventWriter {
    @Override
    public void process(Log eventLog) {

    }

    @Override
    public void process(Log eventLog, boolean synchronous) {

    }
}

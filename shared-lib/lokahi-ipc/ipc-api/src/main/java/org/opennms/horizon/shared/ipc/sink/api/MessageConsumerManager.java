/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2016 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.horizon.shared.ipc.sink.api;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import com.google.protobuf.Message;

/**
 * Handles dispatching of messages to the registered consumer(s).
 *
 * @author jwhite
 */
public interface MessageConsumerManager {

    static final String LOG_PREFIX = "ipc";
    static final String METRIC_MESSAGES_RECEIVED = "messagesReceived";
    static final String METRIC_MESSAGE_SIZE = "messageSize";
    static final String METRIC_DISPATCH_TIME = "dispatchTime";

    <S extends Message, T extends Message> void dispatch(SinkModule<S, T> module, T message);

    <S extends Message, T extends Message> void registerConsumer(MessageConsumer<S, T> consumer);

    <S extends Message, T extends Message> void unregisterConsumer(MessageConsumer<S, T> consumer);

    static void updateMessageSize(MetricRegistry metricRegistry, String location, String moduleId, int messageSize) {
        Histogram messageSizeHistogram = metricRegistry.histogram(MetricRegistry.name(location, moduleId, METRIC_MESSAGE_SIZE));
        messageSizeHistogram.update(messageSize);
    }

    static Timer getDispatchTimerMetric(MetricRegistry metricRegistry, String location, String moduleId) {
        return metricRegistry.timer(MetricRegistry.name(location, moduleId, METRIC_DISPATCH_TIME));
    }

}

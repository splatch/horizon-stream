/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022-2023 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2023 The OpenNMS Group, Inc.
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
package org.opennms.horizon.minion.flows.parser;

import com.codahale.metrics.MetricRegistry;

import org.opennms.horizon.grpc.flows.contract.FlowDocument;
import org.opennms.horizon.minion.flows.listeners.Listener;
import org.opennms.horizon.minion.flows.listeners.Parser;
import org.opennms.horizon.minion.flows.listeners.factory.ListenerFactory;
import org.opennms.horizon.minion.flows.listeners.factory.TcpListenerFactory;
import org.opennms.horizon.minion.flows.listeners.factory.UdpListenerFactory;
import org.opennms.horizon.minion.flows.parser.factory.DnsResolver;
import org.opennms.horizon.minion.flows.parser.factory.IpfixTcpParserFactory;
import org.opennms.horizon.minion.flows.parser.factory.IpfixUdpParserFactory;
import org.opennms.horizon.minion.flows.parser.factory.Netflow5UdpParserFactory;
import org.opennms.horizon.minion.flows.parser.factory.Netflow9UdpParserFactory;
import org.opennms.horizon.minion.flows.parser.factory.ParserFactory;
import org.opennms.horizon.shared.ipc.rpc.IpcIdentity;
import org.opennms.horizon.shared.ipc.sink.api.AsyncDispatcher;
import org.opennms.horizon.shared.ipc.sink.api.MessageDispatcherFactory;
import org.opennms.sink.flows.contract.ListenerConfig;
import org.opennms.sink.flows.contract.ParserConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: this should be replace by using AlertingPluginRegistry to make plugins work.
// TODO: AlertingPluginRegistry needs de-registration to make dynamic loading work.
// TODO: And then this will replace TelemetryRegistry
public class TelemetryRegistryImpl implements TelemetryRegistry {
    private final List<ListenerFactory> listenerFactories = new ArrayList<>();
    private final List<ParserFactory> parserFactories = new ArrayList<>();

    private final AsyncDispatcher<FlowDocument> dispatcher;


    public TelemetryRegistryImpl(MessageDispatcherFactory messageDispatcherFactory,
                                 IpcIdentity identity,
                                 DnsResolver dnsResolver) {
        Objects.requireNonNull(messageDispatcherFactory);
        Objects.requireNonNull(identity);
        Objects.requireNonNull(dnsResolver);

        var sink = new FlowSinkModule(identity);
        this.dispatcher = messageDispatcherFactory.createAsyncDispatcher(sink);

        this.addListenerFactory(new UdpListenerFactory(this));
        this.addListenerFactory(new TcpListenerFactory(this));

        this.addParserFactory(new Netflow5UdpParserFactory(this, identity, dnsResolver));
        this.addParserFactory(new Netflow9UdpParserFactory(this, identity, dnsResolver));
        this.addParserFactory(new IpfixUdpParserFactory(this, identity, dnsResolver));
        this.addParserFactory(new IpfixTcpParserFactory(this, identity, dnsResolver));
    }

    @Override
    public void addListenerFactory(ListenerFactory factory) {
        Objects.requireNonNull(factory);
        this.listenerFactories.add(factory);
    }

    @Override
    public void addParserFactory(ParserFactory factory) {
        Objects.requireNonNull(factory);
        this.parserFactories.add(factory);
    }

    @Override
    public Listener createListener(ListenerConfig listenerConfig) {
        return this.listenerFactories.stream()
            .filter(factory -> factory.getListenerClass().getName().equals(listenerConfig.getClassName()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid parser class."))
            .create(listenerConfig);
    }

    @Override
    public Parser createParser(ParserConfig parserConfig) {
        return this.parserFactories.stream()
            .filter(factory -> factory.getParserClass().getName().equals(parserConfig.getClassName()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid parser class."))
            .create(parserConfig);
    }

    @Override
    public MetricRegistry getMetricRegistry() {
        return new MetricRegistry();
    }

    @Override
    public AsyncDispatcher<FlowDocument> getDispatcher() {
        return dispatcher;
    }
}

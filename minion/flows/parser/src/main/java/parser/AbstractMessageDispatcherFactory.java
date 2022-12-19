/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2016-2016 The OpenNMS Group, Inc.
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

package parser;

import java.util.Objects;

import org.osgi.framework.BundleContext;

import com.codahale.metrics.MetricRegistry;

import listeners.factory.AsyncDispatcher;
import listeners.factory.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * This class does all the hard work of building and maintaining the state of the message
 * dispatchers so that concrete implementations only need to focus on dispatching the messages.
 * <p>
 * Different types of dispatchers are created based on whether or not the module is using aggregation.
 * <p>
 * Asynchronous dispatchers use a queue and a thread pool to delegate to a suitable synchronous dispatcher.
 *
 * @param <W> type of module specific state or meta-data, use <code>Void</code> if none is used
 * @author jwhite
 */
@Slf4j
public abstract class AbstractMessageDispatcherFactory<W> implements MessageDispatcherFactory {

    public abstract <S extends Message, T extends Message> void dispatch(SinkModule<S, T> module, W metadata, T message);

    public abstract String getMetricDomain();

    public abstract BundleContext getBundleContext();

    public abstract MetricRegistry getMetrics();

    /**
     * Invokes dispatch within a timer context.
     */
    private <S extends Message, T extends Message> void timedDispatch(DispatcherState<W, S, T> state, T message) {
        log.info("Dispatching.. {}", message);
    }

    /**
     * Optionally build meta-data or state information for the module which will
     * be passed on all the calls to {@link #dispatch}.
     * <p>
     * This is useful for calculating things like message headers which are
     * re-used on every dispatch.
     *
     * @param module
     * @return
     */
    public <S extends Message, T extends Message> W getModuleMetadata(SinkModule<S, T> module) {
        return null;
    }

    @Override
    public <S extends Message, T extends Message> SyncDispatcher<S> createSyncDispatcher(SinkModule<S, T> module) {
        Objects.requireNonNull(module, "module cannot be null");
        final DispatcherState<W, S, T> state = new DispatcherState<>(this, module);
        return createSyncDispatcher(state);
    }

    @Override
    public <S extends Message, T extends Message> AsyncDispatcher<S> createAsyncDispatcher(SinkModule<S, T> module) {
        Objects.requireNonNull(module, "module cannot be null");
        Objects.requireNonNull(module.getAsyncPolicy(), "module must have an AsyncPolicy");
        final DispatcherState<W, S, T> state = new DispatcherState<>(this, module);
        final SyncDispatcher<S> syncDispatcher = createSyncDispatcher(state);
        return new AsyncDispatcherImpl<>(state, module.getAsyncPolicy(), syncDispatcher);
    }

    protected <S extends Message, T extends Message> SyncDispatcher<S> createSyncDispatcher(DispatcherState<W, S, T> state) {
        final SinkModule<S, T> module = state.getModule();
        if (module.getAggregationPolicy() != null) {
            // Aggregate the message before dispatching them
            return new AggregatingSinkMessageProducer<S, T>(module) {
                @Override
                public void dispatch(T message) {
                    AbstractMessageDispatcherFactory.this.timedDispatch(state, message);
                }

                @Override
                public void close() throws Exception {
                    super.close();
                    state.close();
                }

            };
        } else {
            // No aggregation strategy is set, dispatch directly to reduce overhead
            return new DirectDispatcher<>(state);
        }
    }

    private class DirectDispatcher<S extends Message, T extends Message> implements SyncDispatcher<S> {
        private final DispatcherState<W, S, T> state;

        public DirectDispatcher(DispatcherState<W, S, T> state) {
            this.state = state;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void send(S message) {
            // Cast S to T, modules that do not use an AggregationPolicty
            // must have the same types for S and T
            AbstractMessageDispatcherFactory.this.timedDispatch(state, (T) message);
        }

        @Override
        public void close() throws Exception {
            state.close();
        }
    }
}

/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
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

package org.opennms.horizon.minion.flows.parser.ie.values;

import static org.opennms.horizon.minion.flows.listeners.utils.BufferUtils.uint;

import java.util.Objects;
import java.util.Optional;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.UnsignedLong;

import io.netty.buffer.ByteBuf;
import org.opennms.horizon.minion.flows.parser.ie.InformationElement;
import org.opennms.horizon.minion.flows.parser.ie.Semantics;
import org.opennms.horizon.minion.flows.parser.ie.Value;
import org.opennms.horizon.minion.flows.parser.session.Session;

public class UnsignedValue extends Value<UnsignedLong> {
    private final UnsignedLong value;

    public UnsignedValue(final String name,
                         final Optional<Semantics> semantics,
                         final UnsignedLong value) {
        super(name, semantics);
        this.value = Objects.requireNonNull(value);
    }

    public UnsignedValue(final String name,
                         final long value) {
        this(name, Optional.empty(), UnsignedLong.valueOf(value));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", getName())
                .add("value", value)
                .toString();
    }

    public static InformationElement parserWith8Bit(final String name, final Optional<Semantics> semantics) {
        return new InformationElement() {
            @Override
            public Value<?> parse(final Session.Resolver resolver, final ByteBuf buffer) {
                return new UnsignedValue(name, semantics, uint(buffer, 1));
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMinimumFieldLength() {
                return 0;
            }

            @Override
            public int getMaximumFieldLength() {
                return 1;
            }
        };
    }

    public static InformationElement parserWith16Bit(final String name, final Optional<Semantics> semantics) {
        return new InformationElement() {
            @Override
            public Value<?> parse(final Session.Resolver resolver, final ByteBuf buffer) {
                return new UnsignedValue(name, semantics, uint(buffer, buffer.readableBytes()));
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMinimumFieldLength() {
                return 0;
            }

            @Override
            public int getMaximumFieldLength() {
                return 2;
            }
        };
    }

    public static InformationElement parserWith24Bit(final String name, final Optional<Semantics> semantics) {
        return new InformationElement() {
            @Override
            public Value<?> parse(final Session.Resolver resolver, final ByteBuf buffer) {
                return new UnsignedValue(name, semantics, uint(buffer, buffer.readableBytes()));
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMinimumFieldLength() {
                return 0;
            }

            @Override
            public int getMaximumFieldLength() {
                return 3;
            }
        };
    }

    public static InformationElement parserWith32Bit(final String name, final Optional<Semantics> semantics) {
        return new InformationElement() {
            @Override
            public Value<?> parse(final Session.Resolver resolver, final ByteBuf buffer) {
                return new UnsignedValue(name, semantics, uint(buffer, buffer.readableBytes()));
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMinimumFieldLength() {
                return 0;
            }

            @Override
            public int getMaximumFieldLength() {
                return 4;
            }
        };
    }

    public static InformationElement parserWith64Bit(final String name, final Optional<Semantics> semantics) {
        return new InformationElement() {
            @Override
            public Value<?> parse(final Session.Resolver resolver, final ByteBuf buffer) {
                return new UnsignedValue(name, semantics, uint(buffer, buffer.readableBytes()));
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getMinimumFieldLength() {
                return 0;
            }

            @Override
            public int getMaximumFieldLength() {
                return 8;
            }
        };
    }

    @Override
    public UnsignedLong getValue() {
        return this.value;
    }

    @Override
    public void visit(final Visitor visitor) {
        visitor.accept(this);
    }
}

/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2002-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
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

package org.opennms.horizon.minion.icmp.proxy.common.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.StringJoiner;

/**
 * <p>
 * This class is used to represent the polling information needed by the
 * discovery process. Each instance encapsulates an internet address, timeout in
 * milliseconds, and a retry count.
 * </p>
 *
 * @author <A HREF="mailto:sowmya@opennms.org">Sowmya </A>
 * @author <A HREF="mailto:weave@oculan.com">Brian Weaver </A>
 */
public class IPPollAddress implements Serializable {

    private static final long serialVersionUID = -4162816651553193934L;

    /**
     * Foreign source where this address should be persisted.
     */
    private final String m_foreignSource;

    /**
     * Network location of this address.
     */
    private final String m_location;

    /**
     * The dotted decimal IPv4 address for the poll.
     */
    private final InetAddress m_address; // dotted IP m_address

    /**
     * The timeout for the poller in 1/1000th of a second.
     */
    private final long m_timeout;

    /**
     * The number of times to attempt to contact the remote.
     */
    private final int m_retries;

    /**
     * <P>
     * Constructs an IPPollAddress object with the specified parameters.
     * </P>
     * 
     * @param ipAddress
     *            The Dotted Decimal IPv4 Address.
     * @param timeout
     *            The timeout between retries in 1/1000th of a second.
     * @param retries
     *            The number of times to attempt to contact the address.
     * 
     */
    public IPPollAddress(final String foreignSource, final String location, final InetAddress ipAddress, final long timeout, final int retries) {
        m_foreignSource = foreignSource;
        m_location = location;
        m_address = ipAddress;
        m_timeout = timeout;
        m_retries = retries;
    }

    /**
     * Foreign source where this address should be persisted.
     */
    public String getForeignSource() {
        return m_foreignSource;
    }

    /**
     * Network location of this address.
     */
    public String getLocation() {
        return m_location;
    }

    /**
     * <P>
     * Returns the timeout in 1/1000th of a second increments.
     * </P>
     *
     * @return The timeout associated with the host in 1/1000th of a second.
     */
    public long getTimeout() {
        return m_timeout;
    }

    /**
     * <P>
     * Returns the current number of retries set for this address.
     * </P>
     *
     * @return The retry count for the instance.
     */
    public int getRetries() {
        return m_retries;
    }

    /**
     * Returns the internet address encapsulated in the object.
     *
     * @return The encapsulated internet address.
     */
    public InetAddress getAddress() {
        return m_address;
    }

    /**
     * <P>
     * Returns true if the passed object is equal to self. The objects must be
     * equal in address, timeout, and the number of retries.
     * </P>
     *
     * @return True if the objects are logically equal. False is returned otherwise.
     * @param pollAddr a {@link IPPollAddress} object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object instanceof IPPollAddress) {
            IPPollAddress pollAddr = (IPPollAddress)object;
            if (pollAddr != null) {
                if (pollAddr == this) {
                    return true;
                } else if (pollAddr.getAddress().equals(m_address) && pollAddr.getRetries() == m_retries && pollAddr.getTimeout() == m_timeout) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IPPollAddress.class.getSimpleName() + "[", "]")
                .add("m_foreignSource='" + m_foreignSource + "'")
                .add("m_location='" + m_location + "'")
                .add("m_address=" + m_address)
                .add("m_timeout=" + m_timeout)
                .add("m_retries=" + m_retries)
                .toString();
    }
}

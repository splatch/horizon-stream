/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
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

package org.opennms.minion.icmp.jni6;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet6Address;

import org.opennms.horizon.shared.logging.Logging;
import org.opennms.protocols.icmp6.ICMPv6EchoReply;
import org.opennms.protocols.icmp6.ICMPv6Packet;
import org.opennms.protocols.icmp6.ICMPv6Packet.Type;
import org.opennms.protocols.icmp6.ICMPv6Socket;
import org.opennms.protocols.rt.Messenger;
import org.opennms.protocols.rt.ReplyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JniIcmpMessenger
 *
 * @author brozow
 * @version $Id: $
 */
public class Jni6IcmpMessenger implements Messenger<Jni6PingRequest, Jni6PingResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(Jni6IcmpMessenger.class);
    
    private int m_pingerId;
    private ICMPv6Socket m_socket;
    
    /**
     * <p>Constructor for JniIcmpMessenger.</p>
     * @param pingerId 
     *
     * @throws java.io.IOException if any.
     */
    public Jni6IcmpMessenger(int pingerId) throws IOException {
        m_pingerId = pingerId;
        m_socket = new ICMPv6Socket(Integer.valueOf(pingerId).shortValue());
    }

    void processPackets(ReplyHandler<Jni6PingResponse> callback) {
        while (true) {
            try {
                DatagramPacket packet = m_socket.receive();
                
                Jni6PingResponse reply = Jni6IcmpMessenger.createPingResponse(packet);
                
                if (reply != null && reply.getIdentifier() == m_pingerId) {
                    callback.handleReply(reply);
                }

     
            } catch (IOException e) {
                LOG.error("I/O Error occurred reading from ICMP Socket", e);
            } catch (IllegalArgumentException e) {
                // this is not an EchoReply so ignore it
            } catch (IndexOutOfBoundsException e) {
                // this packet is not a valid EchoReply ignore it
            } catch (Throwable e) {
                LOG.error("Unexpected Exception processing reply packet!", e);
            }
        }

    }

    
    /**
     * <p>sendRequest</p>
     *
     * @param request a {@link Jni6PingRequest} object.
     */
    @Override
    public void sendRequest(Jni6PingRequest request) {
        request.send(m_socket);
    }

    /** {@inheritDoc} */
    @Override
    public void start(final ReplyHandler<Jni6PingResponse> callback) {
        Thread socketReader = new Thread("JNI-ICMP-"+m_pingerId+"-Socket-Reader") {

            @Override
            public void run() {
                Logging.putPrefix("icmp");
                try {
                    processPackets(callback);
                } catch (Throwable t) {
                    LOG.error("Unexpected exception on Thread {}!", this, t);
                }
            }
        };
        socketReader.setDaemon(true);
        socketReader.start();
    }

    /**
     * <p>
     * Creates a new instance of the class using the passed datagram as the data
     * source. The address and ping packet are extracted from the datagram and
     * returned as a new instance of the class. In addition to extracting the
     * packet, the packet's received time is updated to the current time.
     * </p>
     *
     * <p>
     * If the received datagram is not an echo reply or an incorrect length then
     * an exception is generated to alert the caller.
     * </p>
     *
     * @param packet
     *            The packet with the ICMP datagram.
     * @throws java.lang.IllegalArgumentException
     *             Throw if the datagram is not the correct length or type.
     * @throws java.lang.IndexOutOfBoundsException
     *             Thrown if the datagram does not contain sufficient data.
     * @return a {@link  Jni6PingResponse} object.
     */
    public static Jni6PingResponse createPingResponse(DatagramPacket packet) {

        ICMPv6Packet icmpPacket = new ICMPv6Packet(packet.getData(), packet.getOffset(), packet.getLength());

        if (icmpPacket.getType() != Type.EchoReply) return null;

        ICMPv6EchoReply echoReply = new ICMPv6EchoReply(icmpPacket);

        if (!echoReply.isEchoReply() || !echoReply.isValid()) return null;

        Inet6Address address = (Inet6Address) packet.getAddress();

        return new Jni6PingResponse(address, echoReply);
    }


    public void setTrafficClass(int tc) throws IOException {
        try {
            m_socket.setTrafficClass(tc);
        } catch (final IOException e) {
            LOG.error("Failed to set traffic class {} on ICMPv6 socket.", tc, e);
        }
    }

    public void setAllowFragmentation(final boolean allow) throws IOException {
        if (!allow) {
            try {
                m_socket.dontFragment();
            } catch (final IOException e) {
                LOG.error("Failed to set 'Don't Fragment' bit on ICMPv6 socket.", e);
            }
        }
    }
}

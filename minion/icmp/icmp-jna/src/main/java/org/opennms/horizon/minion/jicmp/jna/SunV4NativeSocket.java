/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2014 The OpenNMS Group, Inc.
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

package org.opennms.horizon.minion.jicmp.jna;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * UnixNativeSocketFactory
 *
 * @author brozow
 */
public class SunV4NativeSocket extends NativeDatagramSocket {
    
    static {
        Native.register((String)null);
    }

    private static final int IP_TOS = 3;
    private final int m_sock;

    public SunV4NativeSocket(int family, int type, int protocol, int listenPort) throws Exception {
        m_sock = socket(family, type, protocol);
        final sockaddr_in addr_in = new sockaddr_in(listenPort);
        bind(m_sock, addr_in, addr_in.size());
    }

    public native int bind(int socket, sockaddr_in address, int address_len) throws LastErrorException;

    public native int socket(int domain, int type, int protocol) throws LastErrorException;

    public native int setsockopt(int socket, int level, int option_name, Pointer value, int option_len);

    public native int sendto(int socket, Buffer buffer, int buflen, int flags, sockaddr_in dest_addr, int dest_addr_len) throws LastErrorException;

    public native int recvfrom(int socket, Buffer buffer, int buflen, int flags, sockaddr_in in_addr, int[] in_addr_len) throws LastErrorException;

    public native int close(int socket) throws LastErrorException;

    @Override
    public int getSock() {
        return m_sock;
    }

    @Override
    public void setTrafficClass(final int tc) throws IOException {
        final IntByReference tc_ptr = new IntByReference(tc);
        try {
            setsockopt(getSock(), IPPROTO_IP, IP_TOS, tc_ptr.getPointer(), Pointer.SIZE);
        } catch (final LastErrorException e) {
            throw new IOException("setsockopt: " + strerror(e.getErrorCode()));
        }
    }

    @Override
    public void allowFragmentation(final boolean frag) throws IOException {
        allowFragmentation(IPPROTO_IP, IP_MTU_DISCOVER, frag);
    }

    @Override
    public int receive(NativeDatagramPacket p) {
        sockaddr_in in_addr = new sockaddr_in();
        int[] szRef = new int[] { in_addr.size() };
        
        ByteBuffer buf = p.getContent();
        
        int n = recvfrom(getSock(), buf, buf.capacity(), 0, in_addr, szRef);
        p.setLength(n);
        p.setAddress(in_addr.getAddress());
        p.setPort(in_addr.getPort());
        
        return n;
    }

    @Override
    public int send(NativeDatagramPacket p) {
        sockaddr_in destAddr = new sockaddr_in(p.getAddress(), p.getPort());
        ByteBuffer buf = p.getContent();
        return sendto(getSock(), buf, buf.remaining(), 0, destAddr, destAddr.size());
    }

    @Override
    public void close() {
        close(getSock());
    }

}

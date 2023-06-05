/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2023 The OpenNMS Group, Inc.
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


package org.opennms.horizon.minion.flows.shell;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.opennms.horizon.minion.flows.listeners.TcpParser;
import org.opennms.horizon.minion.flows.listeners.UdpParser;
import org.opennms.horizon.minion.flows.parser.TelemetryRegistry;

/**
 * Shell command to clear parsers sessions to avoid templates inconsistencies
 */
@Command(scope = "opennms", name = "clear-session", description = "Clear Parsers Sessions to avoid templates inconsistencies")
@Service
public class ClearSessionCmd implements Action {

    @Reference
    TelemetryRegistry registry;

    @Option(name = "-s", aliases = "--protocol", description = "specify protocol name")
    String protocolShortName = "udp";

    @Option(name = "-f", aliases = "--feature", description = "specify feature short name")
    String featureShortName = "not-specified";

    @Option(name = "-p", aliases = "--port", description = "specify protocol short name")
    int portNumber;


    @Override
    public Object execute() throws Exception {
        if (StringUtils.isBlank(featureShortName)) {
            System.out.println("Please specify a valid feature, e.g. -f N9 or --feature N5");
            return null;
        }

        if (portNumber == 0) {
            System.out.println("Please specify a valid port, e.g. -p 1234 or --port 1234");
            return null;
        }

        // Tcp Sessions
        if (protocolShortName.equals("tcp")) {
            // Get Tcp Parsers
            List<TcpParser> allTcpParsers = registry.getTcpParsers();

            if (allTcpParsers.isEmpty()) {
                System.out.printf("The given feature %s could not be matched with any of the following active parsers: %s",
                    featureShortName, allTcpParsers);
                return null;
            }

            // Empty Session for retrieved parsers
            allTcpParsers.forEach(tcpParser -> tcpParser.getSessions().stream()
                .filter(session ->
                    session.getLocalAddress().getPort() == portNumber
                ).forEach(foundSession -> {
                    System.out.printf("Found matching session for local address %s to be dropped.", foundSession.getLocalAddress());
                    tcpParser.dumpInternalState();
                }));
        } else {
            // Udp Sessions
            // Get Udp Parsers
            List<UdpParser> allUdpParsers = registry.getUdpParsers();
            List<UdpParser> udpParsers = allUdpParsers.stream().filter(parser -> featureShortName.equals(parser.getShortName())).toList();

            if (udpParsers.isEmpty()) {
                System.out.printf("The given feature %s could not be matched with any of the following active parsers: %s",
                    featureShortName, udpParsers);
                return null;
            }

            // Retrieve active sessions from parsers
            udpParsers.forEach(udpParser -> udpParser.getSessionKeyHashMap().entrySet()
                .stream()
                .filter(entry ->
                    entry.getKey().getLocalAddress().getPort() == portNumber
                ).forEach(foundEntry -> {
                    System.out.printf("Found matching session with session key %s to be dropped.", foundEntry.getKey());
                    udpParser.getSessionManager().drop(foundEntry.getKey());
                    udpParser.getSessionKeyHashMap().clear();
                }));
        }

        System.out.printf("Sessions for protocol %s and feature %s successfully dropped.", protocolShortName, featureShortName);
        return null;
    }
}

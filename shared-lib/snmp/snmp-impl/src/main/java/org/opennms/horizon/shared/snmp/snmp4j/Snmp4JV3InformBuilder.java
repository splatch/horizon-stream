/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2005-2014 The OpenNMS Group, Inc.
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

package org.opennms.horizon.shared.snmp.snmp4j;

import org.opennms.horizon.shared.snmp.SnmpConfiguration;
import org.opennms.horizon.shared.snmp.SnmpV3TrapBuilder;
import org.opennms.horizon.shared.snmp.SnmpValue;
import org.snmp4j.ScopedPDU;

public class Snmp4JV3InformBuilder extends Snmp4JV2TrapBuilder implements SnmpV3TrapBuilder {
    
    protected Snmp4JV3InformBuilder(Snmp4JStrategy strategy) {
        super(strategy, new ScopedPDU(), ScopedPDU.INFORM);
    }
    
    @Override
    public SnmpValue[] sendInform(String destAddr, int destPort, int timeout, int retry, String community) throws Exception {
    	return super.sendInform(destAddr, destPort, 1000, 3, SnmpConfiguration.NOAUTH_NOPRIV, community, SnmpConfiguration.DEFAULT_AUTH_PASS_PHRASE,
    			SnmpConfiguration.DEFAULT_AUTH_PROTOCOL, SnmpConfiguration.DEFAULT_PRIV_PASS_PHRASE, SnmpConfiguration.DEFAULT_PRIV_PROTOCOL);
    }  
}

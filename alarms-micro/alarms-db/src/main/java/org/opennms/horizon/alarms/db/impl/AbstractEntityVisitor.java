/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2014 The OpenNMS Group, Inc.
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

package org.opennms.horizon.alarms.db.impl;

import org.opennms.horizon.alarms.db.api.EntityVisitor;
import org.opennms.horizon.alarms.db.impl.dto.EntityDTO;
import org.opennms.horizon.alarms.db.impl.dto.IpInterfaceDTO;
import org.opennms.horizon.alarms.db.impl.dto.MonitoredServiceDTO;

/**
 * <p>
 * AbstractEntityVisitor class.
 * </p>
 */
public class AbstractEntityVisitor implements EntityVisitor {

    /** {@inheritDoc} */
    @Override
    public void visitSnmpInterface(final EntityDTO snmpIface) {
    }

    /** {@inheritDoc} */
    @Override
    public void visitIpInterface(final IpInterfaceDTO iface) {
    }

    /** {@inheritDoc} */
    @Override
    public void visitMonitoredService(final MonitoredServiceDTO monSvc) {
    }

    /** {@inheritDoc} */
    @Override
    public void visitSnmpInterfaceComplete(final EntityDTO snmpIface) {
    }

    /** {@inheritDoc} */
    @Override
    public void visitIpInterfaceComplete(final IpInterfaceDTO iface) {
    }

    /** {@inheritDoc} */
    @Override
    public void visitMonitoredServiceComplete(final MonitoredServiceDTO monSvc) {
    }

}

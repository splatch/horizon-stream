/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2017 The OpenNMS Group, Inc.
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

package org.opennms.horizon.events.conf.xml;


import org.opennms.horizon.events.util.ConfigUtils;
import org.opennms.horizon.events.util.ValidateUsing;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Objects;

/**
 * The SNMP information from the trap
 */
@XmlRootElement(name="snmp")
@XmlAccessorType(XmlAccessType.NONE)
@ValidateUsing("eventconf.xsd")
@XmlType(propOrder={"m_id", "m_idText", "m_version", "m_specific", "m_generic", "m_community", "m_trapOID"})
public class Snmp implements Serializable {
    private static final long serialVersionUID = 2L;

    /**
     * The SNMP enterprise ID
     */
    // @NotNull
    @XmlElement(name="id", required=true)
    private String m_id;

    @XmlElement(name="trapoid")
    private String m_trapOID;

    /**
     * The SNMP enterprise ID text
     */
    @XmlElement(name="idtext", required=false)
    private String m_idText;

    /**
     * The SNMP version
     */
    // @NotNull
    @XmlElement(name="version", required=true)
    private String m_version;

    /**
     * The specific trap number
     */
    @XmlElement(name="specific", required=false)
    private Integer m_specific;

    /**
     * The generic trap number
     */
    @XmlElement(name="generic", required=false)
    private Integer m_generic;

    /**
     * The community name
     */
    @XmlElement(name="community", required=false)
    private String m_community;

    /** The SNMP enterprise ID */
    public String getId() {
        return m_id;
    }

    public void setId(final String id) {
        m_id = ConfigUtils.assertNotEmpty(id, "id");
    }

    public String getTrapOID() {
        return m_trapOID;
    }

    public void setTrapOID(String m_trapOID) {
        this.m_trapOID = m_trapOID;
    }

    /** The SNMP enterprise ID text */
    public String getIdtext() {
        return m_idText;
    }

    public void setIdtext(final String idText) {
        m_idText = ConfigUtils.normalizeString(idText);
    }

    /** The SNMP version */
    public String getVersion() {
        return m_version;
    }

    public void setVersion(final String version) {
        m_version = ConfigUtils.assertNotEmpty(version, "version");
    }

    /** The specific trap number */
    public Integer getSpecific() {
        return m_specific;
    }

    public void setSpecific(final Integer specific) {
        m_specific = specific;
    }

    /** The generic trap number. */
    public Integer getGeneric() {
        return m_generic;
    }

    public void setGeneric(final Integer generic) {
        m_generic = generic;
    }

    public String getCommunity() {
        return m_community;
    }

    public void setCommunity(final String community) {
        m_community = ConfigUtils.normalizeString(community);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_id, m_idText, m_version, m_specific, m_generic, m_community, m_trapOID);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Snmp) {
            final Snmp that = (Snmp) obj;
            return Objects.equals(this.m_id, that.m_id) &&
                    Objects.equals(this.m_idText, that.m_idText) &&
                    Objects.equals(this.m_version, that.m_version) &&
                    Objects.equals(this.m_specific, that.m_specific) &&
                    Objects.equals(this.m_generic, that.m_generic) &&
                    Objects.equals(this.m_community, that.m_community) &&
                    Objects.equals(this.m_trapOID, that.m_trapOID);
        }
        return false;
    }

}

/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2007-2014 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.provision.persistence.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Data;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class ForeignSourceDTO implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(ForeignSourceDTO.class);
    private static final long serialVersionUID = -1903289015976502808L;

    private String name;
    protected Date dateStamp;
    private Duration scanInterval = Duration.standardDays(1);
    private Map<String, PluginConfigDTO> detectors = new HashMap<>();
    private Map<String, PluginConfigDTO> policies = new HashMap<>();
    private boolean defaultForeignSource;

    /**
     * <p>Constructor for ForeignSource.</p>
     */
    public ForeignSourceDTO() {
        updateDateStamp();
    }

    /**
     * <p>Constructor for ForeignSource.</p>
     *
     * @param name a {@link String} object.
     */
    public ForeignSourceDTO(final String name) {
        this();
        setName(name);
    }

    public Date getDateStampAsDate() {
//        return dateStamp == null ? null : dateStamp.toGregorianCalendar().getTime();
        return dateStamp;
    }

    /**
     * Update the date stamp to the current date and time
     */
    public void updateDateStamp() {
        this.dateStamp = new Date();
//        try {
//            dateStamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
//        } catch (final DatatypeConfigurationException e) {
//            LOG.warn("unable to update datestamp", e);
//        }
    }

    public List<String> getDetectorNames() {
        return detectors.values().stream().map(detector -> detector.getName()).collect(Collectors.toList());
    }

    
    /**
     * <p>addDetector</p>
     *
     * @param detector a {@link org.opennms.netmgt.provision.persistence.dto.PluginConfigDTO} object.
     */
    public void addDetector(final PluginConfigDTO detector) {
        detectors.put(detector.getName(), detector);
    }

    /**
     * <p>addPolicy</p>
     *
     * @param policy a {@link org.opennms.netmgt.provision.persistence.dto.PluginConfigDTO} object.
     */
    public void addPolicy(final PluginConfigDTO policy) {
        policies.put(policy.getName(), policy);
    }

    /**
     * <p>getDetector</p>
     *
     * @param detector a {@link String} object.
     * @return a {@link org.opennms.netmgt.provision.persistence.dto.PluginConfigDTO} object.
     */
    public PluginConfigDTO getDetector(final String detector) {
        return detectors.get(detector);
    }

    /* an unfortunate naming convention because of the way PropertyPath works */
    /**
     * <p>removeDetectors</p>
     *
     * @param detector a {@link org.opennms.netmgt.provision.persistence.dto.PluginConfigDTO} object.
     */
    public void deleteDetectors(final PluginConfigDTO detector) {
        detectors.remove(detector.getName());
    }

    /**
     * <p>getPolicy</p>
     *
     * @param policy a {@link String} object.
     * @return a {@link org.opennms.netmgt.provision.persistence.dto.PluginConfigDTO} object.
     */
    public PluginConfigDTO getPolicy(final String policy) {
        return policies.get(policy);
    }

    /* an unfortunate naming convention because of the way PropertyPath works */
    /**
     * <p>removePolicies</p>
     *
     * @param policy a {@link org.opennms.netmgt.provision.persistence.dto.PluginConfigDTO} object.
     */
    public void deletePolicies(final PluginConfigDTO policy) {
        policies.remove(policy.getName());
    }

    public void validate() throws ValidationException {
        //TODO: Implement this!
    };
}

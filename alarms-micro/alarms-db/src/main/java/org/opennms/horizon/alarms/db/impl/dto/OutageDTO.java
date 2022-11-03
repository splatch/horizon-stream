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

package org.opennms.horizon.alarms.db.impl.dto;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Data;
import org.hibernate.annotations.Type;

/**
 * <p>OnmsOutage class.</p>
 *
 * @hibernate.class table="outages"
 */
@Entity
@Table(name="outages")
@Data
public class OutageDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3846398168228820151L;

    @Id
    @Column(name="outageId", nullable=false)
    @SequenceGenerator(name="outageSequence", sequenceName="outageNxtId", allocationSize = 1)
    @GeneratedValue(generator="outageSequence")
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ifLostService", nullable=false)
    private Date ifLostService;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ifRegainedService")
    private Date ifRegainedService;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="svcRegainedEventId")
    private EventDTO serviceRegainedEvent;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="svcLostEventId")
    private EventDTO serviceLostEvent;

    @ManyToOne
    @JoinColumn(name="ifserviceId")
    private MonitoredServiceDTO monitoredService;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="suppressTime")
    private Date suppressTime;

    @Column(name="suppressedBy")
    private String suppressedBy;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="perspective")
    private MonitoringLocationDTO perspective;

    public MonitoringLocationDTO getM_perspective() {
        return perspective;
    }

    public void setM_perspective(MonitoringLocationDTO perspective) {
        this.perspective = perspective;
    }

    public EventDTO getM_serviceRegainedEvent() {
        return serviceRegainedEvent;
    }

    public void setM_serviceRegainedEvent(EventDTO serviceRegainedEvent) {
        this.serviceRegainedEvent = serviceRegainedEvent;
    }

    public EventDTO getM_serviceLostEvent() {
        return serviceLostEvent;
    }

    public void setM_serviceLostEvent(EventDTO serviceLostEvent) {
        this.serviceLostEvent = serviceLostEvent;
    }

    public MonitoredServiceDTO getM_monitoredService() {
        return monitoredService;
    }

    public void setM_monitoredService(MonitoredServiceDTO monitoredService) {
        this.monitoredService = monitoredService;
    }

    /**
     * full constructor
     *
     * @param ifLostService a {@link Date} object.
     * @param ifRegainedService a {@link Date} object.
     * @param monitoredService a {@link MonitoredServiceDTO} object.
     * @param suppressTime a {@link Date} object.
     * @param suppressedBy a {@link String} object.
     */
    public OutageDTO(Date ifLostService, Date ifRegainedService, MonitoredServiceDTO monitoredService, Date suppressTime, String suppressedBy) {
        this.ifLostService = ifLostService;
        this.ifRegainedService = ifRegainedService;
        this.monitoredService = monitoredService;
        this.suppressTime = suppressTime;
        this.suppressedBy = suppressedBy;
        
    }

    /**
     * default constructor
     */
    public OutageDTO() {
    }

    /**
     */
    public OutageDTO(Date ifLostService, MonitoredServiceDTO monitoredService) {
        this.ifLostService = ifLostService;
        this.monitoredService = monitoredService;
    }

    public OutageDTO(Date ifLostService, Date ifRegainedService, MonitoredServiceDTO monitoredService) {
        this.ifLostService = ifLostService;
        this.ifRegainedService = ifRegainedService;
        this.monitoredService = monitoredService;
    }

    /**
     * <p>getNodeId</p>
     *
     * @return a {@link Integer} object.
     */
    @Transient
    public Integer getNodeId(){
    	return getMonitoredService().getId();
    }

    /**
     * <p>getIpAddress</p>
     *
     * @return a {@link String} object.
     */
    @Transient
    //TODO:MMF fix this
    @Type(type="org.opennms.horizon.db.model.InetAddressUserType")
    public InetAddress getIpAddress() {
        return getMonitoredService().getIpAddress();
    }

    /**
     * <p>getIpAddressAsString</p>
     *
     * @return a {@link String} object.
     * @deprecated use getIpAddress
     */
    @Transient
    public String getIpAddressAsString() {
        return getMonitoredService().getIpAddressAsString();
    }

    /**
     * <p>getServiceId</p>
     *
     * @return a {@link Integer} object.
     */
    @Transient
    public Integer getServiceId() {
    	return getMonitoredService().getServiceId();
    }

    //TODO:MMF therefore not needed?
    /**
     * This method is necessary for CXF to be able to introspect
     * the type of NodeDTO} parameters.
     *
     * @return a {@link ServiceTypeDTO} object.
     */
    @Transient
    public ServiceTypeDTO getServiceType() {
        return getMonitoredService().getServiceType();
    }

    /**
     * This method is necessary for CXF to be able to introspect
     * the type of {@link ServiceTypeDTO} parameters.
     */
    public void setServiceType(ServiceTypeDTO type) {
        MonitoredServiceDTO service = getMonitoredService();
        if (service == null) {
            service = new MonitoredServiceDTO();
            setMonitoredService(service);
        }
        service.setServiceType(type);
    }
}

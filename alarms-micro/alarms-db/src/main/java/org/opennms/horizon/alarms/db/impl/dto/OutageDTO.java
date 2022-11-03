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
import org.hibernate.annotations.Type;

/**
 * <p>OnmsOutage class.</p>
 *
 * @hibernate.class table="outages"
 */
@Entity
@Table(name="outages")
public class OutageDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3846398168228820151L;

    /** identifier field */
    private Integer m_id;

    /** persistent field */
    private Date m_ifLostService;

    /** nullable persistent field */
    private Date m_ifRegainedService;

    /** persistent field */
    @OneToOne
    @JoinColumn(name = "m_service_regained_event_event_id")
    private EventDTO m_serviceRegainedEvent;

    /** persistent field */
    @OneToOne
    @JoinColumn(name = "m_service_lost_event_event_id")
    private EventDTO m_serviceLostEvent;

    /** persistent field */
    @OneToOne
    @JoinColumn(name = "m_monitored_service_id")
    private MonitoredServiceDTO m_monitoredService;
    
    /** persistent field */
    private Date m_suppressTime;
    
    /** persistent field */
    private String m_suppressedBy;

    /** persistent field */
    @OneToOne
    @JoinColumn(name = "m_perspective_id")
    private MonitoringLocationDTO m_perspective;

    public MonitoringLocationDTO getM_perspective() {
        return m_perspective;
    }

    public void setM_perspective(MonitoringLocationDTO m_perspective) {
        this.m_perspective = m_perspective;
    }

    public EventDTO getM_serviceRegainedEvent() {
        return m_serviceRegainedEvent;
    }

    public void setM_serviceRegainedEvent(EventDTO m_serviceRegainedEvent) {
        this.m_serviceRegainedEvent = m_serviceRegainedEvent;
    }

    public EventDTO getM_serviceLostEvent() {
        return m_serviceLostEvent;
    }

    public void setM_serviceLostEvent(EventDTO m_serviceLostEvent) {
        this.m_serviceLostEvent = m_serviceLostEvent;
    }

    public MonitoredServiceDTO getM_monitoredService() {
        return m_monitoredService;
    }

    public void setM_monitoredService(MonitoredServiceDTO m_monitoredService) {
        this.m_monitoredService = m_monitoredService;
    }

    /**
     * full constructor
     *
     * @param ifLostService a {@link Date} object.
     * @param ifRegainedService a {@link Date} object.
     * @param eventBySvcRegainedEvent a {@link EventDTO} object.
     * @param eventBySvcLostEvent a {@link EventDTO} object.
     * @param monitoredService a {@link MonitoredServiceDTO} object.
     * @param suppressTime a {@link Date} object.
     * @param suppressedBy a {@link String} object.
     */
    public OutageDTO(Date ifLostService, Date ifRegainedService, EventDTO eventBySvcRegainedEvent, EventDTO eventBySvcLostEvent, MonitoredServiceDTO monitoredService, Date suppressTime, String suppressedBy) {
        m_ifLostService = ifLostService;
        m_ifRegainedService = ifRegainedService;
//        m_serviceRegainedEvent = eventBySvcRegainedEvent;
//        m_serviceLostEvent = eventBySvcLostEvent;
        m_monitoredService = monitoredService;
        m_suppressTime = suppressTime;
        m_suppressedBy = suppressedBy;
        
    }

    /**
     * default constructor
     */
    public OutageDTO() {
    }

    /**
     */
    public OutageDTO(Date ifLostService, MonitoredServiceDTO monitoredService) {
        m_ifLostService = ifLostService;
        m_monitoredService = monitoredService;
    }

    public OutageDTO(Date ifLostService, Date ifRegainedService, MonitoredServiceDTO monitoredService) {
        m_ifLostService = ifLostService;
        m_ifRegainedService = ifRegainedService;
        m_monitoredService = monitoredService;
    }

    /**
     * minimal constructor
     *
     * @param ifLostService a {@link Date} object.
     * @param eventBySvcLostEvent a {@link EventDTO} object.
     * @param monitoredService a {@link MonitoredServiceDTO} object.
     */
    public OutageDTO(Date ifLostService, EventDTO eventBySvcLostEvent, MonitoredServiceDTO monitoredService) {
        m_ifLostService = ifLostService;
//        m_serviceLostEvent = eventBySvcLostEvent;
        m_monitoredService = monitoredService;
    }

    /**
     * <p>getId</p>
     *
     * @return a {@link Integer} object.
     */
    @Id
    @Column(name="outageId", nullable=false)
    @SequenceGenerator(name="outageSequence", sequenceName="outageNxtId", allocationSize = 1)
    @GeneratedValue(generator="outageSequence")
    public Integer getId() {
        return m_id;
    }

    /**
     * <p>setId</p>
     *
     * @param outageId a {@link Integer} object.
     */
    public void setId(Integer outageId) {
        m_id = outageId;
    }

    // @XmlTransient
    /**
     * <p>getMonitoredService</p>
     *
     * @return a {@link MonitoredServiceDTO} object.
     */
    @ManyToOne
    @JoinColumn(name="ifserviceId")
    public MonitoredServiceDTO getMonitoredService() {
        return m_monitoredService;
    }

    /**
     * <p>setMonitoredService</p>
     *
     * @param monitoredService a {@link MonitoredServiceDTO} object.
     */
    public void setMonitoredService(MonitoredServiceDTO monitoredService) {
        m_monitoredService = monitoredService;
    }

    
    /**
     * <p>getIfLostService</p>
     *
     * @return a {@link Date} object.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ifLostService", nullable=false)
    public Date getIfLostService() {
        return m_ifLostService;
    }

    /**
     * <p>setIfLostService</p>
     *
     * @param ifLostService a {@link Date} object.
     */
    public void setIfLostService(Date ifLostService) {
        m_ifLostService = ifLostService;
    }

    /**
     * <p>getServiceLostEvent</p>
     *
     * @return a {@link EventDTO} object.
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="svcLostEventId")
    public EventDTO getServiceLostEvent() {
        return m_serviceLostEvent;
    }

    /**
     * <p>setServiceLostEvent</p>
     *
     * @param svcLostEvent a {@link EventDTO} object.
     */
    public void setServiceLostEvent(EventDTO svcLostEvent) {
        m_serviceLostEvent = svcLostEvent;
    }


    /**
     * <p>getIfRegainedService</p>
     *
     * @return a {@link Date} object.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ifRegainedService")
    public Date getIfRegainedService() {
        return m_ifRegainedService;
    }
    
    /**
     * <p>setIfRegainedService</p>
     *
     * @param ifRegainedService a {@link Date} object.
     */
    public void setIfRegainedService(Date ifRegainedService) {
        m_ifRegainedService = ifRegainedService;
    }

    /**
     * <p>getServiceRegainedEvent</p>
     *
     * @return a {@link EventDTO} object.
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="svcRegainedEventId")
    public EventDTO getServiceRegainedEvent() {
        return m_serviceRegainedEvent;
    }

    /**
     * <p>setServiceRegainedEvent</p>
     *
     * @param svcRegainedEvent a {@link EventDTO} object.
     */
    public void setServiceRegainedEvent(EventDTO svcRegainedEvent) {
        m_serviceRegainedEvent = svcRegainedEvent;
    }

    /**
     * <p>getSuppressTime</p>
     *
     * @return a {@link Date} object.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="suppressTime")
    public Date getSuppressTime(){
    	return m_suppressTime;
    }
    
    /**
     * <p>setSuppressTime</p>
     *
     * @param timeToSuppress a {@link Date} object.
     */
    public void setSuppressTime(Date timeToSuppress){
    	m_suppressTime = timeToSuppress;
    }
    
    
    /**
     * <p>getSuppressedBy</p>
     *
     * @return a {@link String} object.
     */
    @Column(name="suppressedBy")
    public String getSuppressedBy(){
    	return m_suppressedBy;
    }
    
    /**
     * <p>setSuppressedBy</p>
     *
     * @param suppressorMan a {@link String} object.
     */
    public void setSuppressedBy(String suppressorMan){
    	m_suppressedBy = suppressorMan;
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

    /**
     * Monitoring perspective that this outage is associated with.
     */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="perspective")
    public MonitoringLocationDTO getPerspective() {
        return m_perspective;
    }

    /**
     * Set the monitoring perspective for this outage.
     */
    public void setPerspective(MonitoringLocationDTO perspective) {
        m_perspective = perspective;
    }

    /**
     * <p>toString</p>
     *
     * @return a {@link String} object.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("outageId", m_id)
            .add("ifLostService", m_ifLostService)
            .add("ifRegainedService", m_ifRegainedService)
            .add("ifRegainedServiceEvent", m_serviceRegainedEvent)
            .add("service", m_monitoredService)
            .add("suppressedBy", m_suppressedBy)
            .add("suppressTime", m_suppressTime)
            .add("perspective", m_perspective)
            .toString();
    }

}

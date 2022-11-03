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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.hibernate.annotations.Type;

import org.opennms.horizon.alarms.db.api.EntityVisitor;
import org.opennms.horizon.alarms.db.impl.AlarmSeverity;

@Entity
@Table(name="events")
@Data
public class EventDTO extends EntityDTO implements Serializable {

	private static final long serialVersionUID = -7412025003474162992L;

	
	@Id
	@Column(nullable=false)
	@SequenceGenerator(name="eventSequence", sequenceName="eventsNxtId", allocationSize = 1)
	@GeneratedValue(generator="eventSequence")
	private Integer eventId;

	
	@Column(length=256, nullable=false)
	private String eventUei;

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date eventTime;

	
	@Column(length=256)
	private String eventHost;

	
	@Column( length=128, nullable=false)
	private String eventSource;

	
	@Column
    //TODO:MMF fix this
	@Type(type="org.opennms.horizon.db.model.InetAddressUserType")
	private InetAddress ipAddr;

	
    //TODO: bring this over too....
	@ManyToOne
	@JoinColumn(name="systemId", nullable=false)
	private MonitoringSystemDTO distPoller;

	
	@Column(length=256)
	private String eventSnmpHost;

	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(nullable=true)
	private ServiceTypeDTO serviceType;

	
	@Column(length=256)
	private String eventSnmp;

	@OneToMany(mappedBy="event", cascade=CascadeType.ALL)
	private List<EventParameterDTO> eventParameters;

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	private Date eventCreateTime;

	
	@Column(length=4000)
	private String eventDescr;

	
	@Column(length=32)
	private String eventLogGroup;

	
	@Column(length=1024)
	private String eventLogMsg;

	
	@Column(nullable=false)
	private Integer eventSeverity;

	
	@Column
    private Integer ifIndex;

	
	@Column(length=1024)
	private String eventPathOutage;

	
	@Column(length=1024)
	private String eventCorrelation;

	
	@Column
	private Integer eventSuppressedCount;

	
	@Column
	private String eventOperInstruct;

	
	@Column(length=256)
	private String eventAutoAction;

	
	@Column(length=256)
	private String eventOperAction;

	
	@Column(length=64)
	private String eventOperActionMenuText;

	
	@Column(length=128)
	private String eventNotification;

	
	@Column(length=128)
	private String eventTTicket;

	
	@Column
	private Integer eventTTicketState;

	
	@Column(length=256)
	private String eventForward;

	
	@Column(length=64)
	private String eventMouseOverText;

	
	@Column(length=1, nullable=false)
	private String eventLog;

	
	@Column(length=1, nullable=false)
	private String eventDisplay;

	
	@Column(length=256)
	private String eventAckUser;

	
	@Temporal(TemporalType.TIMESTAMP)
	@Column
	private Date eventAckTime;

	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="alarmId")
    //TODO:MMF Eh? This is a circulular reference now!
	private AlarmDTO alarm;
	
	@OneToMany(mappedBy="event", fetch=FetchType.LAZY)
	private Set<NotificationDTO> notifications = new HashSet<>();


	@OneToMany(mappedBy="serviceRegainedEvent", fetch=FetchType.LAZY)
	private Set<OutageDTO> associatedServiceRegainedOutages = new HashSet<>();

	
	@OneToMany(mappedBy="serviceLostEvent", fetch=FetchType.LAZY)
	private Set<OutageDTO> associatedServiceLostOutages = new HashSet<>();

	/**
	 * default constructor
	 */
	public EventDTO() {
	}

	public List<EventParameterDTO> getEventParameters() {
		if(this.eventParameters != null) {
			this.eventParameters.sort(Comparator.comparing(EventParameterDTO::getPosition));
		}
		return this.eventParameters;
	}

	public void setEventParameters(List<EventParameterDTO> eventParameters) {
		this.eventParameters = eventParameters;
		setPositionsOnParameters(this.eventParameters);
	}

    //TODO:MMF
//	public void setEventParametersFromEvent(final Event event) {
//		this.eventParameters = EventParameterUtils.normalizePreserveOrder(event.getParmCollection()).stream()
//				.map(p -> new EventParameterDTO(this, p))
//				.collect(Collectors.toList());
//		setPositionsOnParameters(eventParameters);
//	}

	public void addEventParameter(EventParameterDTO parameter) {
		if (eventParameters == null) {
			eventParameters = new ArrayList<>();
		}
		if (eventParameters.contains(parameter)) {
			eventParameters.remove(parameter);
		}
		eventParameters.add(parameter);
        setPositionsOnParameters(eventParameters);
	}

	/**
     * We need this method to preserve the order in the m_eventParameters when saved and retrieved from the database.
     * There might be a more elegant solution via JPA but none seems to work in our context, see also:
     * https://issues.opennms.org/browse/NMS-9827
     */
    private void setPositionsOnParameters(List<EventParameterDTO> parameters) {
        if (parameters != null) {
            // give each parameter a distinct position
            for (int i = 0; i < parameters.size(); i++) {
                parameters.get(i).setPosition(i);
            }
        }
    }

    /**
     * <p>getSeverityLabel</p>
     *
     * @return a {@link String} object.
     */
    public String getSeverityLabel() {
        return AlarmSeverity.get(eventSeverity).name();
    }

    /**
     * <p>setSeverityLabel</p>
     *
     * @param label a {@link String} object.
     */
    public void setSeverityLabel(String label) {
        eventSeverity = AlarmSeverity.get(label).getId();
    }


    //TODO:MMF
//	/**
//	 * <p>getNode</p>
//	 *
//	 * @return a {@link OnmsNode} object.
//	 */
//	@XmlTransient
//	public OnmsNode getNode() {
//		return node;
//	}
//
//    @XmlElement(name="nodeId")
//    public Integer getNodeId() {
//        try {
//            return node != null ? node.getId() : null;
//        } catch (ObjectNotFoundException e) {
//            return null;
//        }
//    }
//
//    @XmlElement(name="nodeLabel", required=false)
//    public String getNodeLabel() {
//        try{
//            if (node == null) return null;
//            return node.getLabel();
//        } catch (ObjectNotFoundException e){
//            return "";
//        }
//
//    }
//
//	/**
//	 * <p>setNode</p>
//	 *
//	 * @param node a {@link OnmsNode} object.
//	 */
//	public void setNode(OnmsNode node) {
//		this.node = node;
//	}

	/**
	 * <p>toString</p>
	 *
	 * @return a {@link String} object.
	 */
        @Override
	public String toString() {
            return MoreObjects.toStringHelper(this).add("eventid", getEventId())
		        .add("eventuei", getEventUei())
				.toString();
	}

	/** {@inheritDoc} */
        @Override
	public void visit(EntityVisitor visitor) {
		throw new RuntimeException("visitor method not implemented");
	}
}

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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;
import org.opennms.horizon.alarms.db.impl.TroubleTicketState;

@Entity
@Table(name="alarms")
@Data
@NoArgsConstructor
public class AlarmDTO implements Serializable {
    private static final long serialVersionUID = 7275548439687562161L;

    public static final int PROBLEM_TYPE = 1;
    public static final int RESOLUTION_TYPE = 2;
    public static final int PROBLEM_WITHOUT_RESOLUTION_TYPE = 3;

    public static final String ARCHIVED = "Archived";


    @Id
    @SequenceGenerator(name="alarmSequence", sequenceName="alarmsNxtId", allocationSize = 1)
    @GeneratedValue(generator="alarmSequence")
    @Column(name="alarmId", nullable=false)
    private Integer id;

    @Column(name="eventUEI", length=256, nullable=false)
    private String uei;

    @Column
    @Type(type= "org.opennms.horizon.alarms.db.impl.utils.InetAddressUserType")
    private InetAddress ipAddr;

    @Column(unique=true)
    private String reductionKey;

    @Column
    private Integer alarmType;

    @Column
    private Integer ifIndex;

    @Column(nullable=false)
    private Integer counter;

    @Column(nullable=false)
    @Type(type= "org.opennms.horizon.alarms.db.impl.dto.SeverityUserTypeDTO")
    private AlarmSeverity severity = AlarmSeverity.INDETERMINATE;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date firstEventTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date lastEventTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date firstAutomationTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date lastAutomationTime;

    @Column(length=4000)
    private String description;

    @Column(length=1024)
    private String logMsg;

    @Column
    private String operInstruct;

    @Column(length=128)
    private String tTicketId;

    @Column
    private TroubleTicketState tTicketState;

    @Column(length=64)
    private String mouseOverText;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date suppressedUntil;

    @Column(length=256)
    private String suppressedUser;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date suppressedTime;

    @Column(length=256)
    private String alarmAckUser;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date alarmAckTime;

    @Column
    private String clearKey;

    @Column(length=512)
    private String managedObjectInstance;

    @Column(length=512)
    private String managedObjectType;

    @Column(length=512)
    private String applicationDN;

    @Column(length=512)
    private String ossPrimaryKey;

    @Column(length=31)
    private String x733AlarmType;

    @Column(length=31)
    private String qosAlarmState;

    @Column(nullable=false)
    private int x733ProbableCause = 0;

    @ElementCollection
    @JoinTable(name="alarm_attributes", joinColumns = @JoinColumn(name="alarmId"))
    @MapKeyColumn(name="attributename")
    @Column(name="attributeValue", nullable=false)
    private Map<String, String> details;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="stickymemo")
    private MemoDTO stickyMemo;

    @ManyToOne
    @JoinColumn(name="reductionKey", referencedColumnName="reductionkey", updatable=false, insertable=false)
    private ReductionKeyMemoDTO reductionKeyMemo;
    private Set<AlarmAssociationDTO> associatedAlarms = new HashSet<>();


    @ElementCollection
    @JoinTable(name = "alarm_situations", joinColumns = @JoinColumn(name = "related_alarm_id"), inverseJoinColumns = @JoinColumn(name = "situation_id"))
    @Column(name="alarm_id", nullable=false)
    private Set<AlarmDTO> relatedSituations = new HashSet<>();

    @Formula(value = "(SELECT COUNT(*)>0 FROM ALARM_SITUATIONS S WHERE S.SITUATION_ID=ALARMID)")
    private boolean situation;

    @Formula(value = "(SELECT COUNT(*)>0 FROM ALARM_SITUATIONS S WHERE S.RELATED_ALARM_ID=ALARMID)")
    private boolean partOfSituation;

    //TODO: add in whatever is needed form the Event protobuf as individual fields.

    /**
     * minimal constructor
     *
     * @param alarmid a {@link Integer} object.
     * @param eventuei a {@link String} object.
     * @param counter a {@link Integer} object.
     * @param severity a {@link Integer} object.
     * @param firsteventtime a {@link Date} object.
     */
    public AlarmDTO(Integer alarmid, String eventuei, Integer counter, Integer severity, Date firsteventtime, Date lasteEventTime) {
        this.id = alarmid;
        this.uei = eventuei;
        this.counter = counter;
        this.severity = AlarmSeverity.get(severity);
        this.firstEventTime = firsteventtime;
        setLastEventTime(lasteEventTime);
    }

    @Transient
    public String getSeverityLabel() {
        return this.severity.name();
    }

    public void setSeverityLabel(final String label) {
        severity = AlarmSeverity.get(label);
    }

    @Transient
    public Integer getSeverityId() {
        return this.severity.getId();
    }

    public void setSeverityId(final Integer severity) {
        this.severity = AlarmSeverity.get(severity);
    }

    @Transient
    public boolean isAcknowledged() {
        return getAlarmAckUser() != null;
    }

    //TODO: Maybe we DO need the lastEvent?
//    @Transient
//    @XmlElementWrapper(name="parameters")
//    @XmlElement(name="parameter")
//    public List<OnmsEventParameter> getEventParameters() {
//        return m_lastEvent != null ? m_lastEvent.getEventParameters() : null;
//    }

//    public Optional<OnmsEventParameter> findEventParameter(final String name) {
//        return this.getEventParameters().stream().filter(p -> Objects.equals(name, p.getName())).findAny();
//    }
//
//    public String getEventParameter(final String name) {
//        return this.getEventParameters().stream().filter(p -> Objects.equals(name, p.getName())).findAny().map(OnmsEventParameter::getValue).orElse(null);
//    }

    /**
     * <p>toString</p>
     *
     * @return a {@link String} object.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("alarmid", getId())
            .add("uei", getUei())
            .add("severity", getSeverity())
            .add("lastEventTime",getLastEventTime())
            .add("counter", getCounter())
            .toString();
    }

    /**
     * This marks an alarm as archived and prevents it from being used again in during reduction.
     */
    public void archive() {
        qosAlarmState = ARCHIVED;
        severity = AlarmSeverity.CLEARED;
        reductionKey = getReductionKey() + ":ID:"+ getId();
    }

    // Alarms that are archived
    @Transient
    public boolean isArchived() {
        return ARCHIVED.equals(qosAlarmState);
    }

    /**
     * <p>getRelatedAlarms</p>
     *
     * @return a {@link Set} object.
     */
    @Transient
    
    public Set<AlarmDTO> getRelatedAlarms() {
        return associatedAlarms.stream().map(AlarmAssociationDTO::getRelatedAlarm).collect(Collectors.toSet());
    }

    @Transient
    
    public Set<Integer> getRelatedAlarmIds() {
        return getRelatedAlarms().stream()
                .map(AlarmDTO::getId)
                .collect(Collectors.toSet());
    }

    
    @OneToMany(mappedBy = "situationAlarm", orphanRemoval = true, cascade = CascadeType.ALL)
    public Set<AlarmAssociationDTO> getAssociatedAlarms() {
        return associatedAlarms;
    }

    public void setAssociatedAlarms(Set<AlarmAssociationDTO> alarms) {
        associatedAlarms = alarms;
        situation = !associatedAlarms.isEmpty();
    }

    public void setRelatedAlarms(Set<AlarmDTO> alarms) {
        associatedAlarms.clear();
        alarms.forEach(relatedAlarm -> associatedAlarms.add(new AlarmAssociationDTO(this, relatedAlarm)));
        situation = !associatedAlarms.isEmpty();
    }

    public void setRelatedAlarms(Set<AlarmDTO> alarms, Date associationEventTime) {
        associatedAlarms.clear();
        alarms.forEach(relatedAlarm -> associatedAlarms.add(new AlarmAssociationDTO(this, relatedAlarm, associationEventTime)));
        situation = !associatedAlarms.isEmpty();
    }

    public void addRelatedAlarm(AlarmDTO alarm) {
        associatedAlarms.add(new AlarmAssociationDTO(this, alarm));
        situation = !associatedAlarms.isEmpty();
    }

    public void removeRelatedAlarm(AlarmDTO alarm) {
        associatedAlarms.removeIf(associatedAlarm -> associatedAlarm.getRelatedAlarm().getId().equals(alarm.getId()));
        situation = !associatedAlarms.isEmpty();
    }

    public void removeRelatedAlarmWithId(Integer relatedAlarmId) {
        associatedAlarms.removeIf(associatedAlarm -> associatedAlarm.getRelatedAlarm().getId().equals(relatedAlarmId));
        situation = !associatedAlarms.isEmpty();
    }

    @Transient
    public Set<Integer> getRelatedSituationIds() {
        return getRelatedSituations().stream()
                .map(AlarmDTO::getId)
                .collect(Collectors.toSet());
    }

    public void setRelatedSituations(Set<AlarmDTO> alarms) {
        relatedSituations = alarms;
        partOfSituation = !relatedSituations.isEmpty();
    }

    @Transient
    public Date getLastUpdateTime() {
        if (getLastAutomationTime() != null && getLastAutomationTime().compareTo(getLastEventTime()) > 0) {
            return getLastAutomationTime();
        }
        return getLastEventTime();
    }

}

/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
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

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.opennms.horizon.alarms.db.impl.dto.AlarmDTO;

/**
 * <p> Entity to store situations and their associated (related) alarms with other details like mappedTime </p>
 */
@Entity
@Table(name = "alarm_situations", uniqueConstraints={@UniqueConstraint(columnNames={"situation_id", "related_alarm_id"})})
public class AlarmAssociation implements Serializable {

    private static final long serialVersionUID = 4115687014888009683L;

    private Integer id;

    private AlarmDTO situationAlarm;

    private AlarmDTO relatedAlarm;

    private Date mappedTime;

    public AlarmAssociation() {
    }

    public AlarmAssociation(AlarmDTO situationAlarm, AlarmDTO relatedAlarm) {
        this(situationAlarm, relatedAlarm, new Date());
    }

    public AlarmAssociation(AlarmDTO situationAlarm, AlarmDTO relatedAlarm, Date mappedTime) {
        this.mappedTime = mappedTime;
        this.situationAlarm = situationAlarm;
        this.relatedAlarm = relatedAlarm;
    }

    @Id
    @SequenceGenerator(name="alarmSequence", sequenceName="alarmsNxtId", allocationSize = 1)
    @GeneratedValue(generator="alarmSequence")
    @Column(name="id", nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "situation_id")
    public AlarmDTO getSituationAlarm() {
        return situationAlarm;
    }

    public void setSituationAlarm(AlarmDTO situationAlarm) {
        this.situationAlarm = situationAlarm;
    }

    @OneToOne
    @JoinColumn(name = "related_alarm_id")
    public AlarmDTO getRelatedAlarm() {
        return relatedAlarm;
    }

    public void setRelatedAlarm(AlarmDTO relatedAlarm) {
        this.relatedAlarm = relatedAlarm;
    }


    public void setMappedTime(Date mappedTime) {
        this.mappedTime = mappedTime;
    }

    @Column(name = "mapped_time")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getMappedTime() {
        return mappedTime;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("situation", getSituationAlarm().getId())
                .add("alarm", getRelatedAlarm().getId())
                .add("time", getMappedTime())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmAssociation that = (AlarmAssociation) o;
        return Objects.equals(situationAlarm, that.situationAlarm) &&
                Objects.equals(relatedAlarm, that.relatedAlarm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(situationAlarm, relatedAlarm);
    }
}

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

package org.opennms.horizon.alertservice.db.entity;

import org.opennms.horizon.alerts.proto.EventType;
import org.opennms.horizon.alerts.proto.OverTimeUnit;
import org.opennms.horizon.alerts.proto.Severity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trigger_event")
@Getter
@Setter
public class TriggerEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (name = "tenant_id", nullable = false)
    private String tenantId;
    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_event_type")
    private EventType triggerEvent;
    @Column(name = "event_count")
    private Integer count;
    @Column(name = "over_time")
    private Integer overtime;
    @Enumerated(EnumType.STRING)
    @Column(name = "over_time_unit")
    private OverTimeUnit overtimeUnit;
    @Enumerated(EnumType.STRING)
    private Severity severity;
    @Enumerated(EnumType.STRING)
    @Column(name = "clear_event_type")
    private EventType clearEvent;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", referencedColumnName = "id")
    private PolicyRule rule;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id", referencedColumnName = "trigger_event_id")
    private AlertDefinition alertDefinition;
}

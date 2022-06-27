/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2022 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2022 The OpenNMS Group, Inc.
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

package org.opennms.horizon.server.model;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmDTO {
  private Integer id;
  private String uei;
  private String location;
  private Integer nodeId;
  private String nodeLabel;
  private String ipAddress;
  private ServiceTypeDTO serviceType;
  private String reductionKey;
  private Integer type;
  private Integer count;
  private String severity;
  private Date firstEventTime;
  private String description;
  private String logMessage;
  private String operatorInstructions;
  private String troubleTicket;
  private Integer troubleTicketState;
  private String troubleTicketLink;
  private String mouseOverText;
  private Date suppressedUntil;
  private String suppressedBy;
  private Date suppressedTime;
  private String ackUser;
  private Date ackTime;
  private String clearKey;
  private EventDTO lastEvent;
  private List<EventParameterDTO> parameters;
  private Date lastEventTime;
  private String applicationDN;
  private String managedObjectInstance;
  private String managedObjectType;
  private String ossPrimaryKey;
  private String x733AlarmType;
  private Integer x733ProbableCause;
  private String qosAlarmState;
  private Date firstAutomationTime;
  private Date lastAutomationTime;
  private Integer ifIndex;
  private ReductionKeyMemoDTO reductionKeyMemo;
  private MemoDTO stickyMemo;
  private List<AlarmSummaryDTO> relatedAlarms;
  private Integer affectedNodeCount;
}

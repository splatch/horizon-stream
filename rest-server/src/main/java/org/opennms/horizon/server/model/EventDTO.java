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

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDTO {
  private Integer id;
  private String uei;
  private String label;
  private Date time;
  private String host;
  private String source;
  private String ipAddress;
  private String snmpHost;
  private ServiceTypeDTO serviceType;
  private String snmp;
  private List<EventParameterDTO> parameters;
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Date createTime;
  private String description;
  private String logGroup;
  private String logMessage;
  private String severity;
  private String pathOutage;
  private String correlation;
  private Integer suppressedCount;
  private String operatorInstructions;
  private String autoAction;
  private String operatorAction;
  private String operationActionMenuText;
  private String notification;
  private String troubleTicket;
  private Integer troubleTicketState;
  private String mouseOverText;
  private String log;
  private String display;
  private String ackUser;
  private Date ackTime;
  private Integer nodeId;
  private String nodeLabel;
  private Integer ifIndex;
  private String location;
}

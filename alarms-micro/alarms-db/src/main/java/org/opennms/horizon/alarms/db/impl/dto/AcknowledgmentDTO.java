/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2014 The OpenNMS Group, Inc.
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

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.opennms.horizon.db.model.AckAction;
import org.opennms.horizon.db.model.AckType;
import org.opennms.horizon.db.model.Acknowledgeable;
import org.opennms.horizon.events.xml.Event;
import org.opennms.horizon.events.xml.Parm;

@Entity
@Table(name = "acks")
/**
 * Persistable object used in acknowledgment activities
 *
 * @author <a href="mailto:david@opennms.org">David Hustace</a>
 * @version $Id: $
 */
public class AcknowledgmentDTO {

    private Integer id;
    private Date ackTime;
    private String ackUser;
    private AckType ackType;
    private AckAction ackAction;
    private String log;
    private Integer refId;
    
    //main constructor
    /**
     * <p>Constructor for OnmsAcknowledgment.</p>
     *
     * @param time a {@link Date} object.
     * @param user a {@link String} object.
     */
    public AcknowledgmentDTO(Date time, String user) {
        ackTime = (time == null) ? new Date() : time;
        ackUser = (user == null) ? "admin" : user;
        ackType = AckType.UNSPECIFIED;
        ackAction = AckAction.ACKNOWLEDGE;  //probably should be the default, set as appropriate after instantiation
    }
    
    /**
     * <p>Constructor for OnmsAcknowledgment.</p>
     */
    public AcknowledgmentDTO() {
        this(new Date(), "admin");
    }
    
    /**
     * <p>Constructor for OnmsAcknowledgment.</p>
     *
     * @param user a {@link String} object.
     */
    public AcknowledgmentDTO(String user) {
        this(new Date(), user);
    }
    
    /**
     * <p>Constructor for OnmsAcknowledgment.</p>
     *
     * @param time a {@link Date} object.
     */
    public AcknowledgmentDTO(final Date time) {
        this(time, "admin");
    }

    /**
     * <p>Constructor for OnmsAcknowledgment.</p>
     *
     * @param e a {@link .Event} object.
     * @throws ParseException if any.
     */
    public AcknowledgmentDTO(final Event e) throws ParseException {
        this(e.getTime(), "admin");
        Collection<Parm> parms = e.getParmCollection();
        
        if (parms.size() <= 2) {
            throw new IllegalArgumentException("Event:"+e.getUei()+" has invalid paramenter list, requires ackType and refId.");
        }
        
        for (Parm parm : parms) {
            final String parmValue = parm.getValue().getContent();
            if (!"ackAction".equals(parm.getParmName()) && 
                !"ackType".equals(parm.getParmName())   && 
                !"refId".equals(parm.getParmName())     && 
                !"ackUser".equals(parm.getParmName())   &&
                !"user".equals(parm.getParmName())) {
                throw new IllegalArgumentException("Event parm: "+parm.getParmName()+", is an invalid paramter");
            } 
            
            if ("ackType".equals(parm.getParmName())) {

                if ("ALARM".equalsIgnoreCase(parmValue) || "NOTIFICATION".equalsIgnoreCase(parmValue)) {
                    ackType = ("ALARM".equalsIgnoreCase(parmValue) ? AckType.ALARM : AckType.NOTIFICATION);
                } else {
                    throw new IllegalArgumentException("Event parm: "+parm.getParmName()+", has invalid value, requires: \"Alarm\" or \"Notification\"." );
                }
                
            } else if ("refId".equals(parm.getParmName())){
                refId = Integer.valueOf(parmValue);
            } else if ("ackUser".equals(parm.getParmName())|| "user".equals(parm.getParmName())){
                ackUser = parmValue;
            } else {
                if ("ACKNOWLEDGE".equalsIgnoreCase(parmValue)) {
                    ackAction =AckAction.ACKNOWLEDGE;
                } else if ("ESCALATE".equalsIgnoreCase(parmValue)) {
                    ackAction =AckAction.ESCALATE;
                } else if ("UNACKNOWLEDGE".equalsIgnoreCase(parmValue)) {
                    ackAction =AckAction.UNACKNOWLEDGE;
                } else if ("CLEAR".equalsIgnoreCase(parmValue)) {
                    ackAction =AckAction.CLEAR;
                } else {
                    ackAction = AckAction.UNSPECIFIED;
                } 
            }
        }
    }

    /**
     * <p>Constructor for OnmsAcknowledgment.</p>
     *
     * @param a a {@link Acknowledgeable} object.
     */
    public AcknowledgmentDTO(final Acknowledgeable a) {
        this(a, "admin", new Date());
        
        //not sure this is a valid use case but doing it for now
        if (a.getType() == AckType.ALARM) {
            if (a.getAckUser() != null) {
                ackUser = a.getAckUser();
                ackTime = a.getAckTime();
            }
        }
        
        
    }
    
    /**
     * <p>Constructor for OnmsAcknowledgment.</p>
     *
     * @param a a {@link Acknowledgeable} object.
     * @param user a {@link String} object.
     */
    public AcknowledgmentDTO(final Acknowledgeable a, String user) {
        this(a, user, new Date());
    }
    
    /**
     * <p>Constructor for OnmsAcknowledgment.</p>
     *
     * @param a a {@link Acknowledgeable} object.
     * @param user a {@link String} object.
     * @param ackTime a {@link Date} object.
     */
    public AcknowledgmentDTO(final Acknowledgeable a, String user, Date ackTime) {
        this();
        if (a == null) {
            throw new IllegalArgumentException("Acknowledgable is null.");
        }
        
        ackUser = user;
        this.ackTime = ackTime;
        ackType = a.getType();
        refId = a.getAckId();
    }
    
    
    /**
     * <p>getId</p>
     *
     * @return a {@link Integer} object.
     */
    @Id
    @Column(nullable=false)
    @SequenceGenerator(name="opennmsSequence", sequenceName="opennmsNxtId", allocationSize = 1)
    @GeneratedValue(generator="opennmsSequence")    
    public Integer getId() {
        return id;
    }
    
    /**
     * <p>setId</p>
     *
     * @param id a {@link Integer} object.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * <p>getAckTime</p>
     *
     * @return a {@link Date} object.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ackTime", nullable=false)
    public Date getAckTime() {
        return ackTime;
    }
    
    /**
     * <p>setAckTime</p>
     *
     * @param time a {@link Date} object.
     */
    public void setAckTime(Date time) {
        ackTime = time;
    }

    //TODO: make this right when Users are persisted to the DB
    /**
     * <p>getAckUser</p>
     *
     * @return a {@link String} object.
     */
    @Column(name="ackUser", length=64, nullable=false)
    public String getAckUser() {
        return ackUser;
    }
    
    /**
     * <p>setAckUser</p>
     *
     * @param user a {@link String} object.
     */
    public void setAckUser(String user) {
        ackUser = user;
    }

    /**
     * <p>getAckType</p>
     *
     * @return a {@link AckType} object.
     */
    @Column(name="ackType", nullable=false)
    public AckType getAckType() {
        return ackType;
    }

    /**
     * <p>setAckType</p>
     *
     * @param ackType a {@link AckType} object.
     */
    public void setAckType(AckType ackType) {
        this.ackType = ackType;
    }

    /**
     * <p>getRefId</p>
     *
     * @return a {@link Integer} object.
     */
    @Column(name="refId")
    public Integer getRefId() {
        return refId;
    }

    /**
     * <p>setRefId</p>
     *
     * @param refId a {@link Integer} object.
     */
    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    /**
     * <p>getAckAction</p>
     *
     * @return a {@link AckAction} object.
     */
    @Column(name="ackAction", nullable=false)
    public AckAction getAckAction() {
        return ackAction;
    }

    /**
     * <p>setAckAction</p>
     *
     * @param ackAction a {@link AckAction} object.
     */
    public void setAckAction(AckAction ackAction) {
        this.ackAction = ackAction;
    }
    
    /**
     * <p>getLog</p>
     *
     * @return a {@link String} object.
     */
    @Column(name="log", nullable=true)
    public String getLog() {
        return log;
    }
    
    /**
     * <p>setLog</p>
     *
     * @param log a {@link String} object.
     */
    public void setLog(String log) {
        this.log = log;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder bldr = new StringBuilder("Acknowledgment ID:");
        bldr.append(id);
        bldr.append(" User:");
        bldr.append(ackUser);
        bldr.append(" Time:");
        bldr.append(ackTime);
        bldr.append(" AckType:");
        bldr.append(ackType);
        bldr.append(" AckAction:");
        bldr.append(ackAction);
        bldr.append(" Acknowledable ID:");
        bldr.append(refId);
        return bldr.toString();
    }

}

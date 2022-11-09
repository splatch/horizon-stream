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

package org.opennms.horizon.alarmservice.db.impl.entity;

import com.google.common.base.MoreObjects;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.alarmservice.db.api.EntityVisitor;
import org.opennms.horizon.alarmservice.utils.AlphaNumeric;
import org.opennms.horizon.alarmservice.utils.RrdLabelUtils;
import org.opennms.horizon.alarmservice.utils.SystemProperties;

@Entity
@Table(name = "snmpInterface")
@Slf4j
@Data
@NoArgsConstructor
public class SnmpInterface extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 4688655131862954563L;

    @Id
    @Column(nullable=false)
    @SequenceGenerator(name = "opennmsSequence", sequenceName = "opennmsNxtId", allocationSize = 1)
    @GeneratedValue(generator = "opennmsSequence")
    private Integer id;

    @Column(name = "snmpPhysAddr", length = 32)
    private String physAddr;

    @Column(name = "snmpIfIndex")
    private Integer ifIndex;

    @Column(name = "snmpIfDescr", length = 256)
    private String ifDescr;

    @Column(name = "snmpIfType")
    private Integer ifType;

    @Column(name = "snmpIfName", length = 32)
    private String ifName;

    @Column(name = "snmpIfSpeed")
    private Long ifSpeed;

    @Column(name = "snmpIfAdminStatus")
    private Integer ifAdminStatus;

    @Column(name = "snmpIfOperStatus")
    private Integer ifOperStatus;

    @Column(name = "snmpIfAlias", length = 256)
    private String ifAlias;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="snmpLastCapsdPoll")
    private Date lastCapsdPoll;

    @Column(name="snmpCollect")
    private String collect = "N";

    @Column(name="snmpPoll")
    private String poll;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="snmpLastSnmpPoll")
    private Date lastSnmpPoll;

    @OneToMany(mappedBy = "snmpInterface", fetch = FetchType.LAZY)
    private Set<IpInterface> ipInterfaces = new HashSet<>();

    /** timestamps for a flow exporting node */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_ingress_flow")
    private Date lastIngressFlow;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="last_egress_flow")
    private Date lastEgressFlow;

    public static final int MAX_FLOW_AGE = SystemProperties.getInteger("org.opennms.features.telemetry.maxFlowAgeSeconds", 604800);
    public static final boolean INGRESS_AND_EGRESS_REQUIRED = Boolean.getBoolean("org.opennms.features.telemetry.ingressAndEgressRequired");


    /**
     * <p>Constructor for OnmsSnmpInterface.</p>
     * @param ifIndex a {@link Integer} object.
     */
    public SnmpInterface(Integer ifIndex) {
        ifIndex = ifIndex;
    }
    


    /**
     * <p>isCollectionUserSpecified</p>
     *
     * @return a boolean.
     */
    @Transient
    public boolean isCollectionUserSpecified(){
        return collect.startsWith("U");
    }
    
    /**
     * <p>isCollectionEnabled</p>
     *
     * @return a boolean.
     */
    @Transient
    public boolean isCollectionEnabled() {
        return "C".equals(collect) || "UC".equals(collect);
    }
    
    /**
     * <p>setCollectionEnabled</p>
     *
     * @param shouldCollect a boolean.
     */
    public void setCollectionEnabled(boolean shouldCollect) {
        setCollectionEnabled(shouldCollect, false);
    }
    
    /**
     * <p>setCollectionEnabled</p>
     *
     * @param shouldCollect a boolean.
     * @param userSpecified a boolean.
     */
    public void setCollectionEnabled(boolean shouldCollect, boolean userSpecified){
       if(userSpecified){
           collect = shouldCollect ? "UC":"UN";
       }else if(!collect.startsWith("U")){
           collect = shouldCollect ? "C" : "N";
       }
    }

    /**
     * <p>isPollEnabled</p>
     *
     * @return a boolean.
     */
    @Transient
    public boolean isPollEnabled() {
        return "P".equals(poll);
    }


    @Transient
    public boolean getHasFlows() {
        if (INGRESS_AND_EGRESS_REQUIRED) {
            return getHasIngressFlows() && getHasEgressFlows();
        } else {
            return getHasIngressFlows() || getHasEgressFlows();
        }
    }

    @Transient
    public boolean getHasIngressFlows() {
        if (lastIngressFlow == null) {
            return false;
        }
        return (System.currentTimeMillis() - lastIngressFlow.getTime()) / 1000 < MAX_FLOW_AGE;
    }

    @Transient
    public boolean getHasEgressFlows() {
        if (lastEgressFlow == null) {
            return false;
        }
        return (System.currentTimeMillis() - lastEgressFlow.getTime()) / 1000 < MAX_FLOW_AGE;
    }


    /**
     * <p>toString</p>
     *
     * @return a {@link String} object.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("snmpphysaddr", getPhysAddr())
            .add("snmpifindex", getIfIndex())
            .add("snmpifdescr", getIfDescr())
            .add("snmpiftype", getIfType())
            .add("snmpifname", getIfName())
            .add("snmpifspeed", getIfSpeed())
            .add("snmpifadminstatus", getIfAdminStatus())
            .add("snmpifoperstatus", getIfOperStatus())
            .add("snmpifalias", getIfAlias())
            .add("snmpCollect", getCollect())
            .add("snmpPoll", getPoll())
            .add("lastCapsdPoll", getLastCapsdPoll())
            .add("lastSnmpPoll", getLastSnmpPoll())
            .add("lastIngressFlow", lastIngressFlow)
            .add("lastEgressFlow", lastEgressFlow)
            .toString();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(EntityVisitor visitor) {
        visitor.visitSnmpInterface(this);
        visitor.visitSnmpInterfaceComplete(this);
    }

    // @Transient
    // public Set getIpInterfaces() {
    //		
    // Set ifsForSnmpIface = new LinkedHashSet();
    // for (Iterator it = getNode().getIpInterfaces().iterator();
    // it.hasNext();) {
    // OnmsIpInterface iface = (OnmsIpInterface) it.next();
    // if (getIfIndex().equals(iface.getIfIndex()))
    // ifsForSnmpIface.add(iface);
    // }
    // return ifsForSnmpIface;
    // }

    /**
     * <p>computePhysAddrForRRD</p>
     *
     * @return a {@link String} object.
     */
    public String computePhysAddrForRRD() {
        /*
         * In order to assure the uniqueness of the RRD file names we now
         * append the MAC/physical address to the end of label if it is
         * available.
         */
        String physAddrForRRD = null;

        if (getPhysAddr() != null) {
            String parsedPhysAddr = AlphaNumeric.parseAndTrim(getPhysAddr());
            if (parsedPhysAddr.length() == 12) {
                physAddrForRRD = parsedPhysAddr;
            } else {
                    log.debug("physAddrForRRD: physical address len is NOT 12, physAddr={}", parsedPhysAddr);
            }
        }
        log.debug("computed physAddr for {} to be {}", this, physAddrForRRD);
        return physAddrForRRD;
    }

    /**
     * <p>computeNameForRRD</p>
     *
     * @return a {@link String} object.
     */
    public String computeNameForRRD() {
        /*
         * Determine the label for this interface. The label will be used to
         * create the RRD file name which holds SNMP data retreived from the
         * remote agent. If available ifName is used to generate the label
         * since it is guaranteed to be unique. Otherwise ifDescr is used. In
         * either case, all non alpha numeric characters are converted to
         * underscores to ensure that the resuling string will make a decent
         * file name and that RRD won't have any problems using it
         */
	String firstChoice = RrdLabelUtils.PREFER_IFDESCR ? getIfDescr() : getIfName();
	String secondChoice = RrdLabelUtils.PREFER_IFDESCR ? getIfName() : getIfDescr();
        String label = null;
        if (firstChoice != null) {
            label = RrdLabelUtils.DONT_SANITIZE_IFNAME ? firstChoice : AlphaNumeric.parseAndReplace(firstChoice, '_');
        } else if (secondChoice != null) {
            label = RrdLabelUtils.DONT_SANITIZE_IFNAME ? secondChoice : AlphaNumeric.parseAndReplace(secondChoice, '_');
        } else {
            // TODO: Use IfLabel.NO_IFLABEL instead of "no_ifLabel"
            log.info("Interface ({}) has no ifName and no ifDescr...setting to label to 'no_ifLabel'.", this);
            label = "no_ifLabel";
        }
        return label;
    }

    /**
     * <p>computeLabelForRRD</p>
     *
     * @return a {@link String} object.
     */
    public String computeLabelForRRD() {
        return RrdLabelUtils.computeLabelForRRD(getIfName(), getIfDescr(), getPhysAddr());
    }

    /**
     * <p>addIpInterface</p>
     *
     * @param iface a {@link IpInterface} object.
     */
    public void addIpInterface(IpInterface iface) {
        iface.setSnmpInterface(this);
        ipInterfaces.add(iface);
    }

    /**
     * <p>mergeSnmpInterfaceAttributes</p>
     *
     * @param scannedSnmpIface a {@link SnmpInterface} object.
     */
    public void mergeSnmpInterfaceAttributes(SnmpInterface scannedSnmpIface) {
        
        if (hasNewValue(scannedSnmpIface.getIfAdminStatus(), getIfAdminStatus())) {
            setIfAdminStatus(scannedSnmpIface.getIfAdminStatus());
        }
        
        if (hasNewValue(scannedSnmpIface.getIfAlias(), getIfAlias())) {
            setIfAlias(scannedSnmpIface.getIfAlias());
        }
        
        if (hasNewValue(scannedSnmpIface.getIfDescr(), getIfDescr())) {
            setIfDescr(scannedSnmpIface.getIfDescr());
        }
            
        if (hasNewValue(scannedSnmpIface.getIfName(), getIfName())) {
            setIfName(scannedSnmpIface.getIfName());
        }
        
        if (hasNewValue(scannedSnmpIface.getIfOperStatus(), getIfOperStatus())) {
            setIfOperStatus(scannedSnmpIface.getIfOperStatus());
        }
        
        if (hasNewValue(scannedSnmpIface.getIfSpeed(), getIfSpeed())) {
            setIfSpeed(scannedSnmpIface.getIfSpeed());
        }
        
        if (hasNewValue(scannedSnmpIface.getIfType(), getIfType())) {
            setIfType(scannedSnmpIface.getIfType());
        }
        
        if (hasNewValue(scannedSnmpIface.getPhysAddr(), getPhysAddr())) {
            setPhysAddr(scannedSnmpIface.getPhysAddr());
        }
        
        if (hasNewValue(scannedSnmpIface.getLastCapsdPoll(), getLastCapsdPoll())) {
            setLastCapsdPoll(scannedSnmpIface.getLastCapsdPoll());
        }
        
        if (hasNewValue(scannedSnmpIface.getPoll(), getPoll())) {
            setPoll(scannedSnmpIface.getPoll());
        }

        if (hasNewValue(scannedSnmpIface.getLastSnmpPoll(), getLastSnmpPoll())) {
            setLastSnmpPoll(scannedSnmpIface.getLastSnmpPoll());
        }
        
        if(scannedSnmpIface.isCollectionUserSpecified() || !isCollectionUserSpecified()){
            setCollectionEnabled(scannedSnmpIface.isCollectionEnabled(), scannedSnmpIface.isCollectionUserSpecified());
        }
        
    }

}

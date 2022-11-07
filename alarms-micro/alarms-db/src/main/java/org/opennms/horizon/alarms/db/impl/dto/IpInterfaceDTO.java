/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2021 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2021 The OpenNMS Group, Inc.
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.opennms.horizon.alarms.db.api.EntityVisitor;
import org.opennms.horizon.alarms.db.impl.PrimaryType;
import org.opennms.horizon.alarms.db.impl.utils.InetAddressUtils;

@Entity
@Table(name="ipInterface")
@Getter
@Setter
@NoArgsConstructor
public class IpInterfaceDTO extends EntityDTO implements Serializable {
    private static final long serialVersionUID = 8463903013592837114L;

    @Id
    @Column(nullable=false)
    @SequenceGenerator(name="opennmsSequence", sequenceName="opennmsNxtId", allocationSize = 1)
    @GeneratedValue(generator="opennmsSequence")
    private Integer id;

    @Column(name="ipAddr")
    @Type(type= "org.opennms.horizon.alarms.db.impl.utils.InetAddressUserType")
    private InetAddress ipAddress;

    @Column(name = "netmask")
    @Type(type= "org.opennms.horizon.alarms.db.impl.utils.InetAddressUserType")
    private InetAddress netMask;

    @Column(length=256)
    private String ipHostName;

    @Column(length=1)
    private String isManaged;

    @Embedded
    @Column(length=1)
    @Type(type= "org.opennms.horizon.alarms.db.impl.utils.CharacterUserType.CharacterUserType")
    private PrimaryType isSnmpPrimary = PrimaryType.NOT_ELIGIBLE;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date ipLastCapsdPoll;

    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn
    private SnmpInterfaceDTO snmpInterface;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="ipInterface_metadata", joinColumns = @JoinColumn(name = "id"))
    private List<MetaDataDTO> metaData = new ArrayList<>();

    @Transient
    private List<MetaDataDTO> requisitionedMetaData = new ArrayList<>();

    public IpInterfaceDTO(String ipAddr) {
        ipAddress = InetAddressUtils.getInetAddress(ipAddr);
    }

    public IpInterfaceDTO(InetAddress ipAddr) {
        ipAddress = ipAddr;
    }


    @Transient
    public String getInterfaceId() {
        return getId() == null? null : getId().toString();
    }

    public void setInterfaceId(final String id) {
        setId(Integer.valueOf(id));
    }

    @Transient
    public Date getLastIngressFlow() {
        if (snmpInterface == null) {
            return null;
        }

        return snmpInterface.getLastIngressFlow();
    }

    @Transient
    public Date getLastEgressFlow() {
        if (snmpInterface == null) {
            return null;
        }

        return snmpInterface.getLastEgressFlow();
    }

    /**
     * <p>getIpAddress</p>
     *
     * @return a {@link String} object.
     * @deprecated
     */
    @Transient
    public String getIpAddressAsString() {
        return InetAddressUtils.toIpAddrString(ipAddress);
    }



    @Transient
    public Integer getIfIndex() {
        if (snmpInterface == null) {
            return null;
        }
        return snmpInterface.getIfIndex();

    }

    /**
     * <p>setIfIndex</p>
     *
     * @param ifindex a {@link Integer} object.
     */
    public void setIfIndex(Integer ifindex) {
        if (snmpInterface == null) {
            throw new IllegalStateException("Cannot set ifIndex if snmpInterface relation isn't setup");
        }
        snmpInterface.setIfIndex(ifindex);
        //m_ifIndex = ifindex;
    }

    /**
     * <p>isManaged</p>
     *
     * @return a boolean.
     */
    @Transient
    public boolean isManaged() {
        return "M".equals(getIsManaged());
    }

    public String getSnmpPrimary() {
        final PrimaryType type = isSnmpPrimary == null? PrimaryType.NOT_ELIGIBLE : isSnmpPrimary;
        return type.getCode();
    }

    public void setSnmpPrimary(final String primary) {
        this.isSnmpPrimary = PrimaryType.get(primary);
    }

    /**
     * <p>getIsSnmpPrimary</p>
     *
     * @return a {@link .PrimaryType} object.
     */
    @Transient
    public PrimaryType getIsSnmpPrimary() {
        return isSnmpPrimary == null? PrimaryType.NOT_ELIGIBLE : isSnmpPrimary;
    }

    /**
     * <p>isPrimary</p>
     *
     * @return a boolean.
     */
    @Transient
    public boolean isPrimary(){
        return PrimaryType.PRIMARY.equals(isSnmpPrimary);
    }

    public void addRequisionedMetaData(final MetaDataDTO onmsMetaData) {
        requisitionedMetaData.add(onmsMetaData);
    }

    public void addMetaData(final String context, final String key, final String value) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        final Optional<MetaDataDTO> entry = getMetaData().stream()
                .filter(m -> m.getContext().equals(context))
                .filter(m -> m.getKey().equals(key))
                .findFirst();

        // Update the value if present, otherwise create a new entry
        if (entry.isPresent()) {
            entry.get().setValue(value);
        } else {
            getMetaData().add(new MetaDataDTO(context, key, value));
        }
    }

    public void removeMetaData(final String context, final String key) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(key);
        final Iterator<MetaDataDTO> iterator = getMetaData().iterator();

        while (iterator.hasNext()) {
            final MetaDataDTO onmsNodeMetaData = iterator.next();

            if (context.equals(onmsNodeMetaData.getContext()) && key.equals(onmsNodeMetaData.getKey())) {
                iterator.remove();
            }
        }
    }

    public void removeMetaData(final String context) {
        Objects.requireNonNull(context);
        final Iterator<MetaDataDTO> iterator = getMetaData().iterator();

        while (iterator.hasNext()) {
            final MetaDataDTO onmsNodeMetaData = iterator.next();

            if (context.equals(onmsNodeMetaData.getContext())) {
                iterator.remove();
            }
        }
    }

    /**
     * <p>toString</p>
     *
     * @return a {@link String} object.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("ipAddr", InetAddressUtils.str(ipAddress))
        .add("netMask", InetAddressUtils.str(netMask))
        .add("ipHostName", ipHostName)
        .add("isManaged", isManaged)
        .add("snmpPrimary", isSnmpPrimary)
        .add("ipLastCapsdPoll", ipLastCapsdPoll)
        .toString();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(EntityVisitor visitor) {
        visitor.visitIpInterface(this);

        visitor.visitIpInterfaceComplete(this);
    }

    /**
     * <p>mergeInterfaceAttributes</p>
     *
     * @param scannedIface a {@link IpInterfaceDTO} object.
     */
    public void mergeInterfaceAttributes(IpInterfaceDTO scannedIface) {
        
        if (hasNewValue(scannedIface.getIfIndex(), getIfIndex())) {
            setIfIndex(scannedIface.getIfIndex());
        }
    
        if (hasNewValue(scannedIface.getNetMask(), getNetMask())) {
            setNetMask(scannedIface.getNetMask());
        }
    
        if (hasNewValue(scannedIface.getIsManaged(), getIsManaged())) {
            setIsManaged(scannedIface.getIsManaged());
        }
    
        if (hasNewCollectionTypeValue(scannedIface.getIsSnmpPrimary(), getIsSnmpPrimary())) {
            setIsSnmpPrimary(scannedIface.getIsSnmpPrimary());
        }
    
        if (hasNewValue(scannedIface.getIpHostName(), getIpHostName())) {
            setIpHostName(scannedIface.getIpHostName());
        }
        
        if (hasNewValue(scannedIface.getIpLastCapsdPoll(), getIpLastCapsdPoll())) {
            setIpLastCapsdPoll(scannedIface.getIpLastCapsdPoll());
        }
        
    }
    
    /**
     * <p>hasNewCollectionTypeValue</p>
     *
     * @param newVal a {@link .PrimaryType} object.
     * @param existingVal a {@link .PrimaryType} object.
     * @return a boolean.
     */
    protected static boolean hasNewCollectionTypeValue(PrimaryType newVal, PrimaryType existingVal) {
        return newVal != null && !newVal.equals(existingVal) && newVal != PrimaryType.NOT_ELIGIBLE;
    }


//    /**
//     * <p>mergeMonitoredServices</p>
//     *
//     * @param scannedIface a {@link OnmsIpInterface} object.
//     * @param eventForwarder a {@link org.opennms.netmgt.events.api.EventForwarder} object.
//     * @param deleteMissing a boolean.
//     */
//    public void mergeMonitoredServices(OnmsIpInterface scannedIface, EventForwarder eventForwarder, boolean deleteMissing) {
//
//        // create map of services to serviceType
//        Map<OnmsServiceType, OnmsMonitoredService> serviceTypeMap = new HashMap<OnmsServiceType, OnmsMonitoredService>();
//        for (OnmsMonitoredService svc : scannedIface.getMonitoredServices()) {
//            serviceTypeMap.put(svc.getServiceType(), svc);
//        }
//
//        // for each service in the database
//        for (Iterator<OnmsMonitoredService> it = getMonitoredServices().iterator(); it.hasNext();) {
//            OnmsMonitoredService svc = it.next();
//
//            // find the corresponding scanned service
//            OnmsMonitoredService imported = serviceTypeMap.get(svc.getServiceType());
//            if (imported == null) {
//                if (deleteMissing) {
//                    // there is no scanned service... delete it from the database
//                    it.remove();
//                    svc.visit(new DeleteEventVisitor(eventForwarder));
//                }
//            }
//            else {
//                // otherwice update the service attributes
//                svc.mergeServiceAttributes(imported);
//                svc.mergeMetaData(imported);
//            }
//
//            // mark the service is updated
//            serviceTypeMap.remove(svc.getServiceType());
//        }
//
//        // for any services not found in the database, add them
//        Collection<OnmsMonitoredService> newServices = serviceTypeMap.values();
//        for (OnmsMonitoredService svc : newServices) {
//            svc.setIpInterface(this);
//            getMonitoredServices().add(svc);
//            svc.visit(new AddEventVisitor(eventForwarder));
//        }
//    }

    public void mergeMetaData(IpInterfaceDTO scanned) {
        if (!getMetaData().equals(scanned.getMetaData())) {
            setMetaData(scanned.getMetaData());
        }
    }

    /**
     * <p>updateSnmpInterface</p>
     *
     * @param scannedIface a {@link IpInterfaceDTO} object.
     */
    public void updateSnmpInterface(IpInterfaceDTO scannedIface) {
        
        if (!hasNewValue(scannedIface.getIfIndex(), getIfIndex())) {
            /* no ifIndex in currently scanned interface so don't bother
             * we must have failed to collect data
             */ 
            return;
        }
        
        if (scannedIface.getSnmpInterface() == null) {
            // there is no longer an snmpInterface associated with the ipInterface
            setSnmpInterface(null);
        }
    }
}

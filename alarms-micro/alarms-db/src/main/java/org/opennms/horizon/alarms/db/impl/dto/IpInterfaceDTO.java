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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.hibernate.annotations.Type;
import org.opennms.core.xml.InetAddressXmlAdapter;
import org.opennms.horizon.alarms.db.api.EntityVisitor;
import org.opennms.horizon.alarms.db.impl.PrimaryType;
import org.opennms.horizon.shared.utils.InetAddressUtils;

/**
 * <p>OnmsIpInterface class.</p>
 */
@XmlRootElement(name = "ipInterface")
@Entity
@Table(name="ipInterface")
@XmlAccessorType(XmlAccessType.NONE)
public class IpInterfaceDTO extends EntityDTO implements Serializable {
    private static final long serialVersionUID = 8463903013592837114L;

    private Integer m_id;

    private InetAddress m_ipAddress;

    private InetAddress m_netMask;

    private String m_ipHostName;

    private String m_isManaged;

    @Embedded
    private PrimaryType m_isSnmpPrimary = PrimaryType.NOT_ELIGIBLE;

    private Date m_ipLastCapsdPoll;

    @OneToOne
    @JoinColumn(name = "m_node_node_id")
    private NodeDTO m_node;

    private Set<MonitoredServiceDTO> m_monitoredServices = new LinkedHashSet<>();

    @OneToOne
    @JoinColumn(name = "m_snmp_interface_id")
    private SnmpInterfaceDTO m_snmpInterface;

    private List<MetaDataDTO> m_metaData = new ArrayList<>();

    private List<MetaDataDTO> m_requisitionedMetaData = new ArrayList<>();

    public PrimaryType getM_isSnmpPrimary() {
        return m_isSnmpPrimary;
    }

    public void setM_isSnmpPrimary(PrimaryType m_isSnmpPrimary) {
        this.m_isSnmpPrimary = m_isSnmpPrimary;
    }

    public NodeDTO getM_node() {
        return m_node;
    }

    public void setM_node(NodeDTO m_node) {
        this.m_node = m_node;
    }

    public SnmpInterfaceDTO getM_snmpInterface() {
        return m_snmpInterface;
    }

    public void setM_snmpInterface(SnmpInterfaceDTO m_snmpInterface) {
        this.m_snmpInterface = m_snmpInterface;
    }

    /**
     * <p>Constructor for OnmsIpInterface.</p>
     */
    public IpInterfaceDTO() {
    }

    /**
     * minimal constructor
     * @deprecated Use the {@link InetAddress} version instead.
     * @param ipAddr a {@link String} object.
     * @param node a {@link NodeDTO} object.
     */
    public IpInterfaceDTO(String ipAddr, NodeDTO node) {
        this(InetAddressUtils.getInetAddress(ipAddr), node);
    }

    public IpInterfaceDTO(String ipAddr) {
        m_ipAddress = InetAddressUtils.getInetAddress(ipAddr);
    }

    /**
     * minimal constructor
     *
     * @param ipAddr a {@link String} object.
     * @param node a {@link NodeDTO} object.
     */
    public IpInterfaceDTO(InetAddress ipAddr, NodeDTO node) {
        m_ipAddress = ipAddr;
        m_node = node;
        if (node != null) {
            node.getIpInterfaces().add(this);
        }
    }

    /**
     * Unique identifier for ipInterface.
     *
     * @return a {@link Integer} object.
     */
    @Id
    @Column(nullable=false)
    @XmlTransient
    @SequenceGenerator(name="opennmsSequence", sequenceName="opennmsNxtId", allocationSize = 1)
    @GeneratedValue(generator="opennmsSequence")    
    public Integer getId() {
        return m_id;
    }
    
    /**
     * <p>setId</p>
     *
     * @param id a {@link Integer} object.
     */
    public void setId(Integer id) {
        m_id = id;
    }

    /**
     * <p>getInterfaceId</p>
     *
     * @return a {@link String} object.
     */
    @XmlID
    @XmlAttribute(name="id")
    @Transient
    public String getInterfaceId() {
        return getId() == null? null : getId().toString();
    }

    public void setInterfaceId(final String id) {
        setId(Integer.valueOf(id));
    }

    @Transient
    @XmlAttribute(name="lastIngressFlow")
    public Date getLastIngressFlow() {
        if (m_snmpInterface == null) {
            return null;
        }

        return m_snmpInterface.getLastIngressFlow();
    }

    @Transient
    @XmlAttribute(name="lastEgressFlow")
    public Date getLastEgressFlow() {
        if (m_snmpInterface == null) {
            return null;
        }

        return m_snmpInterface.getLastEgressFlow();
    }

    /**
     * <p>getIpAddress</p>
     *
     * @return a {@link String} object.
     * @deprecated
     */
    @Transient
    @XmlTransient
    public String getIpAddressAsString() {
        return InetAddressUtils.toIpAddrString(m_ipAddress);
    }

    //@Column(name="ifIndex")
    /**
     * <p>getIfIndex</p>
     *
     * @return a {@link Integer} object.
     */
    @Transient
    @XmlAttribute(name="ifIndex")
    public Integer getIfIndex() {
        if (m_snmpInterface == null) {
            return null;
        }
        return m_snmpInterface.getIfIndex();
        //return m_ifIndex;
    }

    /**
     * <p>setIfIndex</p>
     *
     * @param ifindex a {@link Integer} object.
     */
    public void setIfIndex(Integer ifindex) {
        if (m_snmpInterface == null) {
            throw new IllegalStateException("Cannot set ifIndex if snmpInterface relation isn't setup");
        }
        m_snmpInterface.setIfIndex(ifindex);
        //m_ifIndex = ifindex;
    }

    /**
     * <p>getIpHostName</p>
     *
     * @return a {@link String} object.
     */
    @Column(name="ipHostName", length=256)
    @XmlElement(name="hostName")
    public String getIpHostName() {
        return m_ipHostName;
    }

    /**
     * <p>setIpHostName</p>
     *
     * @param iphostname a {@link String} object.
     */
    public void setIpHostName(String iphostname) {
        m_ipHostName = iphostname;
    }

    /**
     * <p>getIsManaged</p>
     *
     * @return a {@link String} object.
     */
    @Column(name="isManaged", length=1)
    @XmlAttribute(name="isManaged")
    public String getIsManaged() {
        return m_isManaged;
    }

    /**
     * <p>setIsManaged</p>
     *
     * @param ismanaged a {@link String} object.
     */
    public void setIsManaged(String ismanaged) {
        m_isManaged = ismanaged;
    }

    /**
     * <p>isManaged</p>
     *
     * @return a boolean.
     */
    @Transient
    @XmlTransient
    public boolean isManaged() {
        return "M".equals(getIsManaged());
    }

    /**
     * <p>getIpLastCapsdPoll</p>
     *
     * @return a {@link Date} object.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="ipLastCapsdPoll")
    @XmlElement(name="lastCapsdPoll")
    public Date getIpLastCapsdPoll() {
        return m_ipLastCapsdPoll;
    }

    /**
     * <p>setIpLastCapsdPoll</p>
     *
     * @param iplastcapsdpoll a {@link Date} object.
     */
    public void setIpLastCapsdPoll(Date iplastcapsdpoll) {
        m_ipLastCapsdPoll = iplastcapsdpoll;
    }

    @Column(name="isSnmpPrimary", length=1)
    @XmlAttribute(name="snmpPrimary")
    @Type(type="CharacterUserType")
    public String getSnmpPrimary() {
        final PrimaryType type = m_isSnmpPrimary == null? PrimaryType.NOT_ELIGIBLE : m_isSnmpPrimary;
        return type.getCode();
    }

    public void setSnmpPrimary(final String primary) {
        this.m_isSnmpPrimary = PrimaryType.get(primary);
    }

    /**
     * <p>getIsSnmpPrimary</p>
     *
     * @return a {@link .PrimaryType} object.
     */
    @XmlTransient
    @Transient
    public PrimaryType getIsSnmpPrimary() {
        return m_isSnmpPrimary == null? PrimaryType.NOT_ELIGIBLE : m_isSnmpPrimary;
    }

    /**
     * <p>setIsSnmpPrimary</p>
     *
     * @param issnmpprimary a {@link .PrimaryType} object.
     */
    public void setIsSnmpPrimary(PrimaryType issnmpprimary) {
        m_isSnmpPrimary = issnmpprimary;
    }

    /**
     * <p>isPrimary</p>
     *
     * @return a boolean.
     */
    @Transient
    @XmlTransient
    public boolean isPrimary(){
        return PrimaryType.PRIMARY.equals(m_isSnmpPrimary);
    }

    @Transient
    @XmlTransient
    public List<MetaDataDTO> getRequisitionedMetaData() {
        return m_requisitionedMetaData;
    }

    public void setRequisionedMetaData(final List<MetaDataDTO> requisitionedMetaData) {
        m_requisitionedMetaData = requisitionedMetaData;
    }

    public void addRequisionedMetaData(final MetaDataDTO onmsMetaData) {
        m_requisitionedMetaData.add(onmsMetaData);
    }

    
    @XmlTransient
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="ipInterface_metadata", joinColumns = @JoinColumn(name = "id"))
    public List<MetaDataDTO> getMetaData() {
        return m_metaData;
    }

    public void setMetaData(final List<MetaDataDTO> metaData) {
        m_metaData = metaData;
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
     * <p>getNode</p>
     *
     * @return a {@link NodeDTO} object.
     */
    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="nodeId")
    @XmlElement(name="nodeId")
    public NodeDTO getNode() {
        return m_node;
    }

    /**
     * <p>setNode</p>
     *
     * @param node a {@link NodeDTO} object.
     */
    public void setNode(NodeDTO node) {
        m_node = node;
    }

    @Transient
    @XmlTransient
    public Integer getNodeId() {
        if (m_node != null) {
            return m_node.getId();
        }
        return null;
    }

    /**
     * The services on this interface
     *
     * @return a {@link Set} object.
     */
    @XmlTransient
    @OneToMany(mappedBy="ipInterface",orphanRemoval=true)
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    public Set<MonitoredServiceDTO> getMonitoredServices() {
        return m_monitoredServices ;
    }

    /**
     * <p>setMonitoredServices</p>
     *
     * @param ifServices a {@link Set} object.
     */
    public void setMonitoredServices(Set<MonitoredServiceDTO> ifServices) {
        m_monitoredServices = ifServices;
    }

    public void addMonitoredService(final MonitoredServiceDTO svc) {
        m_monitoredServices.add(svc);
    }

    public void removeMonitoredService(final MonitoredServiceDTO svc) {
        m_monitoredServices.remove(svc);
    }

    /**
     * The SnmpInterface associated with this interface if any
     *
     * @return a {@link .OnmsSnmpInterface} object.
     */
    @XmlElement(name = "snmpInterface")
    @ManyToOne(optional=true, fetch=FetchType.LAZY)
    @JoinColumn(name="snmpInterfaceId")
    public SnmpInterfaceDTO getSnmpInterface() {
        return m_snmpInterface;
    }


    /**
     * <p>setSnmpInterface</p>
     *
     * @param snmpInterface a {@link .OnmsSnmpInterface} object.
     */
    public void setSnmpInterface(SnmpInterfaceDTO snmpInterface) {
        m_snmpInterface = snmpInterface;
    }

    /**
     * <p>toString</p>
     *
     * @return a {@link String} object.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
        .add("id", m_id)
        .add("ipAddr", InetAddressUtils.str(m_ipAddress))
        .add("netMask", InetAddressUtils.str(m_netMask))
        .add("ipHostName", m_ipHostName)
        .add("isManaged", m_isManaged)
        .add("snmpPrimary", m_isSnmpPrimary)
        .add("ipLastCapsdPoll", m_ipLastCapsdPoll)
        .add("nodeId", getNodeId())
        .toString();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(EntityVisitor visitor) {
        visitor.visitIpInterface(this);

        for (MonitoredServiceDTO monSvc : getMonitoredServices()) {
            monSvc.visit(visitor);
        }

        visitor.visitIpInterfaceComplete(this);
    }

    /**
     * <p>getInetAddress</p>
     *
     * @return a {@link InetAddress} object.
     */
    @Column(name="ipAddr")
    @XmlElement(name="ipAddress")
    @Type(type="InetAddressUserType")
    @XmlJavaTypeAdapter(InetAddressXmlAdapter.class)
    public InetAddress getIpAddress() {
        return m_ipAddress;
    }

    /**
     * <p>setInetAddress</p>
     *
     * @param ipaddr a {@link String} object.
     */
    public void setIpAddress(InetAddress ipaddr) {
        m_ipAddress = ipaddr;
    }

    @Column(name = "netmask")
    @Type(type="InetAddressUserType")
    @XmlJavaTypeAdapter(InetAddressXmlAdapter.class)
    public InetAddress getNetMask() {
        return m_netMask;
    }

    public void setNetMask(final InetAddress netMask) {
        m_netMask = netMask;
    }
    
    /**
     * <p>isDown</p>
     *
     * @return a boolean.
     */
    @Transient
    @XmlAttribute(name="isDown")
    public boolean isDown() {
        boolean down = true;
        for (MonitoredServiceDTO svc : m_monitoredServices) {
            if (!svc.isDown()) {
                return !down;
            }
        }
        return down;
    }

    
    @Transient 
    @XmlAttribute
    public int getMonitoredServiceCount () {
    	return m_monitoredServices.size();
    }
    
    /**
     * <p>getMonitoredServiceByServiceType</p>
     *
     * @param svcName a {@link String} object.
     * @return a {@link .OnmsMonitoredService} object.
     */
    
    public MonitoredServiceDTO getMonitoredServiceByServiceType(String svcName) {
        for (MonitoredServiceDTO monSvc : getMonitoredServices()) {
            if (monSvc.getServiceType().getName().equals(svcName)) {
                return monSvc;
            }
        }
        return null;
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
        } else {
            // locate the snmpInterface on this node that has the new ifIndex and set it
            // into the interface
            SnmpInterfaceDTO snmpIface = getNode().getSnmpInterfaceWithIfIndex(scannedIface.getIfIndex());
            setSnmpInterface(snmpIface);
        }
        
        
        
    }
//
//    /**
//     * <p>mergeInterface</p>
//     *
//     * @param scannedIface a {@link OnmsIpInterface} object.
//     * @param eventForwarder a {@link org.opennms.netmgt.events.api.EventForwarder} object.
//     * @param deleteMissing a boolean.
//     */
//    public void mergeInterface(OnmsIpInterface scannedIface, EventForwarder eventForwarder, boolean deleteMissing) {
//        updateSnmpInterface(scannedIface);
//        mergeInterfaceAttributes(scannedIface);
//        mergeMonitoredServices(scannedIface, eventForwarder, deleteMissing);
//        mergeMetaData(scannedIface);
//    }

    @Transient
    @XmlTransient
    public String getForeignSource() {
        if (getNode() != null) {
            return getNode().getForeignSource();
        }
        return null;
    }

    @Transient
    @XmlTransient
    public String getForeignId() {
        if (getNode() != null) {
            return getNode().getForeignId();
        }
        return null;
    }

}

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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.opennms.horizon.alarms.db.api.EntityVisitor;

@Entity
@Table(name="ifServices")
@Getter
@Setter
public class MonitoredServiceDTO extends EntityDTO implements Serializable, Comparable<MonitoredServiceDTO> {
    private static final long serialVersionUID = 7899180234592272274L;

    @Id
    @Column(nullable=false)
    @SequenceGenerator(name="opennmsSequence", sequenceName="opennmsNxtId", allocationSize = 1)
    @GeneratedValue(generator="opennmsSequence")
    private Integer id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date lastGood;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date lastFail;

    @Column(length=16)
    private String qualifier;

    @Column(length=1)
    private String status;

    @Column(length=1)
    private String source;

    @Column(length=1)
    private String notify;

    @ManyToOne(optional=false)
    @JoinColumn(name="serviceId")
    private ServiceTypeDTO serviceType;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="ipInterfaceId")
    private IpInterfaceDTO ipInterface;


    @ManyToMany(
        cascade={CascadeType.PERSIST, CascadeType.MERGE}
    )
    @JoinTable(
        name="application_service_map",
        joinColumns={@JoinColumn(name="ifserviceid")},
        inverseJoinColumns={@JoinColumn(name="appid")}
    )
    private Set<ApplicationDTO> applications = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="ifServices_metadata", joinColumns = @JoinColumn(name = "id"))
    private List<MetaDataDTO> metaData = new ArrayList<>();

    public static final Map<String, String> STATUS_MAP;

    static {
        STATUS_MAP = new HashMap<String, String>();
        STATUS_MAP.put("A", "Managed");
        STATUS_MAP.put("U", "Unmanaged");
        STATUS_MAP.put("D", "Deleted");
        STATUS_MAP.put("F", "Forced Unmanaged");
        STATUS_MAP.put("N", "Not Monitored");
        STATUS_MAP.put("R", "Rescan to Resume");
        STATUS_MAP.put("S", "Rescan to Suspend");
        STATUS_MAP.put("X", "Remotely Monitored");
	}

    /**
     * <p>Constructor for OnmsMonitoredService.</p>
     */
    public MonitoredServiceDTO() {
    }

    /**
     * <p>Constructor for OnmsMonitoredService.</p>
     *
     * @param ipIf a {@link IpInterfaceDTO} object.
     * @param serviceType a {@link ServiceTypeDTO} object.
     */
    public MonitoredServiceDTO(IpInterfaceDTO ipIf, ServiceTypeDTO serviceType) {
        ipInterface = ipIf;
        ipInterface.getMonitoredServices().add(this);
        this.serviceType = serviceType;

    }

    /**
     * <p>getIpAddress</p>
     *
     * @return a {@link String} object.
     */
    @Transient
    public InetAddress getIpAddress() {
        return ipInterface.getIpAddress();
    }

    /**
     * <p>getIpAddress</p>
     *
     * @return a {@link String} object.
     * 
     * @deprecated
     */
    @Transient
    public String getIpAddressAsString() {
        return ipInterface.getIpAddressAsString();
    }

    /**
     * <p>getIfIndex</p>
     *
     * @return a {@link Integer} object.
     */
    @Transient
    public Integer getIfIndex() {
        return ipInterface.getIfIndex();
    }

    
    @Transient
    public String getStatusLong() {
    	return STATUS_MAP.get(getStatus());
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


    @Transient
    public Integer getIpInterfaceId() {
        return ipInterface.getId();
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
        .add("lastGood", lastGood)
        .add("lastFail", lastFail)
        .add("qualifier", qualifier)
        .add("status", status)
        .add("source", source)
        .add("notify", notify)
        .add("serviceType", serviceType)
        .toString();
    }

    /**
     * <p>getServiceId</p>
     *
     * @return a {@link Integer} object.
     */
    @Transient
    
    public Integer getServiceId() {
        return getServiceType().getId();
    }

    /** {@inheritDoc} */
    @Override
    public void visit(EntityVisitor visitor) {
        visitor.visitMonitoredService(this);
        visitor.visitMonitoredServiceComplete(this);
    }

    /**
     * <p>getServiceName</p>
     *
     * @return a {@link String} object.
     */
    @Transient
    
    public String getServiceName() {
        return getServiceType().getName();
    }

    /**
     * <p>addApplication</p>
     *
     * @param application a {@link ApplicationDTO} object.
     * @return a boolean.
     */
    public boolean addApplication(ApplicationDTO application) {
        return getApplications().add(application);
    }

    /**
     * <p>removeApplication</p>
     *
     * @param application a {@link ApplicationDTO} object.
     * @return a boolean.
     */
    public boolean removeApplication(ApplicationDTO application) {
        return getApplications().remove(application);
    }

    /**
     * <p>compareTo</p>
     *
     * @param o a {@link MonitoredServiceDTO} object.
     * @return a int.
     */
    @Override
    public int compareTo(MonitoredServiceDTO o) {
        int diff;

        return getServiceName().compareToIgnoreCase(o.getServiceName());
    }

    /**
     * <p>mergeServiceAttributes</p>
     *
     * @param scanned a {@link MonitoredServiceDTO} object.
     */
    public void mergeServiceAttributes(MonitoredServiceDTO scanned) {

        if (hasNewValue(scanned.getQualifier(), getQualifier())) {
            setQualifier(scanned.getQualifier());
        }

        if (hasNewStatusValue(scanned.getStatus(), getStatus())) {
            setStatus(scanned.getStatus());
        }
        
        if (hasNewValue(scanned.getSource(), getSource())) {
            setSource(scanned.getSource());
        }

        if (hasNewValue(scanned.getNotify(), getNotify())) {
            setNotify(scanned.getNotify());
        }

    }

    public void mergeMetaData(MonitoredServiceDTO scanned) {
        if (!getMetaData().equals(scanned.getMetaData())) {
            setMetaData(scanned.getMetaData());
        }
    }

	private boolean hasNewStatusValue(String newStatus, String oldStatus) 
	{
		/*
		 * Don't overwrite the 'Not Monitored' in the database when provisioning the
		 * node.  The Poller will update it when scheduling it packages.
		 */
		return !"N".equals(oldStatus) && newStatus != null && !newStatus.equals(oldStatus);
	}
}

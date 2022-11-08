package org.opennms.horizon.inventory.model;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.UUID;

// Lombok isn't playing nicely with MapStruct, so generate getters and setters with IDE for now.
//@Getter
//@Setter
@RequiredArgsConstructor
@Entity
public class MonitoredService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Type(type="pg-uuid")
    @Column(name = "tenant_id")
    private UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitored_service_type_id", referencedColumnName = "id")
    private MonitoredServiceType monitoredServiceType;

    @Column(name = "monitored_service_type_id", insertable = false, updatable = false)
    private long monitoredServiceTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_interface_id", referencedColumnName = "id")
    private IpInterface ipInterface;

    @Column(name = "ip_interface_id", insertable = false, updatable = false)
    private long ipInterfaceId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public MonitoredServiceType getMonitoredServiceType() {
        return monitoredServiceType;
    }

    public void setMonitoredServiceType(MonitoredServiceType monitoredServiceType) {
        this.monitoredServiceType = monitoredServiceType;
    }

    public long getMonitoredServiceTypeId() {
        return monitoredServiceTypeId;
    }

    public void setMonitoredServiceTypeId(long monitoredServiceTypeId) {
        this.monitoredServiceTypeId = monitoredServiceTypeId;
    }

    public IpInterface getIpInterface() {
        return ipInterface;
    }

    public void setIpInterface(IpInterface ipInterface) {
        this.ipInterface = ipInterface;
    }

    public long getIpInterfaceId() {
        return ipInterfaceId;
    }

    public void setIpInterfaceId(long ipInterfaceId) {
        this.ipInterfaceId = ipInterfaceId;
    }
}

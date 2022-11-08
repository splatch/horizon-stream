package org.opennms.horizon.inventory.model;

import com.vladmihalcea.hibernate.type.basic.Inet;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLInetType;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

// Lombok isn't playing nicely with MapStruct, so generate getters and setters with IDE for now.
//@Getter
//@Setter
@RequiredArgsConstructor
@Entity
public class MonitoringSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Type(type="pg-uuid")
    @Column(name = "tenant_id")
    private UUID tenantId;

    @NotNull
    @Column(name = "system_id")
    private String systemId;

    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monitoring_location_id", referencedColumnName = "id")
    private MonitoringLocation monitoringLocation;

    @Column(name = "monitoring_location_id", insertable = false, updatable = false)
    private long monitoringLocationId;

    @NotNull
    @Column(name = "last_checked_in", columnDefinition = "TIMESTAMP")
    private LocalDateTime lastCheckedIn;

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

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public MonitoringLocation getMonitoringLocation() {
        return monitoringLocation;
    }

    public void setMonitoringLocation(MonitoringLocation monitoringLocation) {
        this.monitoringLocation = monitoringLocation;
    }

    public long getMonitoringLocationId() {
        return monitoringLocationId;
    }

    public void setMonitoringLocationId(long monitoringLocationId) {
        this.monitoringLocationId = monitoringLocationId;
    }

    public LocalDateTime getLastCheckedIn() {
        return lastCheckedIn;
    }

    public void setLastCheckedIn(LocalDateTime lastCheckedIn) {
        this.lastCheckedIn = lastCheckedIn;
    }
}

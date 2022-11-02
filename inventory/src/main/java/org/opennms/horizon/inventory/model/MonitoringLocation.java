package org.opennms.horizon.inventory.model;

import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

// Lombok isn't playing nicely with MapStruct, so generate getters and setters with IDE for now.
//@Getter
//@Setter
@RequiredArgsConstructor
@Entity
public class MonitoringLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Type(type="pg-uuid")
    @Column(name = "tenant_id")
    private UUID tenantId;

    @NotNull
    private String location;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

package org.opennms.horizon.inventory.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.UUID;

// TODO: Lombok isn't playing nicely with MapStruct, so generate getters and setters with IDE for now.
//@Getter
//@Setter
@RequiredArgsConstructor
@Entity
public class MonitoringLocations {
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

    public UUID getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(UUID tenant_id) {
        this.tenant_id = tenant_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

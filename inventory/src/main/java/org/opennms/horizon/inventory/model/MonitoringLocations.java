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

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class MonitoringLocations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Type(type="pg-uuid")
    private UUID tenant_id;

    @NotNull
    private String location;
}

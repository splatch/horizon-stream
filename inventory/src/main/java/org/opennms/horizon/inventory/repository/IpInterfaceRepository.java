package org.opennms.horizon.inventory.repository;

import java.util.List;

import com.vladmihalcea.hibernate.type.basic.Inet;
import org.opennms.horizon.inventory.model.IpInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IpInterfaceRepository extends JpaRepository<IpInterface, Long> {
    List<IpInterface> findByTenantId(String tenantId);

    @Query("select ip from IpInterface ip inner join Node n on ip.tenantId = n.tenantId inner join MonitoringLocation ml on n.tenantId = ml.tenantId where ip.ipAddress = ?1 and ml.location = ?2 and ip.tenantId = ?3")
    List<IpInterface> findByIpAddressAndLocationAndTenantId(Inet ipAddress, String location, String tenantId);
}

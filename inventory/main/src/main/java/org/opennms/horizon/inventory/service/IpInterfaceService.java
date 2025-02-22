package org.opennms.horizon.inventory.service;

import lombok.RequiredArgsConstructor;
import org.opennms.horizon.azure.api.AzureScanNetworkInterfaceItem;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.mapper.IpInterfaceMapper;
import org.opennms.horizon.inventory.model.IpInterface;
import org.opennms.horizon.inventory.model.Node;
import org.opennms.horizon.inventory.model.SnmpInterface;
import org.opennms.horizon.inventory.repository.IpInterfaceRepository;
import org.opennms.horizon.shared.utils.InetAddressUtils;
import org.opennms.node.scan.contract.IpInterfaceResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TODO: perhaps rename this to ScanResultIpProcessor, or the like.  The name IpInterfaceService can easily lead to
 * tight coupling problems because "ip interface" is a lower-level logical concept than "Azure Scan" and "SNMP".
 * Alternatively, move the azure-specific and snmp-specific handling up a layer and make the operations here work
 * on ip-interface (and lower) concepts only.
 */
@Service
@RequiredArgsConstructor
public class IpInterfaceService {
    private final IpInterfaceRepository modelRepo;

    private final IpInterfaceMapper mapper;

    public List<IpInterfaceDTO> findByTenantId(String tenantId) {
        List<IpInterface> all = modelRepo.findByTenantId(tenantId);
        return all
            .stream()
            .map(mapper::modelToDTO)
            .collect(Collectors.toList());
    }

    public Optional<IpInterfaceDTO> getByIdAndTenantId(long id, String tenantId) {
        return modelRepo.findByIdAndTenantId(id, tenantId).map(mapper::modelToDTO);
    }

    public Optional<IpInterfaceDTO> findByIpAddressAndLocationAndTenantId(String ipAddress, String location, String tenantId) {
            Optional<IpInterface> optional = modelRepo.findByIpAddressAndLocationIdAndTenantId(InetAddressUtils.getInetAddress(ipAddress), Long.valueOf(location), tenantId);
            return optional.map(mapper::modelToDTO);
    }

    public void createFromAzureScanResult(String tenantId, Node node, AzureScanNetworkInterfaceItem networkInterfaceItem) {
        IpInterface ipInterface = new IpInterface();
        ipInterface.setNode(node);
        ipInterface.setTenantId(tenantId);
        ipInterface.setSnmpPrimary(false);
        ipInterface.setIpAddress(InetAddressUtils.getInetAddress(networkInterfaceItem.getIpAddress()));
        modelRepo.save(ipInterface);
    }

    // TODO: is this executed inside a transaction?  If not, there is a race condition in this code (find-then-save).
    public void createOrUpdateFromScanResult(String tenantId, Node node, IpInterfaceResult result, Map<Integer, SnmpInterface> ifIndexSNMPMap) {
        modelRepo.findByNodeIdAndTenantIdAndIpAddress(node.getId(), tenantId, InetAddressUtils.getInetAddress(result.getIpAddress()))
            .ifPresentOrElse(ipInterface -> {
                ipInterface.setHostname(result.getIpHostName());
                ipInterface.setNetmask(result.getNetmask());
                var snmpInterface = ifIndexSNMPMap.get(result.getIfIndex());
                if(snmpInterface != null) {
                    ipInterface.setSnmpInterface(snmpInterface);
                }
                ipInterface.setIfIndex(result.getIfIndex());
                modelRepo.save(ipInterface);
            }, () -> {
                IpInterface ipInterface = mapper.fromScanResult(result);
                ipInterface.setNode(node);
                ipInterface.setTenantId(tenantId);
                ipInterface.setSnmpPrimary(false);
                ipInterface.setHostname(result.getIpHostName());
                ipInterface.setIfIndex(result.getIfIndex());
                var snmpInterface = ifIndexSNMPMap.get(result.getIfIndex());
                if(snmpInterface != null) {
                    ipInterface.setSnmpInterface(snmpInterface);
                }
                modelRepo.save(ipInterface);
            });
    }
}

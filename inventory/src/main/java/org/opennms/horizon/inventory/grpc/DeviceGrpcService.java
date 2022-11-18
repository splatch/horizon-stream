package org.opennms.horizon.inventory.grpc;

import com.google.common.net.InetAddresses;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.grpc.Context;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opennms.horizon.inventory.dto.DeviceCreateDTO;
import org.opennms.horizon.inventory.dto.DeviceList;
import org.opennms.horizon.inventory.dto.DeviceServiceGrpc;
import org.opennms.horizon.inventory.dto.IpInterfaceDTO;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.mapper.NodeMapper;
import org.opennms.horizon.inventory.model.Node;
import org.opennms.horizon.inventory.service.IpInterfaceService;
import org.opennms.horizon.inventory.service.NodeService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class DeviceGrpcService extends DeviceServiceGrpc.DeviceServiceImplBase {
    private final NodeService nodeService;
    private final IpInterfaceService ipInterfaceService;
    private final NodeMapper nodeMapper;
    private final TenantLookup tenantLookup;

    @Override
    @Transactional
    public void createDevice(DeviceCreateDTO request, StreamObserver<NodeDTO> responseObserver) {
        Optional<String> tenantId = tenantLookup.lookupTenantId(Context.current());
        boolean valid = validateInput(request, tenantId.orElseThrow(), responseObserver);

        if (valid) {
            Node node = nodeService.createDevice(request, tenantId.orElseThrow());

            responseObserver.onNext(nodeMapper.modelToDTO(node));
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listDevices(Empty request, StreamObserver<DeviceList> responseObserver) {
        List<NodeDTO> list = tenantLookup.lookupTenantId(Context.current())
            .map(nodeService::findByTenantId).orElseThrow();
        responseObserver.onNext(DeviceList.newBuilder().addAllDevices(list).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getDeviceById(Int64Value request, StreamObserver<NodeDTO> responseObserver) {
        Optional<NodeDTO> node = tenantLookup.lookupTenantId(Context.current())
            .map(tenantId-> nodeService.getByIdAndTenantId(request.getValue(), tenantId))
            .orElseThrow();
        if(node.isPresent()){
            responseObserver.onNext(node.get());
            responseObserver.onCompleted();
        } else {
            Status status = Status.newBuilder()
                .setCode(Code.NOT_FOUND_VALUE)
                .setMessage("Device with id: " + request.getValue() + " doesn't exist.").build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
    }

    private boolean validateInput(DeviceCreateDTO request, String tenantId, StreamObserver<NodeDTO> responseObserver) {
        boolean valid = true;

        if (request.hasManagementIp()) {
            if (!InetAddresses.isInetAddress(request.getManagementIp())) {
                valid = false;
                Status status = Status.newBuilder()
                    .setCode(Code.INVALID_ARGUMENT_VALUE)
                    .setMessage("Bad management_ip: " + request.getManagementIp())
                    .build();
                responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            } else {
                List<IpInterfaceDTO> ipList = ipInterfaceService.findByIpAddressAndLocationAndTenantId(request.getManagementIp(), request.getLocation(), tenantId);
                if (!ipList.isEmpty()) {
                    valid = false;
                    Status status = Status.newBuilder()
                        .setCode(Code.ALREADY_EXISTS_VALUE)
                        .setMessage("Ip address already exists for location")
                        .build();
                    responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                }
            }
        }

        return valid;
    }
}

package org.opennms.horizon.inventory.grpc;

import io.grpc.stub.StreamObserver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opennms.horizon.inventory.dto.DeviceCreateDTO;
import org.opennms.horizon.inventory.dto.DeviceServiceGrpc;
import org.opennms.horizon.inventory.dto.NodeDTO;
import org.opennms.horizon.inventory.mapper.NodeMapper;
import org.opennms.horizon.inventory.service.NodeService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DeviceGrpcTest {
    @InjectMocks
    DeviceGrpcService deviceGrpcService;

    @Mock
    NodeService nodeService;

    @Mock
    NodeMapper nodeMapper;

    @Mock
    TenantLookup tenantLookup;

    @Test
    public void createDevice() {
        doReturn(Optional.of("ANY")).when(tenantLookup).lookupTenantId(any());

        DeviceCreateDTO deviceCreateDTO = DeviceCreateDTO.newBuilder()
            .setLabel("Label")
            .setLocation("loc")
            .setManagementIp("127.0.0.1")
            .build();

        StreamObserver<NodeDTO> obs = mock(StreamObserver.class);

        deviceGrpcService.createDevice(deviceCreateDTO, obs);

        verify(obs, times(0)).onError(any());
        verify(obs).onCompleted();
    }

    @Test
    public void createDeviceBadIp() {
        DeviceCreateDTO deviceCreateDTO = DeviceCreateDTO.newBuilder()
            .setLabel("Label")
            .setLocation("loc")
            .setManagementIp("BAD")
            .build();

        StreamObserver<NodeDTO> obs = mock(StreamObserver.class);

        deviceGrpcService.createDevice(deviceCreateDTO, obs);

        verify(obs).onError(any());
        verify(obs, times(0)).onCompleted();
    }
}

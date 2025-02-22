package org.opennms.horizon.inventory.grpc.discovery;

import com.google.protobuf.BoolValue;
import com.google.protobuf.Empty;
import com.google.protobuf.Int64Value;
import com.google.rpc.Code;
import io.grpc.Context;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.opennms.horizon.inventory.dto.PassiveDiscoveryDTO;
import org.opennms.horizon.inventory.dto.PassiveDiscoveryListDTO;
import org.opennms.horizon.inventory.dto.PassiveDiscoveryToggleDTO;
import org.opennms.horizon.inventory.dto.PassiveDiscoveryUpsertDTO;
import org.opennms.horizon.inventory.grpc.TenantLookup;
import org.opennms.horizon.inventory.service.discovery.PassiveDiscoveryService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PassiveDiscoveryGrpcServiceTest {

    public static final String TEST_TENANT_ID = "x-tenant-id-x";

    private TenantLookup mockTenantLookup;
    private PassiveDiscoveryService mockIcmpActiveDiscoveryService;

    private PassiveDiscoveryUpsertDTO testNewPassiveDiscoveryUpsertDTO;
    private PassiveDiscoveryUpsertDTO testExistingPassiveDiscoveryUpsertDTO;
    private PassiveDiscoveryToggleDTO testPassiveDiscoveryToggleDTO;

    private PassiveDiscoveryGrpcService target;

    @BeforeEach
    public void setUp() {
        mockTenantLookup = Mockito.mock(TenantLookup.class);
        mockIcmpActiveDiscoveryService = Mockito.mock(PassiveDiscoveryService.class);

        testNewPassiveDiscoveryUpsertDTO =
            PassiveDiscoveryUpsertDTO.newBuilder()
                .setName("x-passive-discovery-upsert-x")
                .build();

        testExistingPassiveDiscoveryUpsertDTO =
            PassiveDiscoveryUpsertDTO.newBuilder()
                .setName("x-passive-discovery-upsert-x")
                .setId(1313)
                .build();

        testPassiveDiscoveryToggleDTO =
            PassiveDiscoveryToggleDTO.newBuilder()
                .setId(1717)
                .build();

        target = new PassiveDiscoveryGrpcService(mockTenantLookup, mockIcmpActiveDiscoveryService);
    }

    @Test
    void testCreateNewDiscovery() {
        //
        // Setup Test Data and Interactions
        //
        var testDiscovery =
            PassiveDiscoveryDTO.newBuilder()
                .setName("x-active-discovery-x")
                .build();

        prepareCommonTenantLookup();
        StreamObserver<PassiveDiscoveryDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);
        Mockito.when(mockIcmpActiveDiscoveryService.createDiscovery(TEST_TENANT_ID, testNewPassiveDiscoveryUpsertDTO)).thenReturn(testDiscovery);

        //
        // Execute
        //
        target.upsertDiscovery(testNewPassiveDiscoveryUpsertDTO, mockStreamObserver);

        //
        // Verify the Results
        //
        Mockito.verify(mockStreamObserver).onNext(testDiscovery);
        Mockito.verify(mockStreamObserver).onCompleted();
    }

    @Test
    void testUpdateExistingDiscovery() {
        //
        // Setup Test Data and Interactions
        //
        var testDiscovery =
            PassiveDiscoveryDTO.newBuilder()
                .setName("x-active-discovery-x")
                .build();

        prepareCommonTenantLookup();
        StreamObserver<PassiveDiscoveryDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);
        Mockito.when(mockIcmpActiveDiscoveryService.updateDiscovery(TEST_TENANT_ID, testExistingPassiveDiscoveryUpsertDTO)).thenReturn(testDiscovery);

        //
        // Execute
        //
        target.upsertDiscovery(testExistingPassiveDiscoveryUpsertDTO, mockStreamObserver);

        //
        // Verify the Results
        //
        Mockito.verify(mockStreamObserver).onNext(testDiscovery);
        Mockito.verify(mockStreamObserver).onCompleted();
    }

    @Test
    void testCreateDiscoveryException() {
        //
        // Setup Test Data and Interactions
        //
        var testException = new RuntimeException("x-test-exception-x");
        prepareCommonTenantLookup();
        StreamObserver<PassiveDiscoveryDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);
        Mockito.when(mockIcmpActiveDiscoveryService.createDiscovery(TEST_TENANT_ID, testNewPassiveDiscoveryUpsertDTO)).thenThrow(testException);

        //
        // Execute
        //
        target.upsertDiscovery(testNewPassiveDiscoveryUpsertDTO, mockStreamObserver);

        //
        // Verify the Results
        //
        var matcher = prepareStatusExceptionMatcher(Code.INTERNAL_VALUE, "x-test-exception-x");
        Mockito.verify(mockStreamObserver).onError(Mockito.argThat(matcher));
    }

    @Test
    void testCreateDiscoveryMissingTenant() {
        //
        // Setup Test Data and Interactions
        //
        prepareTenantLookupOnMissingTenant();
        StreamObserver<PassiveDiscoveryDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);

        //
        // Execute
        //
        target.upsertDiscovery(testNewPassiveDiscoveryUpsertDTO, mockStreamObserver);

        //
        // Verify the Results
        //
        var matcher = prepareStatusExceptionMatcher(Code.INVALID_ARGUMENT_VALUE, "Tenant Id can't be empty");
        Mockito.verify(mockStreamObserver).onError(Mockito.argThat(matcher));
    }

    @Test
    void testListAllDiscoveries() {
        //
        // Setup Test Data and Interactions
        //
        var testResultList =
            List.of(
                PassiveDiscoveryDTO.newBuilder().setName("x-passive-discovery-x").build()
            );

        prepareCommonTenantLookup();
        StreamObserver<PassiveDiscoveryListDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);
        Mockito.when(mockIcmpActiveDiscoveryService.getPassiveDiscoveries(TEST_TENANT_ID)).thenReturn(testResultList);

        //
        // Execute
        //
        target.listAllDiscoveries(Empty.getDefaultInstance(), mockStreamObserver);

        //
        // Verify the Results
        //
        Mockito.verify(mockStreamObserver).onNext(
            Mockito.argThat(
                (argument) -> Objects.equals(testResultList, argument.getDiscoveriesList())
            )
        );
        Mockito.verify(mockStreamObserver).onCompleted();
    }

    @Test
    void testListAllDiscoveriesException() {
        //
        // Setup Test Data and Interactions
        //
        var testException = new RuntimeException("x-test-exception-x");
        prepareCommonTenantLookup();
        StreamObserver<PassiveDiscoveryListDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);
        Mockito.when(mockIcmpActiveDiscoveryService.getPassiveDiscoveries(TEST_TENANT_ID)).thenThrow(testException);

        //
        // Execute
        //
        target.listAllDiscoveries(Empty.getDefaultInstance(), mockStreamObserver);

        //
        // Verify the Results
        //
        var matcher = prepareStatusExceptionMatcher(Code.INTERNAL_VALUE, "x-test-exception-x");
        Mockito.verify(mockStreamObserver).onError(Mockito.argThat(matcher));
    }

    @Test
    void testListAllDiscoveriesMissingTenant() {
        //
        // Setup Test Data and Interactions
        //
        prepareTenantLookupOnMissingTenant();
        StreamObserver<PassiveDiscoveryListDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);

        //
        // Execute
        //
        target.listAllDiscoveries(Empty.getDefaultInstance(), mockStreamObserver);

        //
        // Verify the Results
        //
        var matcher = prepareStatusExceptionMatcher(Code.INVALID_ARGUMENT_VALUE, "Tenant Id can't be empty");
        Mockito.verify(mockStreamObserver).onError(Mockito.argThat(matcher));
    }

    @Test
    void testToggleDiscovery() {
        //
        // Setup Test Data and Interactions
        //
        var testResult = PassiveDiscoveryDTO.newBuilder().setName("x-passive-discovery-x").build();

        prepareCommonTenantLookup();
        StreamObserver<PassiveDiscoveryDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);
        Mockito.when(mockIcmpActiveDiscoveryService.toggleDiscovery(TEST_TENANT_ID, testPassiveDiscoveryToggleDTO)).thenReturn(testResult);

        //
        // Execute
        //
        target.toggleDiscovery(testPassiveDiscoveryToggleDTO, mockStreamObserver);

        //
        // Verify the Results
        //
        Mockito.verify(mockStreamObserver).onNext(testResult);
        Mockito.verify(mockStreamObserver).onCompleted();
    }

    @Test
    void testToggleDiscoveryException() {
        //
        // Setup Test Data and Interactions
        //
        var testException = new RuntimeException("x-test-exception-x");
        prepareCommonTenantLookup();
        StreamObserver<PassiveDiscoveryDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);
        Mockito.when(mockIcmpActiveDiscoveryService.toggleDiscovery(TEST_TENANT_ID, testPassiveDiscoveryToggleDTO)).thenThrow(testException);

        //
        // Execute
        //
        target.toggleDiscovery(testPassiveDiscoveryToggleDTO, mockStreamObserver);

        //
        // Verify the Results
        //
        var matcher = prepareStatusExceptionMatcher(Code.INTERNAL_VALUE, "x-test-exception-x");
        Mockito.verify(mockStreamObserver).onError(Mockito.argThat(matcher));
    }

    @Test
    void testToggleDiscoveryMissingTenant() {
        //
        // Setup Test Data and Interactions
        //
        prepareTenantLookupOnMissingTenant();
        StreamObserver<PassiveDiscoveryDTO> mockStreamObserver = Mockito.mock(StreamObserver.class);

        //
        // Execute
        //
        target.toggleDiscovery(testPassiveDiscoveryToggleDTO, mockStreamObserver);

        //
        // Verify the Results
        //
        var matcher = prepareStatusExceptionMatcher(Code.INVALID_ARGUMENT_VALUE, "Tenant Id can't be empty");
        Mockito.verify(mockStreamObserver).onError(Mockito.argThat(matcher));
    }

    @Test
    void testDeleteDiscovery() {
        //
        // Setup Test Data and Interactions
        //
        prepareCommonTenantLookup();
        StreamObserver<BoolValue> mockStreamObserver = Mockito.mock(StreamObserver.class);

        //
        // Execute
        //
        target.deleteDiscovery(Int64Value.of(2323), mockStreamObserver);

        //
        // Verify the Results
        //
        Mockito.verify(mockIcmpActiveDiscoveryService).deleteDiscovery(TEST_TENANT_ID, 2323L);
        Mockito.verify(mockStreamObserver).onNext(BoolValue.of(true));
        Mockito.verify(mockStreamObserver).onCompleted();
    }

    @Test
    void testDeleteDiscoveryException() {
        //
        // Setup Test Data and Interactions
        //
        var testException = new RuntimeException("x-test-exception-x");
        prepareCommonTenantLookup();
        StreamObserver<BoolValue> mockStreamObserver = Mockito.mock(StreamObserver.class);
        Mockito.doThrow(testException).when(mockIcmpActiveDiscoveryService).deleteDiscovery(TEST_TENANT_ID, 2323);

        //
        // Execute
        //
        target.deleteDiscovery(Int64Value.of(2323), mockStreamObserver);

        //
        // Verify the Results
        //
        var matcher = prepareStatusExceptionMatcher(Code.INTERNAL_VALUE, "x-test-exception-x");
        Mockito.verify(mockStreamObserver).onError(Mockito.argThat(matcher));
    }

    @Test
    void testDeleteDiscoveryMissingTenant() {
        //
        // Setup Test Data and Interactions
        //
        prepareTenantLookupOnMissingTenant();
        StreamObserver<BoolValue> mockStreamObserver = Mockito.mock(StreamObserver.class);

        //
        // Execute
        //
        target.deleteDiscovery(Int64Value.of(2323), mockStreamObserver);

        //
        // Verify the Results
        //
        var matcher = prepareStatusExceptionMatcher(Code.INVALID_ARGUMENT_VALUE, "Tenant Id can't be empty");
        Mockito.verify(mockStreamObserver).onError(Mockito.argThat(matcher));
    }

//========================================
// Internals
//----------------------------------------

    private void prepareCommonTenantLookup() {
        Mockito.when(mockTenantLookup.lookupTenantId(Mockito.any(Context.class))).thenReturn(Optional.of(TEST_TENANT_ID));
    }

    private void prepareTenantLookupOnMissingTenant() {
        Mockito.when(mockTenantLookup.lookupTenantId(Mockito.any(Context.class))).thenReturn(Optional.empty());
    }

    private ArgumentMatcher<Exception> prepareStatusExceptionMatcher(int expectedCode, String expectedMessage) {
        return argument ->
            (
                (argument instanceof StatusRuntimeException) &&
                (((StatusRuntimeException) argument).getStatus().getCode().value() == expectedCode)  &&
                argument.getMessage().contains(expectedMessage)
            );
    }
}

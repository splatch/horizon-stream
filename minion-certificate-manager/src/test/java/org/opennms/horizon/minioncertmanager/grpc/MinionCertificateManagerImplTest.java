package org.opennms.horizon.minioncertmanager.grpc;

import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.stub.MetadataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.common.VerificationException;
import org.mockito.Mock;
import org.opennms.horizon.minioncertmanager.certificate.CertFileUtils;
import org.opennms.horizon.minioncertmanager.certificate.PKCS8Generator;
import org.opennms.horizon.minioncertmanager.proto.GetMinionCertificateRequest;
import org.opennms.horizon.minioncertmanager.proto.MinionCertificateManagerGrpc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class MinionCertificateManagerImplTest extends AbstractGrpcUnitTest {
    private MinionCertificateManagerGrpc.MinionCertificateManagerBlockingStub stub;
    private ManagedChannel channel;

    @Mock
    private File mockDirectory;

    @BeforeEach
    public void prepareTest() throws VerificationException, IOException {
        MinionCertificateManagerImpl grpcService = new MinionCertificateManagerImpl(new PKCS8Generator(), new CertFileUtils());
        startServer(grpcService);
        channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        stub = MinionCertificateManagerGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void afterTest() throws InterruptedException {
        File directory = new File(System.getProperty("user.dir"), MinionCertificateManagerImpl.HORIZON_STREAM_CERTIFICATES);
        if(directory.exists()) {
            Arrays.stream(directory.listFiles()).forEach(File::delete);
            directory.delete();
        }
        assertFalse(directory.exists());
        verifyNoMoreInteractions(spyInterceptor);
        reset(spyInterceptor);
        channel.shutdownNow();
        channel.awaitTermination(10, TimeUnit.SECONDS);
        stopServer();
    }

    @Test
    void testGetMinionCert() throws VerificationException {
        var result = stub.withInterceptors(MetadataUtils.newAttachHeadersInterceptor(createHeaders()))
            .getMinionCert(GetMinionCertificateRequest.newBuilder().setLocation("location").setTenantId("tenantId").build());
        assertThat(result.getCertificate()).isNotNull();
        assertThat(result.getPassword()).isNotEmpty();
        File directory = new File(System.getProperty("user.dir"), MinionCertificateManagerImpl.HORIZON_STREAM_CERTIFICATES);
        assertTrue(directory.exists());
        assertThat(directory.listFiles()).hasSize(1);
        for(File file:directory.listFiles()) {
            assertTrue(file.exists());
            assertThat(file.getName()).isEqualTo("minioncert.zip");
        }
        verify(spyInterceptor).verifyAccessToken(authHeader);
        verify(spyInterceptor).interceptCall(any(ServerCall.class), any(Metadata.class), any(ServerCallHandler.class));
    }
}

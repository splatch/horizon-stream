package org.opennms.horizon.minioncertmanager;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.opennms.horizon.minioncertmanager.proto.MinionCertificateManagerGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class MinionCertificateManagerGrpcClientUtils {
    private static final Logger LOG = LoggerFactory.getLogger(MinionCertificateManagerGrpcClientUtils.class);

    private static final int DEADLINE_DURATION = 30000;
    private static final String LOCALHOST = "localhost";

    private final DynamicTenantIdInterceptor dynamicTenantIdInterceptor = new DynamicTenantIdInterceptor(
        // Pull private key directly from container
        CucumberRunnerIT.testContainerRunnerClassRule.getJwtKeyPair());
    private MinionCertificateManagerGrpc.MinionCertificateManagerBlockingStub minionCertificateManagerBlockingStub;
    private ManagedChannel managedChannel;

    public MinionCertificateManagerGrpcClientUtils() {
        initStubs();
    }

    private void initStubs() {
        NettyChannelBuilder channelBuilder =
            NettyChannelBuilder.forAddress(LOCALHOST,
                // Pull gRPC server port directly from container
                CucumberRunnerIT.testContainerRunnerClassRule.getGrpcPort());

        managedChannel = channelBuilder.usePlaintext().build();
        managedChannel.getState(true);
        minionCertificateManagerBlockingStub = MinionCertificateManagerGrpc.newBlockingStub(managedChannel)
            .withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS)
            .withInterceptors(dynamicTenantIdInterceptor);
    }

    public void setTenantId(String tenantId) {
        dynamicTenantIdInterceptor.setTenantId(tenantId);
    }

    public MinionCertificateManagerGrpc.MinionCertificateManagerBlockingStub getAlertServiceStub() {
        return minionCertificateManagerBlockingStub;
    }

    public void cleanup() {
        if (managedChannel != null) {
            managedChannel.shutdownNow();
            try {
                managedChannel.awaitTermination(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException interruptedException) {
                LOG.debug("channel shutdown failed", interruptedException);
            }
        }
    }
}

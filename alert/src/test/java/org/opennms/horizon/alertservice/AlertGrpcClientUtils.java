package org.opennms.horizon.alertservice;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.opennms.horizon.alerts.proto.AlertServiceGrpc;
import org.opennms.horizon.alertservice.grpc.MonitorPolicyGrpc;
import org.opennms.horizon.shared.alert.policy.MonitorPolicyServiceGrpc;

import java.util.concurrent.TimeUnit;

public class AlertGrpcClientUtils {
    private static final int DEADLINE_DURATION = 30;
    private static final String LOCALHOST = "localhost";

    private final DynamicTenantIdInterceptor dynamicTenantIdInterceptor = new DynamicTenantIdInterceptor(
        // Pull private key directly from container
        CucumberRunnerIT.testContainerRunnerClassRule.getJwtKeyPair());
    private AlertServiceGrpc.AlertServiceBlockingStub alertServiceStub;
    private MonitorPolicyServiceGrpc.MonitorPolicyServiceBlockingStub policyStub;

    public AlertGrpcClientUtils() {
        initStubs();
    }

    private void initStubs () {
        NettyChannelBuilder channelBuilder =
            NettyChannelBuilder.forAddress(LOCALHOST,
                // Pull gRPC server port directly from container
                CucumberRunnerIT.testContainerRunnerClassRule.getGrpcPort());

        ManagedChannel managedChannel = channelBuilder.usePlaintext().build();
        managedChannel.getState(true);
        alertServiceStub = AlertServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(dynamicTenantIdInterceptor)
            .withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
        policyStub = MonitorPolicyServiceGrpc.newBlockingStub(managedChannel)
            .withInterceptors(dynamicTenantIdInterceptor)
            .withDeadlineAfter(DEADLINE_DURATION, TimeUnit.SECONDS);
    }

    public void setTenantId(String tenantId) {
        dynamicTenantIdInterceptor.setTenantId(tenantId);
    }

    public AlertServiceGrpc.AlertServiceBlockingStub getAlertServiceStub() {
        return alertServiceStub;
    }

    public MonitorPolicyServiceGrpc.MonitorPolicyServiceBlockingStub getPolicyStub() {
        return policyStub;
    }
}

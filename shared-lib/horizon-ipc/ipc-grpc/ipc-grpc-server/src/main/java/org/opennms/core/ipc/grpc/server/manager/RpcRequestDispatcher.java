package org.opennms.core.ipc.grpc.server.manager;

import java.util.concurrent.CompletableFuture;
import org.opennms.cloud.grpc.minion.RpcRequestProto;
import org.opennms.cloud.grpc.minion.RpcResponseProto;

/**
 * High level interface for dispatching requests.
 *
 * Implementer of this type might decide to validate request and received response after execution of rpc operation.
 * However, it is not determined by interface contract.
 */
public interface RpcRequestDispatcher {

    CompletableFuture<RpcResponseProto> dispatch(String location, RpcRequestProto request);
    CompletableFuture<RpcResponseProto> dispatch(String location, String systemId, RpcRequestProto request);

}

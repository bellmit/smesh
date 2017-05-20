package io.smesh.cluster;

import io.grpc.Server;

public interface GrpcServerFactory {

    Server create(ClusterConfig config);
}

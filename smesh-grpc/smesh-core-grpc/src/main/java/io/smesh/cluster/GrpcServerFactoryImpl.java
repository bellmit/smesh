package io.smesh.cluster;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServerFactoryImpl implements GrpcServerFactory {

    @Override
    public Server create(ClusterConfig config) {
        return ServerBuilder
                //TODO
                .forPort(1337)
                .build();
    }
}

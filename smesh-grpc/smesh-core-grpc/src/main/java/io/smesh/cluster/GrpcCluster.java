package io.smesh.cluster;

import io.grpc.Server;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

public class GrpcCluster extends AbstractCluster {

    // Grpc server may be set via constructor directly, in which case we don't touch...
    // when not set then the factory must be set and a grpc server will be create during startup
    private final boolean grpcServerExternallyManaged;
    private Server grpcServer;
    private GrpcServerFactory grpcServerFactory;

    public GrpcCluster(ClusterConfig config, Server grpcServer) {
        super(config);
        this.grpcServer = grpcServer;
        this.grpcServerExternallyManaged = true;
    }

    public GrpcCluster(ClusterConfig config, GrpcServerFactory grpcServerFactory) {
        super(config);
        this.grpcServerFactory = grpcServerFactory;
        this.grpcServerExternallyManaged = false;
    }

    @Override
    protected ClusterMember doStart() {
        if (!grpcServerExternallyManaged) {
            this.grpcServer = grpcServerFactory.create(getConfig());
            try {
                grpcServer.start();
            } catch (IOException e) {
                // TODO smesh exception
                throw new UncheckedIOException(e);
            }
        }
        return newLocalClusterMember(getConfig());
    }

    protected ClusterMember newLocalClusterMember(ClusterConfig config) {
        return new GrpcClusterMember(config.getLocalMemberName(),
                UUID.randomUUID().toString(),
                config.getLocalMemberRole(),
                true);
    }

    @Override
    protected void doStop() {
        if (!grpcServerExternallyManaged) {
            this.grpcServer.shutdown();
            this.grpcServer = null;
        }
    }
}

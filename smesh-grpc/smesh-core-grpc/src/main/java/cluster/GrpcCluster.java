package cluster;

import io.grpc.Server;
import io.smesh.cluster.AbstractCluster;
import io.smesh.cluster.ClusterConfig;
import io.smesh.cluster.ClusterMember;

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
        }
        // TODO:
        return null;
    }

    @Override
    protected void doStop() {
        if (!grpcServerExternallyManaged) {
            this.grpcServer.shutdown();
            this.grpcServer = null;
        }
    }
}

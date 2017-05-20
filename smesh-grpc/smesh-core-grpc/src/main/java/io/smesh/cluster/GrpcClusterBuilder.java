package io.smesh.cluster;

import io.grpc.Server;

public class GrpcClusterBuilder extends AbstractClusterBuilder<GrpcCluster, GrpcClusterBuilder> {

    private GrpcServerFactory grpcServerFactory;
    private Server grpcServer;

    public GrpcClusterBuilder withGrpcServer(Server grpcServer) {
        if (grpcServerFactory != null) {
            throw new IllegalArgumentException("grpcServerFactory already set");
        }
        this.grpcServer = grpcServer;
        return this;
    }

    public GrpcClusterBuilder withGrpcServerFactory(GrpcServerFactory grpcServerFactory) {
        if (grpcServer != null) {
            throw new IllegalArgumentException("grpcServer already set");
        }
        this.grpcServerFactory = grpcServerFactory;
        return this;
    }

    @Override
    protected GrpcCluster doBuild() {
        initGrpcInstanceFactory();
        if (grpcServer == null) {
            return new GrpcCluster(config, grpcServerFactory);
        }
        return new GrpcCluster(config, grpcServer);
    }

    protected void initGrpcInstanceFactory() {
        if (grpcServer == null && grpcServerFactory == null) {
            this.grpcServerFactory = new GrpcServerFactoryImpl();
        }
    }
}

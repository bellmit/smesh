package cluster;

import io.grpc.Server;
import io.smesh.cluster.ClusterConfig;

public interface GrpcServerFactory {

    Server create(ClusterConfig config);
}

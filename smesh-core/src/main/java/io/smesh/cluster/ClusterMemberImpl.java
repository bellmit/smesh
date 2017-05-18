package io.smesh.cluster;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ClusterMemberImpl implements ClusterMember {
    private final String name;
    private final String uuid;
    private final boolean local;


    public ClusterMemberImpl(String name, String uuid, boolean local) {
        this.name = name;
        this.uuid = uuid;
        this.local = local;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isLocal() {
        return local;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ClusterMember)) {
            return false;
        }

        if (isLocal()) {
            return super.equals(obj);
        }

        ClusterMember other = (ClusterMember) obj;
        EqualsBuilder builder = new EqualsBuilder() //
                .append(getName(), other.getName());
        builder.append(getUuid(), other.getUuid());
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        if (isLocal()) {
            return super.hashCode();
        }

        HashCodeBuilder builder = new HashCodeBuilder().append(getName()).append(getUuid());
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        return getName();
    }
}

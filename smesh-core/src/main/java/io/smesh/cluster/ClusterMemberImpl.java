package io.smesh.cluster;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

public class ClusterMemberImpl implements ClusterMember {
    private final String name;
    private final String id;
    private final Role role;
    private final boolean local;


    public ClusterMemberImpl(String name, String id, Role role, boolean local) {
        this.name = Objects.requireNonNull(name, "name is required");
        this.id = Objects.requireNonNull(id, "id is required");
        this.local = Objects.requireNonNull(local, "local is required");
        this.role = Objects.requireNonNull(role, "role is required");
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isLocal() {
        return local;
    }

    @Override
    public Role getRole() {
        return role;
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
        builder.append(getId(), other.getId());
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        if (isLocal()) {
            return super.hashCode();
        }

        HashCodeBuilder builder = new HashCodeBuilder().append(getName()).append(getId());
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        return getName();
    } // TODO: implement
}

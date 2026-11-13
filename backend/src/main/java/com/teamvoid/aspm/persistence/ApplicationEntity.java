
package com.teamvoid.aspm.persistence;

import com.teamvoid.aspm.domain.AssetStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "applications")
public class ApplicationEntity {

    @Id
    private UUID id;

    private String name;

    @Column(name = "repository_url")
    private String repositoryUrl;

    @Enumerated(EnumType.STRING)
    private AssetStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    public ApplicationEntity() {
    }

    public ApplicationEntity(UUID id, String name, String repositoryUrl,
                             AssetStatus status, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.repositoryUrl = repositoryUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

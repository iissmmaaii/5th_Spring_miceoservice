package com.cyphervault.iam.auth.infrastructure.persistence.entity;

import com.cyphervault.iam.auth.domain.enums.KeyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "user_public_keys",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_public_keys_fingerprint", columnNames = "fingerprint")
        },
        indexes = {
                @Index(name = "idx_public_keys_user_id", columnList = "user_id"),
                @Index(name = "idx_public_keys_device_id", columnList = "device_id"),
                @Index(name = "idx_public_keys_status", columnList = "status"),
                @Index(name = "idx_public_keys_user_status", columnList = "user_id,status"),
                @Index(name = "idx_public_keys_user_device_status", columnList = "user_id,device_id,status")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicKeyJpaEntity {

    @Id
    @Column(name = "key_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID keyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_public_keys_user")
    )
    private UserAccountJpaEntity user;

    @Column(name = "device_id", nullable = false, length = 140)
    private String deviceId;

    @Column(name = "public_key_pem", nullable = false, columnDefinition = "TEXT")
    private String publicKeyPem;

    @Column(name = "fingerprint", nullable = false, length = 128)
    private String fingerprint;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private KeyStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;
}
package com.cyphervault.iam.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "auth_challenges",
        indexes = {
                @Index(name = "idx_challenges_user_id", columnList = "user_id"),
                @Index(name = "idx_challenges_expires_at", columnList = "expires_at"),
                @Index(name = "idx_challenges_used", columnList = "used"),
                @Index(name = "idx_challenges_user_used", columnList = "user_id,used")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthChallengeJpaEntity {

    @Id
    @Column(name = "challenge_id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID challengeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_auth_challenges_user")
    )
    private UserAccountJpaEntity user;

    @Column(name = "nonce", nullable = false, updatable = false, length = 160)
    private String nonce;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "used_at")
    private Instant usedAt;
}
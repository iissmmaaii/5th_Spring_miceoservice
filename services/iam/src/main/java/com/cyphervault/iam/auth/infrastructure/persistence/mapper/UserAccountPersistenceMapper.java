package com.cyphervault.iam.auth.infrastructure.persistence.mapper;

import com.cyphervault.iam.auth.domain.model.UserAccount;
import com.cyphervault.iam.auth.infrastructure.persistence.entity.UserAccountJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserAccountPersistenceMapper {

    public UserAccountJpaEntity toEntity(UserAccount model) {
        return UserAccountJpaEntity.builder()
                .userId(model.getUserId())
                .fullName(model.getFullName())
                .email(model.getEmail())
                .status(model.getStatus())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    public UserAccount toModel(UserAccountJpaEntity entity) {
        return UserAccount.builder()
                .userId(entity.getUserId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
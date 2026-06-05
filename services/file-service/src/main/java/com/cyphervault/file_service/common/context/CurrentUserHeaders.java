package com.cyphervault.file_service.common.context;

import com.cyphervault.file_service.common.exception.BadRequestException;
import org.springframework.util.StringUtils;

import java.util.UUID;

public final class CurrentUserHeaders {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-User-Role";

    private CurrentUserHeaders() {
    }

    public static UUID requiredUserId(String userIdHeader) {
        if (!StringUtils.hasText(userIdHeader)) {
            throw new BadRequestException("Missing X-User-Id header");
        }

        try {
            return UUID.fromString(userIdHeader);
        } catch (Exception exception) {
            throw new BadRequestException("Invalid X-User-Id header");
        }
    }

    public static String requiredRole(String roleHeader) {
        if (!StringUtils.hasText(roleHeader)) {
            throw new BadRequestException("Missing X-User-Role header");
        }

        return roleHeader.trim();
    }
}
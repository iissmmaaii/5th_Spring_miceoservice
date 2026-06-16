package com.cyphervault.api_gateway.common.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class UserContextHeaderFilter extends OncePerRequestFilter {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-User-Role";
    public static final String INTERNAL_GATEWAY_SECRET_HEADER = "X-Internal-Gateway-Secret";

    private final String gatewayInternalSecret;

    public UserContextHeaderFilter(String gatewayInternalSecret) {
        this.gatewayInternalSecret = gatewayInternalSecret;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

        // Remove spoofed headers coming from client
        mutableRequest.removeHeader(USER_ID_HEADER);
        mutableRequest.removeHeader(USER_EMAIL_HEADER);
        mutableRequest.removeHeader(USER_ROLE_HEADER);
        mutableRequest.removeHeader(INTERNAL_GATEWAY_SECRET_HEADER);

        mutableRequest.removeHeader("x-user-id");
        mutableRequest.removeHeader("x-user-email");
        mutableRequest.removeHeader("x-user-role");
        mutableRequest.removeHeader("x-internal-gateway-secret");

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken
                && authentication.isAuthenticated()) {

            String userId = getUserId(jwtAuthenticationToken);
            String email = jwtAuthenticationToken.getToken().getClaimAsString("email");
            String role = jwtAuthenticationToken.getToken().getClaimAsString("role");

            if (StringUtils.hasText(userId)) {
                mutableRequest.putHeader(USER_ID_HEADER, userId);
            }

            if (StringUtils.hasText(email)) {
                mutableRequest.putHeader(USER_EMAIL_HEADER, email);
            }

            if (StringUtils.hasText(role)) {
                mutableRequest.putHeader(USER_ROLE_HEADER, role);
            }

            if (StringUtils.hasText(gatewayInternalSecret)) {
                mutableRequest.putHeader(INTERNAL_GATEWAY_SECRET_HEADER, gatewayInternalSecret);
            }
        }

        filterChain.doFilter(mutableRequest, response);
    }

    private String getUserId(JwtAuthenticationToken jwtAuthenticationToken) {
        String userId = jwtAuthenticationToken.getToken().getClaimAsString("userId");

        if (StringUtils.hasText(userId)) {
            return userId;
        }

        return jwtAuthenticationToken.getToken().getSubject();
    }
}
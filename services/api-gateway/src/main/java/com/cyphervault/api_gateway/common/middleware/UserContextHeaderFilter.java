package com.cyphervault.api_gateway.common.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class UserContextHeaderFilter extends OncePerRequestFilter {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-User-Role";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);

        mutableRequest.removeHeader(USER_ID_HEADER);
        mutableRequest.removeHeader(USER_EMAIL_HEADER);
        mutableRequest.removeHeader(USER_ROLE_HEADER);

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken
                && authentication.isAuthenticated()) {

            String userId = jwtAuthenticationToken.getToken().getSubject();
            String email = jwtAuthenticationToken.getToken().getClaimAsString("email");
            String role = jwtAuthenticationToken.getToken().getClaimAsString("role");

            if (userId != null) {
                mutableRequest.putHeader(USER_ID_HEADER, userId);
            }

            if (email != null) {
                mutableRequest.putHeader(USER_EMAIL_HEADER, email);
            }

            if (role != null) {
                mutableRequest.putHeader(USER_ROLE_HEADER, role);
            }
        }

        filterChain.doFilter(mutableRequest, response);
    }
}
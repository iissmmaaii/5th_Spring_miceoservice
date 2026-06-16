package com.cyphervault.api_gateway.common.config;

import com.cyphervault.api_gateway.common.middleware.UserContextHeaderFilter;
import com.cyphervault.api_gateway.common.security.JwtAccessDeniedHandler;
import com.cyphervault.api_gateway.common.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Value("${cyphervault.gateway.internal-secret}")
    private String gatewayInternalSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info",
                                "/error",
                                "/fallback/**",

                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",

                                "/api/auth/v3/api-docs",
                                "/api/accounts/v3/api-docs",
                                "/api/files/v3/api-docs",

                                // Chat-service Swagger from NestJS
                                "/api/chat/docs",
                                "/api/chat/docs/**",
                                "/api/chat/docs-json",
                                "/api/chat/docs-yaml",

                                "/api/auth/dev/sign",
                                "/api/auth/dev/keypair"
                        ).permitAll()

                        .requestMatchers(
                                "/api/auth/users/register",
                                "/api/auth/challenges",
                                "/api/auth/signatures/verify"
                        ).permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers("/api/accounts/**").authenticated()
                        .requestMatchers("/api/files/**").authenticated()
                        .requestMatchers("/api/chat/**").authenticated()

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )

                .addFilterAfter(
                        new UserContextHeaderFilter(gatewayInternalSecret),
                        BearerTokenAuthenticationFilter.class
                )

                .build();
    }
}
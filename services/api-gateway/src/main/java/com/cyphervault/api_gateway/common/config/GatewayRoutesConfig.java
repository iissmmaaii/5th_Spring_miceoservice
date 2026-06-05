package com.cyphervault.api_gateway.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.security.Principal;
import java.time.Duration;

import static org.springframework.cloud.gateway.server.mvc.filter.Bucket4jFilterFunctions.rateLimit;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {

        return route("iam_service_route")
                .route(path("/api/auth/**"), http())

                // 20 requests per minute per IP for auth endpoints
                .filter(rateLimit(config -> config
                        .setCapacity(20)
                        .setPeriod(Duration.ofMinutes(1))
                        .setTokens(1)
                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
                        .setHeaderName("X-RateLimit-Remaining")
                        .setKeyResolver(this::clientIpKey)
                ))

                // Load balance to iam-service instances
                .filter(lb("iam-service"))

                // Circuit breaker for IAM route
                .filter(circuitBreaker(config -> config
                        .setId("iamCircuitBreaker")
                        .setFallbackUri("forward:/fallback/iam")
                        .setStatusCodes("500", "502", "503", "504")
                ))
                .build()

                // Transfer route first because it is more sensitive than general accounts
                .and(route("account_transfer_route")
                        .route(path("/api/accounts/transfer"), http())

                        // 5 transfers per minute per user
                        .filter(rateLimit(config -> config
                                .setCapacity(5)
                                .setPeriod(Duration.ofMinutes(1))
                                .setTokens(1)
                                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
                                .setHeaderName("X-RateLimit-Remaining")
                                .setKeyResolver(this::userOrIpKey)
                        ))

                        .filter(lb("account-service"))

                        .filter(circuitBreaker(config -> config
                                .setId("accountCircuitBreaker")
                                .setFallbackUri("forward:/fallback/account")
                                .setStatusCodes("500", "502", "503", "504")
                        ))
                        .build()
                )

                // Admin file-service route
                .and(route("admin_file_service_route")
                        .route(path("/api/admin/files/**"), http())
                        .filter(lb("file-service"))
                        .build()
                )

                // User file-service route
                .and(route("file_service_route")
                        .route(path("/api/files/**"), http())
                        .filter(lb("file-service"))
                        .build()
                )

                // General account route
                .and(route("account_service_route")
                        .route(path("/api/accounts/**"), http())

                        // 60 account requests per minute per user
                        .filter(rateLimit(config -> config
                                .setCapacity(60)
                                .setPeriod(Duration.ofMinutes(1))
                                .setTokens(1)
                                .setStatusCode(HttpStatus.TOO_MANY_REQUESTS)
                                .setHeaderName("X-RateLimit-Remaining")
                                .setKeyResolver(this::userOrIpKey)
                        ))

                        .filter(lb("account-service"))

                        .filter(circuitBreaker(config -> config
                                .setId("accountCircuitBreaker")
                                .setFallbackUri("forward:/fallback/account")
                                .setStatusCodes("500", "502", "503", "504")
                        ))

                        .build()
                );
    }

    private String userOrIpKey(ServerRequest request) {
        Principal principal = request.servletRequest().getUserPrincipal();

        if (principal != null && StringUtils.hasText(principal.getName())) {
            return "user:" + principal.getName();
        }

        String userIdHeader = request.servletRequest().getHeader("X-User-Id");
        if (StringUtils.hasText(userIdHeader)) {
            return "user-header:" + userIdHeader;
        }

        return clientIpKey(request);
    }

    private String clientIpKey(ServerRequest request) {
        HttpServletRequest servletRequest = request.servletRequest();

        String forwardedFor = servletRequest.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwardedFor)) {
            return "ip:" + forwardedFor.split(",")[0].trim();
        }

        String realIp = servletRequest.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return "ip:" + realIp.trim();
        }

        return "ip:" + servletRequest.getRemoteAddr();
    }
}
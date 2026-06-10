package com.cyphervault.fabric.common.filter;

import com.cyphervault.fabric.common.logging.MdcKeys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        try {
            log.info(
                    "HTTP_REQUEST_STARTED method={} path={} correlationId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    MDC.get(MdcKeys.CORRELATION_ID)
            );

            filterChain.doFilter(request, response);

        } finally {
            long durationMs = System.currentTimeMillis() - start;

            log.info(
                    "HTTP_REQUEST_COMPLETED method={} path={} status={} durationMs={} correlationId={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    durationMs,
                    MDC.get(MdcKeys.CORRELATION_ID)
            );
        }
    }
}
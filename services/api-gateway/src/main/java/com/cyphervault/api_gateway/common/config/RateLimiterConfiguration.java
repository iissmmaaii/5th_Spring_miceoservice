package com.cyphervault.api_gateway.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.caffeine.CaffeineProxyManager;
import io.github.bucket4j.distributed.proxy.AsyncProxyManager;
import io.github.bucket4j.distributed.remote.RemoteBucketState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfiguration {

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public AsyncProxyManager<String> caffeineProxyManager() {

        Caffeine<String, RemoteBucketState> caffeineBuilder =
                (Caffeine) Caffeine.newBuilder()
                        .maximumSize(100_000)
                        .expireAfterAccess(Duration.ofHours(1));

        return new CaffeineProxyManager<>(
                caffeineBuilder,
                Duration.ofHours(1)
        ).asAsync();
    }
}
package com.cyphervault.account_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient fabricOrchestratorRestClient(
            FabricOrchestratorProperties properties
    ) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
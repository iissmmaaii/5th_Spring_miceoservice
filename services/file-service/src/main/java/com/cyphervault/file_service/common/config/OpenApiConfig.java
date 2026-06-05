package com.cyphervault.file_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cypherVaultFileOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CypherVault File Service API")
                        .version("v1")
                        .description("API documentation for CypherVault File Service"));
    }
}
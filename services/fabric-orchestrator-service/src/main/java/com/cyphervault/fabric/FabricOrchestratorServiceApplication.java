package com.cyphervault.fabric;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@ConfigurationPropertiesScan
@SpringBootApplication
public class FabricOrchestratorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FabricOrchestratorServiceApplication.class, args);
    }
}
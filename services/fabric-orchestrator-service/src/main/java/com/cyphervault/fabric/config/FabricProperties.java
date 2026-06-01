package com.cyphervault.fabric.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "fabric")
public class FabricProperties {

    @NotBlank
    private String channelName;

    @NotBlank
    private String identityChaincodeName;

    @NotBlank
    private String mspId;

    @NotBlank
    private String peerEndpoint;

    @NotBlank
    private String overrideAuthority;

    @NotBlank
    private String tlsCertPath;

    @NotBlank
    private String certPath;

    @NotBlank
    private String privateKeyDir;

    private List<String> endorsingOrgs = List.of("Org1MSP", "Org2MSP");
}
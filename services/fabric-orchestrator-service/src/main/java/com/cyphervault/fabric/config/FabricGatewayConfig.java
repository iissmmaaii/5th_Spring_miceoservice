package com.cyphervault.fabric.config;

import io.grpc.ChannelCredentials;
import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.TlsChannelCredentials;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;
import org.hyperledger.fabric.client.Network;
import org.hyperledger.fabric.client.identity.Identities;
import org.hyperledger.fabric.client.identity.Identity;
import org.hyperledger.fabric.client.identity.Signer;
import org.hyperledger.fabric.client.identity.Signers;
import org.hyperledger.fabric.client.identity.X509Identity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Configuration
public class FabricGatewayConfig {

    @Bean(destroyMethod = "shutdownNow")
    public ManagedChannel fabricManagedChannel(FabricProperties properties)
            throws IOException {

        Path tlsCertPath = Path.of(properties.getTlsCertPath());

        ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
                .trustManager(tlsCertPath.toFile())
                .build();

        return Grpc.newChannelBuilder(properties.getPeerEndpoint(), credentials)
                .overrideAuthority(properties.getOverrideAuthority())
                .build();
    }

    @Bean
    public Gateway fabricGateway(
            FabricProperties properties,
            ManagedChannel fabricManagedChannel
    ) throws Exception {

        Identity identity = newIdentity(properties);
        Signer signer = newSigner(properties);

        return Gateway.newInstance()
                .identity(identity)
                .signer(signer)
                .connection(fabricManagedChannel)
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(60, TimeUnit.SECONDS))
                .connect();
    }

    @Bean
    public Network fabricNetwork(
            FabricProperties properties,
            Gateway fabricGateway
    ) {
        return fabricGateway.getNetwork(properties.getChannelName());
    }

    @Bean
    public Contract identityRegistryContract(
            FabricProperties properties,
            Network fabricNetwork
    ) {
        return fabricNetwork.getContract(properties.getIdentityChaincodeName());
    }
    @Bean
    public Contract accountLedgerContract(
            FabricProperties properties,
            Network fabricNetwork
    ) {
        return fabricNetwork.getContract(
                properties.getIdentityChaincodeName(),
                "AccountLedger"
        );
    }
    private Identity newIdentity(FabricProperties properties)
            throws IOException, CertificateException {

        try (Reader certReader = Files.newBufferedReader(Path.of(properties.getCertPath()))) {
            X509Certificate certificate = Identities.readX509Certificate(certReader);
            return new X509Identity(properties.getMspId(), certificate);
        }
    }

    private Signer newSigner(FabricProperties properties)
            throws IOException, InvalidKeyException {

        Path privateKeyPath = findPrivateKey(Path.of(properties.getPrivateKeyDir()));

        try (Reader keyReader = Files.newBufferedReader(privateKeyPath)) {
            PrivateKey privateKey = Identities.readPrivateKey(keyReader);
            return Signers.newPrivateKeySigner(privateKey);
        }
    }

    private Path findPrivateKey(Path keyDir) throws IOException {
        try (Stream<Path> paths = Files.list(keyDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No private key found in directory: " + keyDir
                    ));
        }
    }
}
package org.kie.efesto.kafka.example.server;

import org.kie.efesto.kafka.api.utils.KafkaSPIUtils;
import org.kie.efesto.kafka.example.server.managers.ExternalRequestManager;
import org.kie.efesto.kafka.runtime.provider.service.KafkaRuntimeServiceGatewayProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainServer {

    private static final Logger logger = LoggerFactory.getLogger(MainServer.class);

    public static void main(String[] args) {
        KafkaRuntimeServiceGatewayProviderImpl runtimeServiceProvider = KafkaSPIUtils.getRuntimeServiceProviders(true)
                .stream()
                .filter(serviceProvider -> serviceProvider instanceof KafkaRuntimeServiceGatewayProviderImpl)
                .findFirst()
                .map(KafkaRuntimeServiceGatewayProviderImpl.class::cast)
                .orElseThrow(() -> new RuntimeException("Failed to find KafkaRuntimeServiceProvider"));
        logger.info("KafkaRuntimeServiceGatewayProviderImpl found {}", runtimeServiceProvider);
        runtimeServiceProvider.searchServices();
        logger.info("Start listening...");
        new ExternalRequestManager().startListening(runtimeServiceProvider);
    }

}
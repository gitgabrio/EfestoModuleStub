package org.kie.efesto.kafka.example.server.managers;

import org.kie.efesto.kafka.api.listeners.EfestoKafkaMessageListener;
import org.kie.efesto.kafka.api.messages.AbstractEfestoKafkaMessage;
import org.kie.efesto.kafka.runtime.provider.service.KafkaRuntimeServiceGatewayProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.kafka.example.server.consumer.ServerEvaluateConsumer.startEvaluateConsumer;

public class ExternalRequestManager implements EfestoKafkaMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ExternalRequestManager.class);

    public void startListening(KafkaRuntimeServiceGatewayProviderImpl runtimeServiceProvider) {
        logger.info("Start listening for evaluation requests");
        startEvaluateConsumer(this, runtimeServiceProvider);
    }

    @Override
    public void onMessageReceived(AbstractEfestoKafkaMessage received) {
        logger.info("onMessageReceived {}", received);
    }
}

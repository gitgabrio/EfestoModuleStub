/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.efesto.kafka.example.server.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.kie.efesto.kafka.api.listeners.EfestoKafkaMessageListener;
import org.kie.efesto.kafka.example.server.managers.EfestoRuntimeManager;
import org.kie.efesto.kafka.runtime.provider.messages.EfestoKafkaRuntimeEvaluateInputResponseMessage;
import org.kie.efesto.kafka.runtime.provider.service.KafkaRuntimeServiceGatewayProviderImpl;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.kie.efesto.kafka.api.KafkaConstants.BOOTSTRAP_SERVERS;
import static org.kie.efesto.kafka.api.KafkaConstants.EVALUATE_TOPIC;
import static org.kie.efesto.kafka.api.ThreadUtils.getConsumeAndListenThread;


/**
 * This receives messages from external client (i.e. not efesto)
 */
public class ServerEvaluateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ServerEvaluateConsumer.class);

    private static Thread consumerThread;

    private static Set<EfestoKafkaMessageListener> registeredListeners;

    private static KafkaRuntimeServiceGatewayProviderImpl runtimeServiceProvider;

    private ServerEvaluateConsumer() {
    }

    public static void removeListener(EfestoKafkaMessageListener toRemove) {
        logger.info("removeListener {}", toRemove);
        if (registeredListeners != null) {
            logger.info("Removing {}", toRemove);
            registeredListeners.remove(toRemove);
        }
    }

    public static void startEvaluateConsumer(EfestoKafkaMessageListener toRegister, KafkaRuntimeServiceGatewayProviderImpl runtimeServiceProvider) {
        logger.info("startEvaluateConsumer");
        ServerEvaluateConsumer.runtimeServiceProvider = runtimeServiceProvider;
        if (consumerThread != null) {
            logger.info("EvaluateInputResponseConsumer already started");
            registeredListeners.add(toRegister);
        } else {
            logger.info("Starting EvaluateInputResponseConsumer....");
            Consumer<Long, JsonNode> consumer = createConsumer();
            registeredListeners = new HashSet<>();
            registeredListeners.add(toRegister);
            startEvaluateConsumer(consumer, registeredListeners);
        }
    }

    public static void startEvaluateConsumer(Consumer<Long, JsonNode> consumer,
                                             Collection<EfestoKafkaMessageListener> listeners) {
        logger.info("startEvaluateConsumer....");
        final int giveUp = 100;
        try {
            consumerThread = getConsumeAndListenThread(consumer, giveUp, ServerEvaluateConsumer.class.getSimpleName(),
                    ServerEvaluateConsumer::consumeModel,
                    listeners);
            consumerThread.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    static EfestoKafkaRuntimeEvaluateInputResponseMessage consumeModel(ConsumerRecord<Long, JsonNode> toConsume) {
        try {
            //runtimeServiceProvider.searchServices();
            logger.info("Consume: ({})\n", toConsume);
            JsonNode jsonNode = toConsume.value();
            logger.info("JsonNode: ({})\n", jsonNode);
            String modelLocalUriIdString = jsonNode.get("modelLocalUriIdString").asText();
            modelLocalUriIdString = URLDecoder.decode(modelLocalUriIdString, StandardCharsets.UTF_8);
            String inputDataString = jsonNode.get("inputData").toString();
//            try {
//                Map<String, Object> inputDataMap  = JSONUtils.getInputData((String) inputData);
//                inputData = new SerializableHashMap<>();
//                ((SerializableMap)inputData).putAll(inputDataMap);
//            } catch (Exception e) {
//                logger.warn("Failed to deserialize ");
//            }
            logger.info("modelLocalUriIdString: ({})\n", modelLocalUriIdString);
            logger.info("inputDataString: ({})\n", inputDataString);
            EfestoOutput toPublish = EfestoRuntimeManager.evaluateModel(modelLocalUriIdString, inputDataString);
            logger.info("*******************************");
            logger.info("*******************************");
            logger.info("EfestoOutput: ({})\n", toPublish);
            logger.info("*******************************");
            logger.info("*******************************");
            return new EfestoKafkaRuntimeEvaluateInputResponseMessage(toPublish, -0L);
        } catch (Exception e) {
            logger.error("Failed to retrieve EfestoOutput", e);
            return null;
        }
    }

    private static Consumer<Long, JsonNode> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                ServerEvaluateConsumer.class.getSimpleName());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class.getName());

        // Create the consumer using props.
        final Consumer<Long, JsonNode> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(EVALUATE_TOPIC));
        return consumer;
    }
}

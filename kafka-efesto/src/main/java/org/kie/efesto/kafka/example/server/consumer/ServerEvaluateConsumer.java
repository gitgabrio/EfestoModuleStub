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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.kafka.example.KafkaConstants.BOOTSTRAP_SERVERS;
import static org.kie.efesto.kafka.example.KafkaConstants.EVALUATE_TOPIC;
import static org.kie.efesto.kafka.example.serialization.JSONUtil.objectMapper;
import static org.kie.efesto.kafka.example.server.producer.ServerEvaluatedProducer.runProducer;

public class ServerEvaluateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ServerEvaluateConsumer.class);

    private ServerEvaluateConsumer() {
    }

    public static void startEvaluateConsumer() throws InterruptedException {
        logger.info("starting consumer....");
        final Consumer<Long, JsonNode> consumer = createConsumer();

        final int giveUp = 100;
        final AtomicInteger noRecordsCount = new AtomicInteger(0);
        Thread thread = new Thread("ServerEvaluateConsumer") {
            @Override
            public void run() {
                while (true) {
                    final ConsumerRecords<Long, JsonNode> consumerRecords =
                            consumer.poll(1000);

                    if (consumerRecords.count() == 0) {
                        int currentNoRecordsCount = noRecordsCount.addAndGet(1);
                        if (currentNoRecordsCount > giveUp) {
                            break;
                        } else {
                            continue;
                        }
                    }
                    consumerRecords.forEach(record -> {
                        logger.info("Consumer Record:({}, {}, {}, {})\n",
                                    record.key(), record.value(),
                                    record.partition(), record.offset());
                        try {
                            consumeModel(record);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    consumer.commitAsync();
                }
                consumer.close();
                logger.info("DONE");
            }
        };
        thread.start();
    }

    static void consumeModel(ConsumerRecord<Long, JsonNode> toConsume) throws JsonProcessingException {
        logger.info("Consume: ({})\n", toConsume);
        JsonNode jsonNode = toConsume.value();
        logger.info("JsonNode: ({})\n", jsonNode);
        Map<String, Object> inputData = getInputData(jsonNode);
        logger.info("inputData: ({})\n", inputData);
        EfestoOutput retrieved = EfestoRuntimeManager.evaluateModel(jsonNode, inputData);
        logger.info("EfestoOutput: ({})\n", retrieved);
        runProducer(toConsume.key(), retrieved);
    }

    static Map<String, Object> getInputData(JsonNode jsonNode) {
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<>() {
        };
        try {
            String inputDataString = jsonNode.get("inputData").toString();
            return objectMapper.readValue(inputDataString, typeRef);
        } catch (Exception e) {
            throw new KieEfestoCommonException("Failed to retrieve inputData");
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
        final Consumer<Long, JsonNode> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(EVALUATE_TOPIC));
        return consumer;
    }
}

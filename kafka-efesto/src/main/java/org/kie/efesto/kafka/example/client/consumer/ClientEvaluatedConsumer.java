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
package org.kie.efesto.kafka.example.client.consumer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.pmml.api.identifiers.AbstractModelLocalUriIdPmml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.kafka.example.KafkaConstants.BOOTSTRAP_SERVERS;
import static org.kie.efesto.kafka.example.KafkaConstants.EVALUATED_TOPIC;
import static org.kie.efesto.kafka.example.client.producer.ClientEvaluateProducer.runProducer;
import static org.kie.efesto.kafka.example.serialization.JSONUtil.objectMapper;

public class ClientEvaluatedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ClientEvaluatedConsumer.class);

    private ClientEvaluatedConsumer() {
    }

    public static void startEvaluatedConsumer() throws InterruptedException {
        logger.info("starting consumer....");
        final Consumer<Long, JsonNode> consumer = createConsumer();

        final int giveUp = 100;
        final AtomicInteger noRecordsCount = new AtomicInteger(0);
        Thread thread = new Thread("ClientCompiledConsumer") {
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
                        consumeModel(record);
                    });

                    consumer.commitAsync();
                }
                consumer.close();
                logger.info("DONE");
            }
        };
        thread.start();
    }

    static void consumeModel(ConsumerRecord<Long, JsonNode> toConsume) {
        logger.info("Consume: ({})\n", toConsume);

        try {
            String efestoOutputString = toConsume.value().get("efestoOutput").asText();
            EfestoOutput retrieved = objectMapper.readValue(efestoOutputString, EfestoOutput.class);
            logger.info("EfestoOutput: ({})\n", retrieved);
            logger.info("ModelLocalUriId: ({})\n", retrieved.getModelLocalUriId());
            logger.info("OutputData: ({})\n", retrieved.getOutputData());
        } catch (Exception e) {
            logger.error("Failed to retrieve ModelLocalUriId", e);
        }
    }

    private static Consumer<Long, JsonNode> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                  BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                  ClientEvaluatedConsumer.class.getSimpleName());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                  LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                  JsonDeserializer.class.getName());

        // Create the consumer using props.
        final Consumer<Long, JsonNode> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(EVALUATED_TOPIC));
        return consumer;
    }
}

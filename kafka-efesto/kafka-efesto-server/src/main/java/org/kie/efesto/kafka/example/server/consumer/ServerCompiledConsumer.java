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

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.connect.json.JsonDeserializer;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.kafka.example.server.storage.ContextStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.kafka.example.KafkaConstants.ASK_COMPILED_TOPIC;
import static org.kie.efesto.kafka.example.KafkaConstants.BOOTSTRAP_SERVERS;
import static org.kie.efesto.kafka.example.ThreadUtils.getConsumerThread;
import static org.kie.efesto.kafka.example.server.producer.ServerAlreadyCompiledProducer.runAlreadyCompiledProducer;

public class ServerCompiledConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ServerCompiledConsumer.class);

    private ServerCompiledConsumer() {
    }

    public static void startCompiledConsumer() {
        logger.info("starting consumer....");
        final int giveUp = 100;
        Consumer<Long, JsonNode> consumer = createConsumer();
        try {
            Thread thread = getConsumerThread(consumer, giveUp, "ServerCompiledConsumer",
                                              ServerCompiledConsumer::consumeModel);
            thread.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    static void consumeModel(ConsumerRecord<Long, JsonNode> toConsume) {
        logger.info("Consume: ({})\n", toConsume);
        Collection<ModelLocalUriId> retrieved = ContextStorage.getAllModelLocalUriId();
        logger.info("ModelLocalUriIds: ({})\n", retrieved);
        try {
            runAlreadyCompiledProducer(toConsume.key(), retrieved);
        } catch (Exception e) {
            logger.error("Failed to send ModelLocalUriIds", e);
        }
    }

    private static Consumer<Long, JsonNode> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                  BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,
                  ServerCompiledConsumer.class.getSimpleName());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                  LongDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                  JsonDeserializer.class.getName());

        // Create the consumer using props.
        final Consumer<Long, JsonNode> consumer =
                new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(ASK_COMPILED_TOPIC));
        return consumer;
    }
}

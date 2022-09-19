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
package org.kie.efesto.kafka.example.server.producer;

import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.connect.json.JsonSerializer;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.kafka.example.KafkaConstants.BOOTSTRAP_SERVERS;
import static org.kie.efesto.kafka.example.KafkaConstants.COMPILED_TOPIC;
import static org.kie.efesto.kafka.example.server.serialization.JSONUtil.objectMapper;

public class ServerCompiledProducer {


    private static final Logger logger = LoggerFactory.getLogger(ServerCompiledProducer.class);

    public static void runProducer(long id, ModelLocalUriId toSend) {
        logger.info("Produce: ({} {})\n", id, toSend);
        final Producer<Long, JsonNode> producer = createProducer();
        long time = System.currentTimeMillis();

        try {
            JsonNode jsonNode = getJsonNode(toSend);
            final ProducerRecord<Long, JsonNode> record =
                    new ProducerRecord<>(COMPILED_TOPIC, id, jsonNode);

            RecordMetadata metadata = producer.send(record).get();

            long elapsedTime = System.currentTimeMillis() - time;
            logger.info("sent record(key={} value={}) " +
                                "meta(partition={}, offset={}) time={}\n",
                        record.key(), record.value(), metadata.partition(),
                        metadata.offset(), elapsedTime);
        } catch (Exception e) {
            throw new KieEfestoCommonException(e);
        } finally {
            producer.flush();
            producer.close();
        }
    }

    private static JsonNode getJsonNode(ModelLocalUriId toSend) throws JsonProcessingException {
        String modelLocalUriIdString = objectMapper.writeValueAsString(toSend);
        ObjectNode toReturn = new JsonNodeFactory(false).objectNode();
        toReturn.set("modelLocalUriIdString", new TextNode(modelLocalUriIdString));
        return toReturn;
    }

    private static Producer<Long, JsonNode> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                  BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "EfestoCompilationProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                  LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                  JsonSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
}

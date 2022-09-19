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
package org.kie.efesto.kafka.example.client.consumer

import com.fasterxml.jackson.databind.JsonNode
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.connect.json.JsonDeserializer
import org.kie.efesto.kafka.example.KafkaConstants
import org.kie.efesto.kafka.example.ThreadUtils
import org.kie.efesto.kafka.example.client.model.RetrievedOutput
import org.kie.efesto.kafka.example.client.serialization.JSONUtil
import org.kie.efesto.kafka.example.client.storage.ClientStorage
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

object ClientEvaluatedConsumer {
    private val logger = LoggerFactory.getLogger(ClientEvaluatedConsumer::class.java)
    @JvmStatic
    @Throws(InterruptedException::class)
    fun startEvaluatedConsumer() {
        logger.info("starting consumer....")
        val giveUp = 100
        val consumer = createConsumer()
        try {
            val thread: Thread = ThreadUtils.getConsumerThread(
                consumer,
                giveUp,
                "ClientEvaluatedConsumer"
            ) { consumeModel(it) }
            thread.start()
        } catch (e: Exception) {
            logger.error(e.message, e)
        }
    }

    private fun consumeModel(toConsume: ConsumerRecord<Long, JsonNode>) {
        logger.info("Consume: ({})\n", toConsume)
        try {
            val efestoOutputString = toConsume.value()["efestoOutput"].asText()
            val retrieved = JSONUtil.objectMapper.readValue(efestoOutputString, RetrievedOutput::class.java)
            logger.info("RetrievedOutput: ({})\n", retrieved)
            logger.info("ExecutableId: ({})\n", retrieved.executableId)
            logger.info("OutputData: ({})\n", retrieved.outputData)
            ClientStorage.putRetrievedOutput(toConsume.key(), retrieved)
        } catch (e: Exception) {
            logger.error("Failed to retrieve ModelLocalUriId", e)
        }
    }

    private fun createConsumer(): org.apache.kafka.clients.consumer.Consumer<Long, JsonNode> {
        val props = Properties()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = KafkaConstants.BOOTSTRAP_SERVERS
        props[ConsumerConfig.GROUP_ID_CONFIG] = ClientEvaluatedConsumer::class.java.simpleName
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] =
            LongDeserializer::class.java.name
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] =
            JsonDeserializer::class.java.name

        // Create the consumer using props.
        val consumer: org.apache.kafka.clients.consumer.Consumer<Long, JsonNode> = KafkaConsumer(props)

        // Subscribe to the topic.
        consumer.subscribe(listOf(KafkaConstants.EVALUATED_TOPIC))
        return consumer
    }
}
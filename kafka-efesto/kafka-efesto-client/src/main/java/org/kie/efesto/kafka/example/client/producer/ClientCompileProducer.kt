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
package org.kie.efesto.kafka.example.client.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.kie.efesto.kafka.example.KafkaConstants
import org.slf4j.LoggerFactory
import java.util.*

object ClientCompileProducer {
    private val logger = LoggerFactory.getLogger(ClientCompileProducer::class.java)
    fun runCompileProducer(id: Long, toSend: String) {
        logger.info("Produce: ({} {})\n", id, toSend)
        val producer = createProducer()
        val time = System.currentTimeMillis()
        try {
            val record = ProducerRecord(KafkaConstants.COMPILE_TOPIC, id, toSend)
            val metadata = producer.send(record).get()
            val elapsedTime = System.currentTimeMillis() - time
            logger.info(
                """
    sent record(key={} value={}) meta(partition={}, offset={}) time={}
    
    """.trimIndent(),
                record.key(), record.value(), metadata.partition(),
                metadata.offset(), elapsedTime
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        } finally {
            producer.flush()
            producer.close()
        }
    }

    private fun createProducer(): Producer<Long, String> {
        val props = Properties()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = KafkaConstants.BOOTSTRAP_SERVERS
        props[ProducerConfig.CLIENT_ID_CONFIG] = "EfestoCompilationProducer"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java.name
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            StringSerializer::class.java.name
        return KafkaProducer(props)
    }
}
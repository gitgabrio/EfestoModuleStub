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
package org.kie.efesto.kafka.example;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadUtils {

    private static final Logger logger = LoggerFactory.getLogger(ThreadUtils.class);

    public static Thread getConsumerThread(Consumer<Long, JsonNode> consumer,
                                           int giveUp,
                                           String threadName,
                                           java.util.function.Consumer<ConsumerRecord<Long, JsonNode>> consumerRecordFunction) {
        logger.info("Retrieving thread for {}", threadName);
        return new Thread(threadName) {
            @Override
            public void run() {
                final AtomicInteger noRecordsCount = new AtomicInteger(0);
                while (true) {
                    final ConsumerRecords<Long, JsonNode> consumerRecords =
                            consumer.poll(Duration.ofMillis(100));
                    if (consumerRecords.count() == 0) {
                        int currentNoRecordsCount = noRecordsCount.addAndGet(1);
                        if (currentNoRecordsCount > giveUp) {
//                            break;
                        } else {
                            continue;
                        }
                    }

                    consumerRecords.forEach(record -> {
                        logger.info("Consumer Record:({}, {}, {}, {})\n",
                                    record.key(), record.value(),
                                    record.partition(), record.offset());
                        consumerRecordFunction.accept(record);
                    });
                    consumer.commitAsync();
                }
//                consumer.close();
//                logger.info("DONE");
            }
        };
    }
}

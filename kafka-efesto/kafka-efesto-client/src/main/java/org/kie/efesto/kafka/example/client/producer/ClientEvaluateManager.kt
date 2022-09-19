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

import org.kie.efesto.kafka.example.client.consumer.ClientCompiledConsumer
import org.kie.efesto.kafka.example.client.producer.ClientEvaluateProducer.runEvaluateProducer
import org.kie.efesto.kafka.example.client.storage.ClientStorage
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference

object ClientEvaluateManager {
    private val logger = LoggerFactory.getLogger(ClientCompiledConsumer::class.java)

    @JvmStatic
    fun executeModel(fileName: String, inputData : Any, resultContainer: AtomicReference<Any>) {
        val executeThread: Thread = object : Thread("Execute-$fileName") {
            override fun run() {
                var id = ClientStorage.getId(fileName)
                while (id == null) {
                    try {
                        sleep(10)
                        id = ClientStorage.getId(fileName)
                    } catch (e: InterruptedException) {
                        // ignore
                    }
                }
                logger.info("RecordId: ({})", id)
                var executableId = ClientStorage.getExecutableId(id)
                while (executableId == null) {
                    try {
                        sleep(10)
                        executableId = ClientStorage.getExecutableId(id)
                    } catch (e: InterruptedException) {
                        // ignore
                    }
                }
                logger.info("ExecutableId: ({})", executableId)
                runEvaluateProducer(id, executableId, inputData)
                var retrievedOutput = ClientStorage.getRetrievedOutput(id)
                while (retrievedOutput == null) {
                    try {
                        sleep(10)
                        retrievedOutput = ClientStorage.getRetrievedOutput(id)
                    } catch (e: InterruptedException) {
                        // ignore
                    }
                }
                logger.info("retrievedOutput: ({})", retrievedOutput)
                resultContainer.set(retrievedOutput)
            }
        }
        executeThread.start()
    }
}
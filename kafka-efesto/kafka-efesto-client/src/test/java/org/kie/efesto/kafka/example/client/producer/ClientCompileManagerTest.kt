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

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.kie.efesto.kafka.example.client.Main
import org.kie.efesto.kafka.example.client.consumer.ClientCompiledConsumer.startCompiledConsumer
import org.kie.efesto.kafka.example.client.model.ExecutableId
import org.kie.efesto.kafka.example.client.storage.ClientStorage
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

internal class ClientCompileManagerTest {

    private val logger = LoggerFactory.getLogger(ClientCompileManagerTest::class.java)

    private val pmmlFileName = "LoanApprovalRegression.pmml"

    @Test
    fun compile() {
        startCompiledConsumer()
        ClientCompileManager.compile(pmmlFileName)
        val id = ClientStorage.getId(pmmlFileName)
        assertNotNull(id)
        logger.info("RecordId {}", id)
        logger.info("Wait....")
        try {
            Thread.sleep(5000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val executableId = ClientStorage.getExecutableId(id!!)
        assertNotNull(executableId)
        logger.info("ExecutableId {}", executableId)
    }
}
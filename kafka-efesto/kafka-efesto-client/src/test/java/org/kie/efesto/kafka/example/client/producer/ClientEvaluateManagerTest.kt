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
import org.kie.efesto.kafka.example.client.consumer.ClientCompiledConsumer
import org.kie.efesto.kafka.example.client.consumer.ClientEvaluatedConsumer
import org.kie.efesto.kafka.example.client.storage.ClientStorage
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.HashMap

internal class ClientEvaluateManagerTest {

    private val logger = LoggerFactory.getLogger(ClientEvaluateManagerTest::class.java)

    private val pmmlFileName = "LoanApprovalRegression.pmml"

    @Test
    fun executeDirectlyPmmlModel() {
        ClientCompiledConsumer.startCompiledConsumer()
        ClientEvaluatedConsumer.startEvaluatedConsumer()
        ClientStorage.getId(pmmlFileName)?.let {
            logger.info("Already compiled... ")
        } ?: run {
            logger.info("Compile....")
            ClientCompileManager.compile(pmmlFileName)
        }
        evalPmml(getPmmlInputData())
    }

    private fun evalPmml(inputData: Map<String, Any>) {
        val resultContainer = AtomicReference<Any>()
        ClientEvaluateManager.executeModel(pmmlFileName, inputData, resultContainer)
        logger.info("Wait....")
        try {
            Thread.sleep(20000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        val result = resultContainer.get()
        assertNotNull(result)
        logger.info("Result {}", result)
        logger.info("We can proceed...")
    }

    fun getPmmlInputData() : Map<String, Any> {
        val inputData: MutableMap<String, Any> = HashMap()
        val availableNames = listOf("Paul", "John", "George")
        val random = Random()
        val approved = random.nextBoolean()
        val applicantName = availableNames[random.nextInt(availableNames.size)]
        inputData["approved"] = approved
        inputData["applicantName"] = applicantName
        return inputData
    }

    fun getDrlInputData() : Map<String, Any> {
        // WRONG
        val inputData: MutableMap<String, Any> = HashMap()
        val availableNames = listOf("Paul", "John", "George")
        val random = Random()
        val loanApplicationId = UUID.randomUUID().toString()
        val loanApplicationAmount = (random.nextInt(9) + 1) * 1000
        val loanApplicationDeposit = (random.nextInt(10) + 1) * 100
        val applicantName = availableNames[random.nextInt(availableNames.size)]
        val applicantAge = random.nextInt(40) + 20
        inputData["loanApplicationId"] = loanApplicationId
        inputData["loanApplicationAmount"] = loanApplicationAmount
        inputData["loanApplicationDeposit"] = loanApplicationDeposit
        inputData["applicantName"] = applicantName
        inputData["applicantAge"] = applicantAge
        return inputData
    }
}
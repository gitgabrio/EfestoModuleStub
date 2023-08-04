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
package org.kie.efesto.kafka.example.client


import org.drools.util.FileUtils
import org.kie.efesto.kafka.example.client.managers.EfestoCompilerManager.compileModel
import org.kie.efesto.kafka.example.client.managers.EfestoRuntimeManager.startRuntime
import java.nio.file.Files

object MainClient {
    private const val pmmlFileName = "LoanApprovalRegression.pmml"
    private const val jdrlFileName = "LoanRules.jdrl"

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        compileModel(pmmlFileName)
        compileModel(jdrlFileName)
        startRuntime()
    }

    private fun compileModel(fileName: String) {
        val file = FileUtils.getFile(fileName)
        val content = Files.readString(file.toPath())
        compileModel(content, fileName)
    }
}
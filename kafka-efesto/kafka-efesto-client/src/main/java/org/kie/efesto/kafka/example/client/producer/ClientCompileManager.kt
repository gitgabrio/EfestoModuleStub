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

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.TextNode
import org.kie.efesto.kafka.example.client.storage.ClientStorage
import org.kie.efesto.kafka.example.client.utils.FileUtil
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicLong

object ClientCompileManager {
    private val counter = AtomicLong(10000000)
    @JvmStatic
    @Throws(Exception::class)
    fun compile(fileName: String) {
        val file = FileUtil.getFileFromFileName(fileName, Thread.currentThread().contextClassLoader).orElseThrow {
            RuntimeException(
                "Failed to get $fileName"
            )
        }
        val content = Files.readString(file.toPath())
        val compilationJson = getJson(content, fileName)
        val id = counter.getAndAdd(1)
        ClientCompileProducer.runCompileProducer(id, compilationJson)
        ClientStorage.putId(fileName, id)
    }

    private fun getJson(content: String, fileName: String): String {
        val jsonNode = getJsonNode(content, fileName)
        return jsonNode.toString()
    }

    private fun getJsonNode(content: String, fileName: String): JsonNode {
        val toReturn = JsonNodeFactory(true).objectNode()
        toReturn.set<JsonNode>("content", TextNode(content))
        toReturn.set<JsonNode>("fileName", TextNode(fileName))
        return toReturn
    }
}
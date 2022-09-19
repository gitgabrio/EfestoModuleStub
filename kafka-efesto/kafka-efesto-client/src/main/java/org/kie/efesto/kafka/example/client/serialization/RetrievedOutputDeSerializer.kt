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
package org.kie.efesto.kafka.example.client.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.kie.efesto.kafka.example.client.model.ExecutableId
import org.kie.efesto.kafka.example.client.model.RetrievedOutput
import java.io.IOException

class RetrievedOutputDeSerializer @JvmOverloads constructor(t: Class<RetrievedOutput?>? = null) :
    StdDeserializer<RetrievedOutput>(t) {
    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): RetrievedOutput {
        val node = p.codec.readTree<JsonNode>(p)
        val modelLocalUriId = node["modelLocalUriId"].toString()
        val outputDataString = node["outputData"].toString()
        val executableId = JSONUtil.objectMapper.readValue(modelLocalUriId, ExecutableId::class.java)
        val outputData = JSONUtil.objectMapper.readValue(outputDataString, Any::class.java)
        return RetrievedOutput(executableId, outputData)
    }
}
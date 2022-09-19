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

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.kie.efesto.kafka.example.client.model.ExecutableId
import org.kie.efesto.kafka.example.client.model.RetrievedOutput

object JSONUtil {

    val objectMapper: ObjectMapper = ObjectMapper()

    init {
        val toRegister = SimpleModule()
        toRegister.addDeserializer(
            ExecutableId::class.java,
            ExecutableIdDeSerializer()
        )
        toRegister.addSerializer(
            ExecutableId::class.java,
            ExecutableIdSerializer()
        )
        toRegister.addDeserializer(
            RetrievedOutput::class.java,
            RetrievedOutputDeSerializer()
        )
        objectMapper.registerModule(toRegister)
    }

    @Throws(JsonProcessingException::class)
    fun getExecutableId(jsonNode: JsonNode): ExecutableId {
        val modelLocalUriIdString = jsonNode["modelLocalUriIdString"].asText()
        return getExecutableId(modelLocalUriIdString)
    }

    @Throws(JsonProcessingException::class)
    fun getExecutableId(modelLocalUriIdString: String?): ExecutableId {
        return objectMapper.readValue(modelLocalUriIdString, ExecutableId::class.java)
    }
}
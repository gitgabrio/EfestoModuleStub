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
package org.kie.efesto.kafka.example.server.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.core.serialization.ModelLocalUriIdDeSerializer;
import org.kie.efesto.common.core.serialization.ModelLocalUriIdSerializer;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.pmml.api.identifiers.AbstractModelLocalUriIdPmml;

public class JSONUtil {

    public static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        SimpleModule toRegister = new SimpleModule();
        toRegister.addDeserializer(ModelLocalUriId.class, new ModelLocalUriIdDeSerializer());
        toRegister.addSerializer(ModelLocalUriId.class, new ModelLocalUriIdSerializer());
        toRegister.addDeserializer(AbstractModelLocalUriIdPmml.class, new AbstractModelLocalUriIdPmmlDeSerializer());
        toRegister.addSerializer(AbstractModelLocalUriIdPmml.class, new AbstractModelLocalUriIdPmmlSerializer());
        toRegister.addDeserializer(EfestoOutput.class, new EfestoOutputDeSerializer());
        objectMapper.registerModule(toRegister);
    }

    public static ModelLocalUriId getModelLocalUriId(JsonNode jsonNode) throws JsonProcessingException {
        String modelLocalUriIdString = jsonNode.get("modelLocalUriIdString").asText();
        return getModelLocalUriId(modelLocalUriIdString);
    }

    public static ModelLocalUriId getModelLocalUriId(String modelLocalUriIdString) throws JsonProcessingException {
        ModelLocalUriId toReturn = objectMapper.readValue(modelLocalUriIdString, ModelLocalUriId.class);
        if (toReturn.model().equals("pmml")) {
            toReturn = objectMapper.readValue(modelLocalUriIdString, AbstractModelLocalUriIdPmml.class);
        }
        return toReturn;
    }
}

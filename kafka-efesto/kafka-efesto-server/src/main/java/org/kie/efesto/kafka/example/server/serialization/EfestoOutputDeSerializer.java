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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;

import static org.kie.efesto.kafka.example.server.serialization.JSONUtil.objectMapper;

public class EfestoOutputDeSerializer extends StdDeserializer<EfestoOutput> {

    public EfestoOutputDeSerializer() {
        this(null);
    }

    public EfestoOutputDeSerializer(Class<EfestoOutput> t) {
        super(t);
    }


    @Override
    public EfestoOutput deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String content =node.toString();
        return objectMapper.readValue(content, EfestoOutputImpl.class);
    }

    private static class EfestoOutputImpl extends AbstractEfestoOutput {

        public EfestoOutputImpl() {
            this(null, null);
        }

        protected EfestoOutputImpl(ModelLocalUriId modelLocalUriId, Object outputData) {
            super(modelLocalUriId, outputData);
        }
    }

}

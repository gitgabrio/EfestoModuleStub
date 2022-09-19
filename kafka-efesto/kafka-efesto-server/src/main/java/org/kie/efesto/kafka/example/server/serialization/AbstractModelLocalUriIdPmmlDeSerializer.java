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
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.pmml.api.identifiers.AbstractModelLocalUriIdPmml;
import org.kie.pmml.api.identifiers.LocalComponentIdPmml;
import org.kie.pmml.api.identifiers.LocalComponentIdRedirectPmml;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

public class AbstractModelLocalUriIdPmmlDeSerializer extends StdDeserializer<AbstractModelLocalUriIdPmml> {

    public AbstractModelLocalUriIdPmmlDeSerializer() {
        this(null);
    }

    public AbstractModelLocalUriIdPmmlDeSerializer(Class<AbstractModelLocalUriIdPmml> t) {
        super(t);
    }

    @Override
    public AbstractModelLocalUriIdPmml deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String fullPath = node.get("fullPath").asText();
        LocalUri parsed = LocalUri.parse(fullPath);
        ModelLocalUriId retrieved = new ModelLocalUriId(parsed);
        String basePath = retrieved.basePath();
        String[] parts = basePath.split(SLASH);
        String fileName = parts[1];
        String name = parts[2];
        String model = retrieved.model();
        if (model.equals("pmml")) {
            return new LocalComponentIdPmml(fileName, name);
        } else {
            return new LocalComponentIdRedirectPmml(model, fileName, name);
        }
    }
}

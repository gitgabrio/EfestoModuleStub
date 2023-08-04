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
package stub.module.runtime.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;
import stub.module.runtime.api.model.JdrlInput;

import java.io.IOException;
import java.util.*;

import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;

public class JdrlInputDeserializer extends StdDeserializer<JdrlInput> {

    private static final long serialVersionUID = 5014755163979962781L;

    public JdrlInputDeserializer() {
        this(null);
    }

    public JdrlInputDeserializer(Class<JdrlInput> t) {
        super(t);
    }

    @Override
    public JdrlInput deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        try {
            ModelLocalUriId modelLocalUriId = getObjectMapper().readValue(node.get("modelLocalUriId").toString(), ModelLocalUriId.class);
            ObjectNode inputDataNode = (ObjectNode) node.get("inputData");
            String modelName = inputDataNode.get("modelName").asText();
            String packageName = inputDataNode.get("packageName").asText();
            List<Object> inserts = new ArrayList<>();
            if (inputDataNode.get("inserts") != null) {
                ArrayNode insertsNode = (ArrayNode) inputDataNode.get("inserts");
                Iterator<JsonNode> elements = insertsNode.elements();
                while (elements.hasNext()) {
                    try {
                        JsonNode next = elements.next();
                        String kind = next.get("kind").asText();
                        Class<?> actualClass = Class.forName(kind);
                        inserts.add(getObjectMapper().readValue(next.get("value").toString(), actualClass));
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("Failed to deserialize %s as JdrlInput", node), e);
                    }
                }
            }
            final Map<String, Object> globals = new HashMap<>();
            if (inputDataNode.get("globals") != null) {
                globals.putAll(getObjectMapper().treeToValue(inputDataNode.get("globals"), Map.class));
            }
            EfestoMapInputDTO inputData = new EfestoMapInputDTO(inserts, globals,
                    Collections.emptyMap(),
                    Collections.emptyMap(),
                    modelName,
                    packageName);
            return new JdrlInput(modelLocalUriId, inputData);
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to deserialize %s as JdrlInput", node), e);
        }
    }


}

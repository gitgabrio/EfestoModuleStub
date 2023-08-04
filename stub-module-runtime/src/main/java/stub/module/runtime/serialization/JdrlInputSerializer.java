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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;
import stub.module.runtime.api.model.JdrlInput;

import java.io.IOException;
import java.util.Map;

public class JdrlInputSerializer extends StdSerializer<JdrlInput> {

    private static final long serialVersionUID = 5014755163979962781L;

    public JdrlInputSerializer() {
        this(null);
    }

    public JdrlInputSerializer(Class<JdrlInput> t) {
        super(t);
    }

    @Override
    public void serialize(JdrlInput value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("modelLocalUriId");
        gen.writeObject(value.getModelLocalUriId());
        gen.writeFieldName("inputData");
        gen.writeStartObject();
        EfestoMapInputDTO inputData = value.getInputData();
        if (inputData != null) {
            gen.writeStringField("modelName", inputData.getModelName());
            gen.writeStringField("packageName", inputData.getPackageName());
            gen.writeFieldName("inserts");
            gen.writeStartArray();
            for (Object insert : inputData.getInserts()) {
                gen.writeStartObject();
                gen.writeStringField("kind", insert.getClass().getCanonicalName());
                gen.writeFieldName("value");
                gen.writeObject(insert);
                gen.writeEndObject();
            }
            gen.writeEndArray();
            gen.writeFieldName("globals");
            gen.writeStartObject();
            for (Map.Entry<String, Object> global : inputData.getGlobals().entrySet()) {
                gen.writeFieldName(global.getKey());
                gen.writeObject(global.getValue());
            }
            gen.writeEndObject();
        }
        gen.writeEndObject();
        gen.writeEndObject();
    }


}

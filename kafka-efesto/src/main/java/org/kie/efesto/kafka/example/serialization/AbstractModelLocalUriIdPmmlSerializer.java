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
package org.kie.efesto.kafka.example.serialization;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.kie.pmml.api.identifiers.AbstractModelLocalUriIdPmml;
import org.kie.pmml.api.identifiers.LocalComponentIdRedirectPmml;

import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

public class AbstractModelLocalUriIdPmmlSerializer extends StdSerializer<AbstractModelLocalUriIdPmml> {

    public AbstractModelLocalUriIdPmmlSerializer() {
        this(null);
    }

    public AbstractModelLocalUriIdPmmlSerializer(Class<AbstractModelLocalUriIdPmml> t) {
        super(t);
    }

    @Override
    public void serialize(AbstractModelLocalUriIdPmml value, JsonGenerator gen,
                          SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("model", value.model());
        gen.writeStringField("basePath", decodedPath(value.basePath()));
        gen.writeStringField("fullPath", decodedPath(value.fullPath()));
        gen.writeStringField("fileName", value.getFileName());
        if (value instanceof LocalComponentIdRedirectPmml) {
            gen.writeStringField("redirectModel", ((LocalComponentIdRedirectPmml)value).getRedirectModel());
        }
        gen.writeEndObject();
    }


    static String decodedPath(String toDecode) {
        StringTokenizer tok = new StringTokenizer(toDecode, SLASH);
        StringBuilder builder = new StringBuilder();
        while (tok.hasMoreTokens()) {
            builder.append(SLASH);
            builder.append(decodeString(tok.nextToken()));
        }
        return builder.toString();
    }

    static String decodeString(String toDecode) {
        return URLDecoder.decode(toDecode, StandardCharsets.UTF_8);
    }
}

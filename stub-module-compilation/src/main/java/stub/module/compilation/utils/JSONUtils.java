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
package stub.module.compilation.utils;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub.module.compilation.model.DeclaredType;
import stub.module.compilation.model.DeclaredTypeField;
import stub.module.compilation.model.Global;
import stub.module.compilation.model.JDRL;
import stub.module.compilation.model.Query;
import stub.module.compilation.model.Rule;

public class JSONUtils {

    private static final ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    private static final Logger logger =
            LoggerFactory.getLogger(org.kie.efesto.common.api.utils.JSONUtils.class.getName());

    private JSONUtils() {
    }

    public static Rule getRuleObject(String ruleString) throws JsonProcessingException {
        return objectMapper.readValue(ruleString, Rule.class);
    }

    public static Global getGlobalObject(String globalString) throws JsonProcessingException {
        return objectMapper.readValue(globalString, Global.class);
    }

    public static DeclaredType getDeclaredType(String declaredTypeString) throws JsonProcessingException {
        return objectMapper.readValue(declaredTypeString, DeclaredType.class);
    }

    public static DeclaredTypeField getDeclaredTypeField(String declaredTypeFieldString) throws JsonProcessingException {
        return objectMapper.readValue(declaredTypeFieldString, DeclaredTypeField.class);
    }

    public static JDRL getJDRLObject(String jdrlString) throws JsonProcessingException {
        return objectMapper.readValue(jdrlString, JDRL.class);
    }

    public static JDRL getJDRLObject(File jdrlFile) throws IOException {
        return objectMapper.readValue(jdrlFile, JDRL.class);
    }
}

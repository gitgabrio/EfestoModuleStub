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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import stub.module.compilation.model.JDRL;

import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;

class JDRLUtilsTest {

    @ParameterizedTest
    @ValueSource(strings = {"org/drools/example/LoanRules.jdrl", "LoanRulesNoRuleUnit.jdrl"})
    void getDrlString(String fileName) throws IOException {
        File jdrlFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get jdrlFile"));
        JDRL jdrl = JSONUtils.getJDRLObject(jdrlFile);
        String retrieved = JDRLUtils.getDrlString(jdrl);
        System.out.println(retrieved);
    }
}
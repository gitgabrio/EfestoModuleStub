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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import stub.module.compilation.model.Global;
import stub.module.compilation.model.JDRL;
import stub.module.compilation.model.Rule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileName;

class JSONUtilsTest {

    private static final String fileName = "LoanRules.jdrl";
    private static File jdrlFile;

    @BeforeAll
    static void setUp() {
        jdrlFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get jdrlFile"));
    }

    @Test
    void getRuleObject() throws JsonProcessingException {
        String ruleString = "{\n" +
                "      \"name\": \"SmallDepositApprove\",\n" +
                "      \"when\": \"$l: /loanApplications[ applicant.age >= 20, deposit < 1000, amount <= 2000\",\n" +
                "      \"then\": \"modify($l) { setApproved(true) };\"\n" +
                "    }";
        Rule retrieved = JSONUtils.getRuleObject(ruleString);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getGlobalObject() throws JsonProcessingException {
        String global = "{\n" +
                "      \"type\": \"Integer\",\n" +
                "      \"name\": \"maxAmount\"\n" +
                "    }";
        Global retrieved = JSONUtils.getGlobalObject(global);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getJDRLObject() throws FileNotFoundException, JsonProcessingException {
        String jdrlString = new BufferedReader(new InputStreamReader(new FileInputStream(jdrlFile)))
                .lines().collect(Collectors.joining("\n"));
        JDRL retrieved = JSONUtils.getJDRLObject(jdrlString);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getJDRLObjectFromFile() throws IOException {
        JDRL retrieved = JSONUtils.getJDRLObject(jdrlFile);
        assertThat(retrieved).isNotNull();
    }
}
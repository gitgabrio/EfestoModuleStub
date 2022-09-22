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
package stub.module.runtime.utils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import stub.module.runtime.model.JdrlInput;
import stub.module.runtime.model.StubOutput;

import static org.assertj.core.api.Assertions.assertThat;
import static stub.module.runtime.JdrlTestingUtility.JSON_PAYLOAD;
import static stub.module.runtime.JdrlTestingUtility.input;
import static stub.module.runtime.JdrlTestingUtility.jdrlTestSetup;
import static stub.module.runtime.JdrlTestingUtility.modelLocalUriId;
import static stub.module.runtime.JdrlTestingUtility.runtimeContext;
import static stub.module.runtime.utils.TranslatorUtils.efestoInputToKieSessionFunction;
import static stub.module.runtime.utils.TranslatorUtils.factTypeFromKieSessionFunction;
import static stub.module.runtime.utils.TranslatorUtils.insertInKieSessionConsumer;
import static stub.module.runtime.utils.TranslatorUtils.jdrlInputToKieSessionFunction;
import static stub.module.runtime.utils.TranslatorUtils.jdrlInputToStubOutputFunction;
import static stub.module.runtime.utils.TranslatorUtils.stringToJsonNodeFunction;

class TranslatorUtilsTest {

    @BeforeAll
    public static void setup() throws IOException {
        jdrlTestSetup();
    }

    @Test
    void stringToJsonNodeFunction() {
        JSONObject retrieved = stringToJsonNodeFunction.apply(JSON_PAYLOAD);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getLoanApplicationsfromPopulateKieSession() throws InstantiationException, IllegalAccessException {
        KieSession kieSession = efestoInputToKieSessionFunction.apply(input, runtimeContext);
        JSONObject jsonObject = stringToJsonNodeFunction.apply(JSON_PAYLOAD);
        List<Object> retrieved =  TranslatorUtils.getLoanApplicationsfromPopulateKieSession(jsonObject, kieSession);
        assertThat(retrieved).isNotNull().hasSize(3);
    }

    @Test
    void insertInKieSessionConsumer() throws InstantiationException, IllegalAccessException {
        String fqdn = "org.drools.example.Applicant";
        KieSession kieSession = efestoInputToKieSessionFunction.apply(input, runtimeContext);
        FactType factType = factTypeFromKieSessionFunction.apply(fqdn, kieSession);
        Object applicant = factType.newInstance();
        factType.set(applicant, "name", "Applicant");
        insertInKieSessionConsumer.accept(applicant, kieSession);
    }

    @Test
    void factTypeFromKieSessionConsumer() {
        String fqdn = "org.drools.example.Applicant";
        KieSession kieSession = efestoInputToKieSessionFunction.apply(input, runtimeContext);
        FactType retrieved = factTypeFromKieSessionFunction.apply(fqdn, kieSession);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void efestoInputToKieSessionFunction() {
        KieSession retrieved = efestoInputToKieSessionFunction.apply(input, runtimeContext);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void jdrlInputToKieSessionFunction() {
        JdrlInput jdrlInput = new JdrlInput(modelLocalUriId, "");
        KieSession retrieved = jdrlInputToKieSessionFunction.apply(jdrlInput, runtimeContext);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void jdrlInputToStubOutputFunction() {
        JdrlInput jdrlInput = new JdrlInput(modelLocalUriId, JSON_PAYLOAD);
        Optional<StubOutput> retrieved = jdrlInputToStubOutputFunction.apply(jdrlInput, runtimeContext);
        assertThat(retrieved).isNotNull().isPresent();
    }

}
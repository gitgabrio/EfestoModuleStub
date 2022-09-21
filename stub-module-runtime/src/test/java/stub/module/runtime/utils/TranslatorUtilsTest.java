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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import stub.module.runtime.model.JdrlInput;
import stub.module.runtime.model.StubOutput;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileName;
import static stub.module.runtime.utils.TranslatorUtils.efestoInputToKieSessionFunction;
import static stub.module.runtime.utils.TranslatorUtils.factTypeFromKieSessionFunction;
import static stub.module.runtime.utils.TranslatorUtils.insertInKieSessionConsumer;
import static stub.module.runtime.utils.TranslatorUtils.jdrlInputToKieSessionFunction;
import static stub.module.runtime.utils.TranslatorUtils.jdrlInputToStubOutputFunction;
import static stub.module.runtime.utils.TranslatorUtils.stringToJsonNodeFunction;

class TranslatorUtilsTest {

    private static final String fileName = "LoanRules.jdrl";
    private static File jdrlFile;

    private static ClassLoader classLoader;

    private static Map<String, GeneratedResources> generatedResourcesMap;
    private static ModelLocalUriId modelLocalUriId;

    private static EfestoRuntimeContext runtimeContext;

   private static EfestoInputDrlKieSessionLocal input;

    private static final String JSON_PAYLOAD =
            "{\n" +
                    "  \"maxAmount\":5000,\n" +
                    "  \"loanApplications\":[\n" +
                    "    {\n" +
                    "      \"id\":\"ABC10001\",\n" +
                    "      \"amount\":2000,\n" +
                    "      \"deposit\":100,\n" +
                    "      \"applicant\":{\n" +
                    "        \"age\":45,\n" +
                    "        \"name\":\"John\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\":\"ABC10002\",\n" +
                    "      \"amount\":5000,\n" +
                    "      \"deposit\":100,\n" +
                    "      \"applicant\":{\n" +
                    "        \"age\":25,\n" +
                    "        \"name\":\"Paul\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"id\":\"ABC10015\",\n" +
                    "      \"amount\":1000,\n" +
                    "      \"deposit\":100,\n" +
                    "      \"applicant\":{\n" +
                    "        \"age\":12,\n" +
                    "        \"name\":\"George\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

    @BeforeAll
    public static void setup() throws IOException {
        CompilationManager compilationManager =
                org.kie.efesto.compilationmanager.api.utils.SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to find CompilationManager"));
        jdrlFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get jdrlFile"));
        classLoader = Thread.currentThread().getContextClassLoader();
        EfestoResource efestoResource = new EfestoInputStreamResource(Files.newInputStream(jdrlFile.toPath()),
                                                                      fileName);
        EfestoCompilationContext compilationContext = EfestoCompilationContext.buildWithParentClassLoader(classLoader);
        compilationManager.processResource(compilationContext, efestoResource);
        generatedResourcesMap = compilationContext.getGeneratedResourcesMap();
        modelLocalUriId = getGeneratedModelLocalUri(generatedResourcesMap);
        runtimeContext = EfestoRuntimeContext.buildWithParentClassLoader(classLoader, generatedResourcesMap);
        input = new EfestoInputDrlKieSessionLocal(modelLocalUriId, "");
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

    private static ModelLocalUriId getGeneratedModelLocalUri(Map<String, GeneratedResources> generatedResourcesMap) {
        GeneratedResources generatedResources = generatedResourcesMap.get("drl");
        assertThat(generatedResources).isNotNull();
        return generatedResources.stream().filter(generatedResource -> generatedResource instanceof GeneratedExecutableResource)
                .map(generatedResource -> ((GeneratedExecutableResource) generatedResource).getModelLocalUriId())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve GeneratedExecutableResource"));
    }
}
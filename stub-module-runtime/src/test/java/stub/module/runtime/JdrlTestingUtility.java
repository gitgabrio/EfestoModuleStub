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
package stub.module.runtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileName;

public class JdrlTestingUtility {

    public static final String fileName = "LoanRules.jdrl";
    public static File jdrlFile;

    public static ClassLoader classLoader;

    public static Map<String, GeneratedResources> generatedResourcesMap;
    public static ModelLocalUriId modelLocalUriId;

    public static EfestoRuntimeContext runtimeContext;

    public static EfestoInputDrlKieSessionLocal input;

    public static final String JSON_PAYLOAD =
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


    public static void jdrlTestSetup() throws IOException {
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

    private static ModelLocalUriId getGeneratedModelLocalUri(Map<String, GeneratedResources> generatedResourcesMap) {
        GeneratedResources generatedResources = generatedResourcesMap.get("drl");
        assertThat(generatedResources).isNotNull();
        return generatedResources.stream().filter(generatedResource -> generatedResource instanceof GeneratedExecutableResource)
                .map(generatedResource -> ((GeneratedExecutableResource) generatedResource).getModelLocalUriId())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve GeneratedExecutableResource"));
    }

}

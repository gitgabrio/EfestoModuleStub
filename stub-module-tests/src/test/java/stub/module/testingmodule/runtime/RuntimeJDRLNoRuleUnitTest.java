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
package stub.module.testingmodule.runtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;
import org.kie.efesto.runtimemanager.api.model.EfestoOriginalTypeGeneratedType;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub.module.compilation.model.JDrlCompilationContext;
import stub.module.testingmodule.compilation.CompileJDRLTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;
import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager;

public class RuntimeJDRLNoRuleUnitTest {

    private static CompilationManager compilationManager;
    private static RuntimeManager runtimeManager;

    private static final Logger logger = LoggerFactory.getLogger(CompileJDRLTest.class);
    private static final String fileName = "LoanRulesNoRuleUnit.jdrl";
    private static File jdrlFile;

    @BeforeAll
    static void setUp() {
        compilationManager = SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve CompilationManager"));
        runtimeManager = getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve " +
                                                                                                 "RuntimeManager"));
        jdrlFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get jdrlFile"));
    }

    @Test
    void evaluateInput() throws IOException {
        EfestoResource efestoResource = new EfestoInputStreamResource(Files.newInputStream(jdrlFile.toPath()),
                                                                      fileName);
        JDrlCompilationContext compilationContext = JDrlCompilationContext
                .buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        compilationManager.processResource(compilationContext, efestoResource);
        ModelLocalUriId modelLocalUriId = getModelLocalUriId(compilationContext);

        Map<Map<String, Object>, Boolean> applicationWithExpectedMap = getApplicationWithExpectedMap();
        applicationWithExpectedMap.forEach((key, value) -> assertThat(key.get("approved")).isEqualTo(false));

        final Map<String, Object> globals = new HashMap<>();
        globals.put("maxAmount", 5000);
        final EfestoRuntimeContext runtimeContext =
                EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader(),
                                                                     compilationContext.getGeneratedResourcesMap());
        applicationWithExpectedMap.forEach(evaluatorBiConsumer(globals, modelLocalUriId, runtimeContext));
    }

    private BiConsumer<Map<String, Object>, Boolean> evaluatorBiConsumer(final Map<String, Object> globals,
                                                                         final  ModelLocalUriId modelLocalUriId,
                                                                         final EfestoRuntimeContext runtimeContext) {
        return (requestData, expected) -> {
            AtomicBoolean approved = new AtomicBoolean(false);
            List<Object> inserts = List.of(approved);
            Map<String, EfestoOriginalTypeGeneratedType> convertedFieldTypeMap = new HashMap<>();
            requestData.forEach((s, o) -> convertedFieldTypeMap.put(s,
                                                                    new EfestoOriginalTypeGeneratedType(Integer.class.getCanonicalName(),
                                                                                                        Integer.class.getCanonicalName())));

            EfestoMapInputDTO darMapInputDTO = new EfestoMapInputDTO(inserts, globals,
                                                                     requestData,
                                                                     convertedFieldTypeMap,
                                                                     "modelname",
                                                                     "packageName");
            EfestoInput<EfestoMapInputDTO> input = new BaseEfestoInput<>(modelLocalUriId,
                                                                             darMapInputDTO) {
            };
            Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(runtimeContext, input);
            assertThat(retrieved).isNotNull().size().isEqualTo(1);
            assertThat(approved.get()).isEqualTo(expected);
        };
    }

    private Map<Map<String, Object>, Boolean> getApplicationWithExpectedMap() {
        Map<Map<String, Object>, Boolean> toReturn = new HashMap<>();
//        applicationWithExpectedMap.put(new LoanApplication("ABC10001", new Applicant("John", 45), 2000, 1000), true);
        toReturn.put(Map.of("amount", 2000,
                                              "applicantAge", 45,
                                              "deposit", 1000
        ), true);
//        applicationWithExpectedMap.put(new LoanApplication("ABC10002", new Applicant("Paul", 25), 5000, 100), false);
        toReturn.put(Map.of("amount", 5000,
                                              "applicantAge", 25,
                                              "deposit", 100
        ), true);
//        applicationWithExpectedMap.put(new LoanApplication("ABC10015", new Applicant("George", 12), 1000, 100),
//        false);
        toReturn.put(Map.of("amount", 1000,
                                              "applicantAge", 12,
                                              "deposit", 100
        ), true);
        return toReturn;
    }

    private ModelLocalUriId getModelLocalUriId(JDrlCompilationContext compilationContext) {
        GeneratedExecutableResource generatedExecutableResource =getGeneratedExecutableResource(compilationContext);
        return generatedExecutableResource.getModelLocalUriId();
    }

    private GeneratedExecutableResource getGeneratedExecutableResource(JDrlCompilationContext compilationContext) {
        Map<String, GeneratedResources> generatedResourcesMap = compilationContext.getGeneratedResourcesMap();
        GeneratedResources generatedResources = generatedResourcesMap.get("drl");

        return findAtMostOne(generatedResources,
                                                                                generatedResource -> generatedResource instanceof GeneratedExecutableResource,
                                                                                (s1, s2) -> new KieCompilerServiceException("Found more than one GeneratedExecutableResource: " + s1 + " and " + s2))
                .map(GeneratedExecutableResource.class::cast)
                .orElseThrow(() -> new KieCompilerServiceException("Failed to retrieve a GeneratedExecutableResource"));
    }


}

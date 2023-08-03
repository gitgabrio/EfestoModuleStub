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
package stub.module.compilation.service;

import org.drools.util.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JdrlCompilerServiceTest {

    private static CompilationManager compilationManager;
    private static KieCompilerService kieCompilerService;
    private EfestoCompilationContext context;
    private static final String jdrlFileName = "LoanRules.jdrl";
    private static File jdrlFile;
    private static EfestoInputStreamResource efestoResource;

    private static final Logger logger = LoggerFactory.getLogger(JdrlCompilerServiceTest.class);

    @BeforeAll
    static void setUp() throws FileNotFoundException {
        kieCompilerService = new JdrlCompilerService();
        compilationManager = SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve CompilationManager"));
        jdrlFile = FileUtils.getFile(jdrlFileName);
        assertThat(jdrlFile).isNotNull().exists();
        efestoResource = new EfestoInputStreamResource(new FileInputStream(jdrlFile), jdrlFileName);
    }

    @BeforeEach
    public void init() {
        context = EfestoCompilationContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Test
    void canManageResource() {
        assertThat(kieCompilerService.canManageResource(efestoResource)).isTrue();
    }

    @Test
    void processResource() throws FileNotFoundException {
        EfestoInputStreamResource efestoResource = new EfestoInputStreamResource(new FileInputStream(jdrlFile), jdrlFileName);
        List<EfestoCompilationOutput> retrieved = kieCompilerService.processResource(efestoResource, context);
        assertThat(retrieved).isNotNull().asList().isNotEmpty();
    }

    @Test
    void roundTrip() {
        compilationManager.processResource(context, efestoResource);
        Map<String, GeneratedResources> generatedResourcesMap = context.getGeneratedResourcesMap();
        //commonValidateGeneratedResources(generatedResourcesMap, even);
        Collection<IndexFile> retrieved = context.createIndexFiles(Paths.get("target/test-classes")).values();
//        commonValidateIndexFiles(retrieved);
        retrieved.forEach(indexFile -> {
            try {
                indexFile.delete();
            } catch (Exception e) {
                logger.error("Failed to delete {}", indexFile, e);
            }
        });
    }
//
//
//    private void commonValidateEfestoCompilationOutputs(List<EfestoCompilationOutput> toValidate, boolean even) {
//        assertThat(toValidate).isNotNull();
//        assertThat(toValidate.size()).isEqualTo(1);
//        assertThat(toValidate.get(0)).isInstanceOf(StubCallableOutput.class);
//        StubCallableOutput retrievedOutput = (StubCallableOutput) toValidate.get(0);
//        ModelLocalUriId modelLocalUriId = getModelUri(even);
//        Class<? extends StubExecutor> expectedExecutor;
//        if (even) {
//            expectedExecutor = ExecutorA.class;
//        } else {
//            expectedExecutor = ExecutorB.class;
//        }
//        assertThat(retrievedOutput.getModelLocalUriId()).isEqualTo(modelLocalUriId);
//        assertThat(retrievedOutput.getFullClassNames().size()).isEqualTo(1);
//        assertThat(retrievedOutput.getFullClassNames().get(0)).isEqualTo(expectedExecutor.getCanonicalName());
//    }
//
//    private void commonValidateIndexFiles(Collection<IndexFile> toValidate) {
//        assertThat(toValidate).isNotNull();
//        assertThat(toValidate.size()).isEqualTo(1);
//        IndexFile indexFile = toValidate.iterator().next();
//        assertThat(indexFile.getModel()).isEqualTo(JDRL_MODEL_TYPE);
//    }
//
//    private void commonValidateGeneratedResources(Map<String, GeneratedResources> generatedResourcesMap, boolean even) {
//        assertThat(generatedResourcesMap.containsKey(JDRL_MODEL_TYPE)).isTrue();
//        GeneratedResources generatedResources = generatedResourcesMap.get(JDRL_MODEL_TYPE);
//        Optional<GeneratedExecutableResource> generatedExecutableResource = findAtMostOne(generatedResources,
//                generatedResource -> generatedResource instanceof GeneratedExecutableResource,
//                (s1, s2) -> new KieCompilerServiceException("Found more than one GeneratedExecutableResource: " + s1 + " and " + s2))
//                .map(GeneratedExecutableResource.class::cast);
//        assertThat(generatedExecutableResource).isPresent();
//        ModelLocalUriId expected = getModelUri(even);
//        assertThat(generatedExecutableResource.get().getModelLocalUriId()).isEqualTo(expected);
//    }
//
//    private ModelLocalUriId getModelUri(boolean even) {
//        ModelLocalUriId toReturn;
//        if (even) {
//            String path = SLASH + JDRL_MODEL_TYPE + SLASH + "EventA";
//            LocalUri parsed = LocalUri.parse(path);
//            toReturn = new ModelLocalUriId(parsed);
//        } else {
//            String path = SLASH + JDRL_MODEL_TYPE + SLASH + "EventB";
//            LocalUri parsed = LocalUri.parse(path);
//            toReturn = new ModelLocalUriId(parsed);
//        }
//        return toReturn;
//    }
}
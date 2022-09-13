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
package stub.module.testingmodule.compilation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub.module.compilation.model.JDrlCompilationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileName;

public class CompileJDRLTest {

    private static CompilationManager compilationManager;

    private static final Logger logger = LoggerFactory.getLogger(CompileJDRLTest.class);
    private static final String fileName = "org/drools/example/LoanRules.jdrl";
    private static File jdrlFile;

    @BeforeAll
    static void setUp() {
        compilationManager = SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve CompilationManager"));
        jdrlFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get jdrlFile"));
    }

    @Test
    void processResource() throws IOException {
        EfestoResource efestoResource = new EfestoInputStreamResource(Files.newInputStream(jdrlFile.toPath()),
                                                                      fileName);
        JDrlCompilationContext compilationContext = JDrlCompilationContext
                .buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        compilationManager.processResource(compilationContext, efestoResource);
        Map<String, GeneratedResources> generatedResourcesMap = compilationContext.getGeneratedResourcesMap();
        assertThat(generatedResourcesMap).containsKey("drl");
        GeneratedResources generatedResources = generatedResourcesMap.get("drl");
        Optional<GeneratedExecutableResource> generatedExecutableResource = findAtMostOne(generatedResources,
                                                                                          generatedResource -> generatedResource instanceof GeneratedExecutableResource,
                                                                                          (s1, s2) -> new KieCompilerServiceException("Found more than one GeneratedExecutableResource: " + s1 + " and " + s2))
                .map(GeneratedExecutableResource.class::cast);
        assertThat(generatedExecutableResource).isPresent();
        Map<String, IndexFile> indexFilesMap = compilationContext.createIndexFiles(Paths.get("target/test-classes/"));
        assertThat(indexFilesMap).containsKey("drl");
        indexFilesMap.values().forEach(indexFile -> {
            try {
                indexFile.delete();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }
}

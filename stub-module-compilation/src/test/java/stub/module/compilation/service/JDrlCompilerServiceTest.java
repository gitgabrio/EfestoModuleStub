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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;

import org.drools.drl.ast.descr.PackageDescr;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub.module.compilation.model.EfestoRedirectOutputJDrl;
import stub.module.compilation.model.JDRL;
import stub.module.compilation.model.JDrlCompilationContext;
import stub.module.compilation.utils.JSONUtils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;
import static stub.module.api.CommonConstants.MODEL_TYPE;

class JDrlCompilerServiceTest {

    private static KieCompilerService kieCompilerService;

    private static final Logger logger = LoggerFactory.getLogger(StubCompilerServiceTest.class);
    private static final String fileName = "LoanRules.jdrl";
    private static final String fullFilePath = "org/drools/example/" + fileName;
    private static File jdrlFile;

    @BeforeAll
    static void setUp() {
        kieCompilerService = new JDrlCompilerService();
        jdrlFile = getFileFromFileName(fullFilePath).orElseThrow(() -> new RuntimeException("Failed to get jdrlFile"));
    }

    @Test
    void canManageResource() throws IOException {
        EfestoResource efestoResource = new EfestoInputStreamResource(Files.newInputStream(jdrlFile.toPath()),
                                                                      fileName);
        assertThat(kieCompilerService.canManageResource(efestoResource)).isTrue();
        efestoResource = () -> 5;
        assertThat(kieCompilerService.canManageResource(efestoResource)).isFalse();
    }

    @Test
    void processResource() throws IOException {
        EfestoResource efestoResource = new EfestoInputStreamResource(Files.newInputStream(jdrlFile.toPath()),
                                                                      fileName);
        JDrlCompilationContext compilationContext = JDrlCompilationContext
                .buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        List<EfestoCompilationOutput> retrieved = kieCompilerService.processResource(efestoResource,
                                                                                     compilationContext);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.size()).isEqualTo(1);
        EfestoCompilationOutput efestoCompilationOutput = retrieved.get(0);
        assertThat(efestoCompilationOutput).isInstanceOf(EfestoRedirectOutputJDrl.class);
    }

    @Test
    void getModelType() {
        assertThat(kieCompilerService.getModelType()).isEqualTo(MODEL_TYPE);
    }

    @Test
    void getEfestoRedirectOutputJDrl() throws IOException {
        JDRL jdrl = JSONUtils.getJDRLObject(jdrlFile);
        JDrlCompilationContext compilationContext = JDrlCompilationContext
                .buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        EfestoRedirectOutputJDrl retrieved = JDrlCompilerService.getEfestoRedirectOutputJDrl(fileName, jdrl, compilationContext);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getContent()).isNotNull();
        Set<PackageDescr> packageDescrs = retrieved.getContent();
        assertThat(packageDescrs.size()).isEqualTo(1);
    }
}
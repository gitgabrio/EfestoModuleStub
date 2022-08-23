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

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import stub.module.api.ExecutorA;
import stub.module.api.ExecutorB;
import stub.module.api.StubExecutor;
import stub.module.compilation.model.StubCallableOutput;
import stub.module.compilation.model.StubResource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StubCompilerServiceTest {

    private static CompilationManager compilationManager;
    private static KieCompilerService kieCompilerService;
    private static EfestoCompilationContext context;

    @BeforeAll
    static void setUp() {
        kieCompilerService = new StubCompilerService();
        context = EfestoCompilationContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        compilationManager = SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve CompilationManager"));
    }
    @Test
    void canManageResource() {
        EfestoResource efestoResource = new StubResource("Content");
        assertThat(kieCompilerService.canManageResource(efestoResource)).isTrue();
        efestoResource = () -> 5;
        assertThat(kieCompilerService.canManageResource(efestoResource)).isFalse();
    }

    @Test
    void processResourceValid() {
        StubResource efestoResource = new StubResource("ConTen"); // even content - expecting A
        List<EfestoCompilationOutput> retrieved = kieCompilerService.processResource(efestoResource, context);
        commonValidateEfestoCompilationOutputs(retrieved, true);
        efestoResource = new StubResource("ConTenT"); // even content - expecting A
        retrieved = kieCompilerService.processResource(efestoResource, context);
        commonValidateEfestoCompilationOutputs(retrieved, false);
    }

    @Test
    void roundTrip() {
        StubResource efestoResource = new StubResource("ConTen");
        Collection<IndexFile> retrieved = compilationManager.processResource(context, efestoResource);
        commonValidateIndexFiles(retrieved, true);
        efestoResource = new StubResource("ConTenT");
        retrieved = compilationManager.processResource(context, efestoResource);
        commonValidateIndexFiles(retrieved, false);
    }


    private void commonValidateEfestoCompilationOutputs(List<EfestoCompilationOutput> toValidate, boolean even) {
        assertThat(toValidate).isNotNull();
        assertThat(toValidate.size()).isEqualTo(1);
        assertThat(toValidate.get(0)).isInstanceOf(StubCallableOutput.class);
        StubCallableOutput retrievedOutput = (StubCallableOutput) toValidate.get(0);
        FRI expectedFri;
        Class<? extends StubExecutor> expectedExecutor;
        if (even) {
            expectedFri = new FRI("stub", "EventA");
            expectedExecutor = ExecutorA.class;
        } else {
            expectedFri = new FRI("stub", "EventB");
            expectedExecutor = ExecutorB.class;
        }
        assertThat(retrievedOutput.getFri()).isEqualTo(expectedFri);
        assertThat(retrievedOutput.getFullClassNames().size()).isEqualTo(1);
        assertThat(retrievedOutput.getFullClassNames().get(0)).isEqualTo(expectedExecutor.getCanonicalName());
    }

    private void commonValidateIndexFiles(Collection<IndexFile> toValidate, boolean even) {
        assertThat(toValidate).isNotNull();
        assertThat(toValidate.size()).isEqualTo(1);
        IndexFile indexFile = toValidate.iterator().next();
        String expectedModel = even ? "EventA" : "EventB";
        assertThat(indexFile.getModel()).isEqualTo(expectedModel);
    }
}
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

import org.drools.example.Applicant;
import org.drools.example.LoanApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub.module.compilation.model.JDrlCompilationContext;
import stub.module.runtime.model.JdrlInput;
import stub.module.runtime.model.JdrlOutput;
import stub.module.testingmodule.compilation.CompileJDRLTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;
import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;
import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager;

public class RuntimeJDRLRuleUnitTest {

    private static CompilationManager compilationManager;
    private static RuntimeManager runtimeManager;

    private static final Logger logger = LoggerFactory.getLogger(CompileJDRLTest.class);
    private static final String fileName = "LoanRules.jdrl";
    private static final String fullPathFileName = String.format("org/drools/example/%s", fileName);
    private static final String requestFileName = "JdrlEvaluationMessage.json";
    private static File jdrlFile;
    private static JdrlInput jdrlInput;
    private static JDrlCompilationContext compilationContext;
    private static EfestoRuntimeContext runtimeContext;

    @BeforeAll
    static void setUp() throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        compilationManager = SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve CompilationManager"));
        runtimeManager = getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve " +
                "RuntimeManager"));
        jdrlFile = getFileFromFileName(fullPathFileName).orElseThrow(() -> new RuntimeException("Failed to get jdrlFile"));
        EfestoResource efestoResource = new EfestoInputStreamResource(Files.newInputStream(jdrlFile.toPath()),
                fileName);
        compilationContext = JDrlCompilationContext
                .buildWithParentClassLoader(contextClassLoader);
        compilationManager.processResource(compilationContext, efestoResource);
        runtimeContext = EfestoRuntimeContextUtils.buildWithParentClassLoader(contextClassLoader, compilationContext.getGeneratedResourcesMap());

        File requestFile = getFileFromFileName(requestFileName).orElseThrow(() -> new RuntimeException("Failed to get jdrlFile"));
        jdrlInput = getObjectMapper().readValue(requestFile, JdrlInput.class);
    }

    @Test
    void evaluateInput() {
        Map<LoanApplication, Boolean> applicationWithExpectedMap = new HashMap<>();
        applicationWithExpectedMap.put(new LoanApplication("ABC10001", new Applicant("John", 45), 2000, 1000), true);
        applicationWithExpectedMap.put(new LoanApplication("ABC10002", new Applicant("Paul", 25), 5000, 100), false);
        applicationWithExpectedMap.put(new LoanApplication("ABC10015", new Applicant("George", 12), 1000, 100), false);
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(runtimeContext, jdrlInput);
        assertThat(retrieved).isNotNull().size().isEqualTo(1);
        EfestoOutput next = retrieved.iterator().next();
        assertThat(next).isExactlyInstanceOf(JdrlOutput.class);
        JdrlOutput output = (JdrlOutput)next;
        List<Object> inserts =  (List) output.getOutputData().get("inserts");
        List<LoanApplication> retrievedInserts =  inserts
                .stream()
                .map(LoanApplication.class::cast)
                .collect(Collectors.toList());
        retrievedInserts.forEach(loanApplication -> {
                   boolean expected = applicationWithExpectedMap.get(loanApplication);
                   assertThat(loanApplication.isApproved()).isEqualTo(expected);
                });
    }
}

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
package stub.module.runtime.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.api.utils.SPIUtils;
import stub.module.runtime.model.JdrlInput;
import stub.module.runtime.model.StubOutput;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;
import static stub.module.api.CommonConstants.MODEL_TYPE;
import static stub.module.runtime.JdrlTestingUtility.JSON_PAYLOAD;
import static stub.module.runtime.JdrlTestingUtility.jdrlTestSetup;
import static stub.module.runtime.JdrlTestingUtility.modelLocalUriId;
import static stub.module.runtime.JdrlTestingUtility.runtimeContext;

class JdrlRuntimeServiceTest {

    private static KieRuntimeService kieRuntimeService;
    private static RuntimeManager runtimeManager;
    
//    private static ModelLocalUriId modelLocalUriId;

    @BeforeAll
    static void setUp() throws IOException {
        jdrlTestSetup();
        kieRuntimeService = new JdrlRuntimeService();
        runtimeManager = SPIUtils.getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve" +
                                                                                                          " RuntimeManager"));
        modelLocalUriId = getJdrlModelLocalUriId();
    }

    @Test
    void canManageInput() {
        EfestoInput efestoInput = new JdrlInput(modelLocalUriId, "Content");
        assertThat(kieRuntimeService.canManageInput(efestoInput, runtimeContext)).isTrue();
    }

    @Test
    void evaluateInput() {
        EfestoInput efestoInput = new JdrlInput(modelLocalUriId, JSON_PAYLOAD);
        Optional<StubOutput> retrieved = kieRuntimeService.evaluateInput(efestoInput, runtimeContext);
        assertThat(retrieved).isPresent();
        StubOutput output = retrieved.get();
//        commonEvaluateStubOutput(output, content, even);
    }

    @Test
    void roundTrip() {
        EfestoInput efestoInput = new JdrlInput(modelLocalUriId, JSON_PAYLOAD);
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(runtimeContext, efestoInput);
//        commonValidateEfestoOutputs(retrieved, content, even);
    }

//    private void commonEvaluateStubOutput(StubOutput toEvaluate, String content, boolean even) {
//        Class<? extends StubExecutor> expectedExecutor = even ? ExecutorA.class : ExecutorB.class;
//        Class<? extends StubExecutor> unexpectedExecutor = even ? ExecutorB.class : ExecutorA.class;
//        assertThat(toEvaluate.getOutputData()).contains(expectedExecutor.getCanonicalName());
//        assertThat(toEvaluate.getOutputData()).doesNotContain(unexpectedExecutor.getCanonicalName());
//        assertThat(toEvaluate.getOutputData()).contains(content);
//
//    }
//
//    private void commonValidateEfestoOutputs(Collection<EfestoOutput> toValidate, String content, boolean even) {
//        assertThat(toValidate).isNotNull();
//        assertThat(toValidate.size()).isEqualTo(1);
//
//        EfestoOutput efestoOutput = toValidate.iterator().next();
//        assertThat(efestoOutput).isInstanceOf(StubOutput.class);
//        StubOutput stubOutput = (StubOutput) efestoOutput;
//        commonEvaluateStubOutput(stubOutput, content, even);
//    }

    private static ModelLocalUriId getJdrlModelLocalUriId() {
        String path = SLASH + MODEL_TYPE + SLASH + "LoanRules";
        LocalUri parsed = LocalUri.parse(path);
        return new ModelLocalUriId(parsed);
    }
}
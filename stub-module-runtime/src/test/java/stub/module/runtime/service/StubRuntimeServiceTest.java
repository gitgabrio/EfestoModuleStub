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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.api.utils.SPIUtils;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import stub.module.api.ExecutorA;
import stub.module.api.ExecutorB;
import stub.module.api.StubExecutor;
import stub.module.runtime.api.model.StubInput;
import stub.module.runtime.api.model.StubOutput;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;
import static stub.module.api.CommonConstants.STUB_MODEL_TYPE;

class StubRuntimeServiceTest {

    private static RuntimeManager runtimeManager;
    private static KieRuntimeService kieRuntimeService;
    private static EfestoRuntimeContext context;

    @BeforeAll
    static void setUp() {
        kieRuntimeService = new StubRuntimeService();
        context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        runtimeManager = SPIUtils.getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve" +
                " RuntimeManager"));
    }

    @ParameterizedTest
    @CsvSource({
            "EventA, true",
            "EventB, true",
            "EventC, false",
    })
    void canManageInput(String event, boolean manageable) {
        ModelLocalUriId fri = getModelUri(event);
        EfestoInput efestoInput = new StubInput(fri, "Content");
        assertThat(kieRuntimeService.canManageInput(efestoInput, context)).isEqualTo(manageable);
    }

    @ParameterizedTest
    @CsvSource({
            "EventA, ContentA, true",
            "EventB, ContentB, false"
    })
    void evaluateInput(String event, String content, boolean even) {
        ModelLocalUriId modelLocalUriId = getModelUri(event);
        EfestoInput efestoInput = new StubInput(modelLocalUriId, content);
        Optional<StubOutput> retrieved = kieRuntimeService.evaluateInput(efestoInput, context);
        assertThat(retrieved).isPresent();
        StubOutput output = retrieved.get();
        commonEvaluateStubOutput(output, content, even);
    }

    @ParameterizedTest
    @CsvSource({
            "EventA, ContentA, true",
            "EventB, ContentB, false"
    })
    void roundTrip(String event, String content, boolean even) {
        ModelLocalUriId modelLocalUriId = getModelUri(event);
        EfestoInput efestoInput = new StubInput(modelLocalUriId, content);
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(context, efestoInput);
        commonValidateEfestoOutputs(retrieved, content, even);
    }

    private void commonEvaluateStubOutput(StubOutput toEvaluate, String content, boolean even) {
        Class<? extends StubExecutor> expectedExecutor = even ? ExecutorA.class : ExecutorB.class;
        Class<? extends StubExecutor> unexpectedExecutor = even ? ExecutorB.class : ExecutorA.class;
        assertThat(toEvaluate.getOutputData()).contains(expectedExecutor.getCanonicalName());
        assertThat(toEvaluate.getOutputData()).doesNotContain(unexpectedExecutor.getCanonicalName());
        assertThat(toEvaluate.getOutputData()).contains(content);

    }

    private void commonValidateEfestoOutputs(Collection<EfestoOutput> toValidate, String content, boolean even) {
        assertThat(toValidate).isNotNull();
        assertThat(toValidate.size()).isEqualTo(1);

        EfestoOutput efestoOutput = toValidate.iterator().next();
        assertThat(efestoOutput).isInstanceOf(StubOutput.class);
        StubOutput stubOutput = (StubOutput) efestoOutput;
        commonEvaluateStubOutput(stubOutput, content, even);
    }

    private ModelLocalUriId getModelUri(String event) {
        String path = SLASH + STUB_MODEL_TYPE + SLASH + event;
        LocalUri parsed = LocalUri.parse(path);
        return new ModelLocalUriId(parsed);
    }
}
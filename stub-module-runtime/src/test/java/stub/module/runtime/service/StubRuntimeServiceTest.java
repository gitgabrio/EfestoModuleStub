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

import java.util.Collection;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.io.IndexFile;
import org.kie.efesto.common.api.model.FRI;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.api.utils.SPIUtils;
import stub.module.api.ExecutorA;
import stub.module.api.ExecutorB;
import stub.module.api.StubExecutor;
import stub.module.runtime.model.StubInput;
import stub.module.runtime.model.StubOutput;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StubRuntimeServiceTest {

    private static RuntimeManager runtimeManager;
    private static KieRuntimeService kieRuntimeService;
    private static EfestoRuntimeContext context;

    @BeforeAll
    static void setUp() {
        kieRuntimeService = new StubRuntimeService();
        context = EfestoRuntimeContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        runtimeManager = SPIUtils.getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve" +
                                                                                                          " RuntimeManager"));
    }

    @Test
    void canManageInput() {
        FRI fri = new FRI("stub", "EventA");
        EfestoInput efestoInput = new StubInput(fri, "Content");
        assertThat(kieRuntimeService.canManageInput(efestoInput, context)).isTrue();
        fri = new FRI("stub", "EventB");
        efestoInput = new StubInput(fri, "Content");
        assertThat(kieRuntimeService.canManageInput(efestoInput, context)).isTrue();
        fri = new FRI("stub", "EventC");
        efestoInput = new StubInput(fri, "Content");
        assertThat(kieRuntimeService.canManageInput(efestoInput, context)).isFalse();
    }

    @Test
    void evaluateInput() {
        String event = "EventA";
        String content = "ContentA";
        FRI fri = new FRI("stub", event);
        EfestoInput efestoInput = new StubInput(fri, content);
        Optional<StubOutput> retrieved = kieRuntimeService.evaluateInput(efestoInput, context);
        assertThat(retrieved).isPresent();
        StubOutput output = retrieved.get();
        commonEvaluateStubOutput(output, content, true);

        event = "EventB";
        content = "ContentB";
        fri = new FRI("stub", event);
        efestoInput = new StubInput(fri, content);
        retrieved = kieRuntimeService.evaluateInput(efestoInput, context);
        assertThat(retrieved).isPresent();
        output = retrieved.get();
        commonEvaluateStubOutput(output, content, false);
    }

    @Test
    void roundTrip() {
        String event = "EventA";
        String content = "ContentA";
        FRI fri = new FRI("stub", event);
        EfestoInput efestoInput = new StubInput(fri, content);
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(context, efestoInput);
        commonValidateEfestoOutputs(retrieved, content, true);

        event = "EventB";
        content = "ContentB";
        fri = new FRI("stub", event);
        efestoInput = new StubInput(fri, content);
        retrieved = runtimeManager.evaluateInput(context, efestoInput);
        commonValidateEfestoOutputs(retrieved, content, false);
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
        StubOutput stubOutput = (StubOutput)efestoOutput;
        commonEvaluateStubOutput(stubOutput, content, even);

    }



}
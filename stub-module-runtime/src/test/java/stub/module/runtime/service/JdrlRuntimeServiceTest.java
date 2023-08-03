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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.example.LoanApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.api.utils.SPIUtils;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextUtils;
import stub.module.runtime.model.JdrlInput;
import stub.module.runtime.model.JdrlOutput;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;
import static stub.module.runtime.testutils.TestUtils.getJdrlInput;

class JdrlRuntimeServiceTest {

    private static RuntimeManager runtimeManager;
    private static KieRuntimeService kieRuntimeService;
    private static EfestoRuntimeContext context;
    private static EfestoInput efestoInput;

    @BeforeAll
    static void setUp() throws JsonProcessingException {
        kieRuntimeService = new JdrlRuntimeService();
        context = EfestoRuntimeContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        runtimeManager = SPIUtils.getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve" +
                " RuntimeManager"));
        efestoInput = getJdrlInput();
    }

    @Test
    void canManageInput() {
        assertThat(kieRuntimeService.canManageInput(efestoInput, context)).isTrue();
    }

    @Test
    void evaluateInput() {
        List<LoanApplication> inserts = ((JdrlInput) efestoInput).getInputData()
                .getInserts()
                        .stream().map(LoanApplication.class::cast)
                        .collect(Collectors.toList());
        assertThat(inserts.stream().filter(insert -> insert.isApproved()).collect(Collectors.toList()))
                .asList().isEmpty();
        inserts.forEach(insert -> assertThat(insert.isApproved()).isFalse());
        Optional<JdrlOutput> retrieved = kieRuntimeService.evaluateInput(efestoInput, context);
        assertThat(retrieved).isPresent();
        assertThat(inserts.stream().filter(insert -> insert.isApproved()).collect(Collectors.toList()))
                .asList().hasSize(1);
    }


    @Test
    void parseJsonInput() throws JsonProcessingException {
        String modelLocalUriIdString = "{\"model\":\"jdrl\",\"basePath\":\"/org/drools/example/LoanRules\",\"fullPath\":\"/jdrl/org/drools/example/LoanRules\"}";
        EfestoMapInputDTO efestoMapInputDTO = ((JdrlInput)efestoInput).getInputData();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("package", efestoMapInputDTO.getPackageName());
        inputData.put("modelName", efestoMapInputDTO.getModelName());
        inputData.put("globals", efestoMapInputDTO.getGlobals());
        inputData.put("inserts", efestoMapInputDTO.getInserts());
        String inputDataString = getObjectMapper().writeValueAsString(inputData);
        Optional<JdrlInput> retrieved = kieRuntimeService.parseJsonInput(modelLocalUriIdString, inputDataString);
        assertThat(retrieved).isNotNull().isPresent();
    }

    @Test
    void roundTrip() {
        Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(context, efestoInput);


    }
}
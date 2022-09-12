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

import java.util.Optional;

import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub.module.api.StubExecutor;
import stub.module.runtime.model.StubInput;
import stub.module.runtime.model.StubOutput;

import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.getGeneratedExecutableResource;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.isPresentExecutableOrRedirect;

import static stub.module.api.CommonConstants.MODEL_TYPE;

public class StubRuntimeService implements KieRuntimeService<String, String, StubInput, StubOutput,
        EfestoRuntimeContext> {

    private static final Logger logger = LoggerFactory.getLogger(StubRuntimeService.class.getName());

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return toEvaluate instanceof StubInput && isPresentExecutableOrRedirect(toEvaluate.getFRI(),
                                                                                toEvaluate.getFRI().getModel());
    }

    @Override
    public Optional<StubOutput> evaluateInput(StubInput toEvaluate, EfestoRuntimeContext context) {
        if (!canManageInput(toEvaluate, context)) {
            throw new KieRuntimeServiceException("Unexpected parameters  " + toEvaluate.getClass() + "  " + context.getClass());
        }
        return getStubOutput(toEvaluate, context);
    }

    private Optional<StubOutput> getStubOutput(StubInput stubInput, EfestoRuntimeContext context) {
        try {
            StubExecutor stubExecutor = loadStubExecutor(stubInput, context);
            String result = stubExecutor.execute(stubInput.getInputData());
            return Optional.of(new StubOutput(stubInput.getFRI(), result));
        } catch (Exception e) {
            logger.error("Failed to get result due to " + e.getMessage(), e);
            return Optional.empty();
        }
    @Override
    public String getModelType() {
        return MODEL_TYPE;
    }
}

    private StubExecutor loadStubExecutor(StubInput stubInput, EfestoRuntimeContext context) {
        GeneratedExecutableResource executableResource = getGeneratedExecutableResource(stubInput.getFRI(),
                                                                                        stubInput.getFRI().getModel())
                .orElseThrow(() -> new KieRuntimeServiceException("Failed to load GeneratedExecutableResource for " + stubInput.getFRI()));
        StubExecutor stubExecutor;
        try {
            String stubExecutorClassName = executableResource.getFullClassNames().get(0);
            final Class<? extends StubExecutor> aClass =
                    (Class<? extends StubExecutor>) context.loadClass(stubExecutorClassName);
            return aClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new KieRuntimeServiceException(e);
        }
    }
}

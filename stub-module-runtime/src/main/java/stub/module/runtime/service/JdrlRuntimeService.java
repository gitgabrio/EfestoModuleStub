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

import java.util.Collections;
import java.util.Optional;

import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub.module.runtime.model.JdrlInput;
import stub.module.runtime.model.StubOutput;

import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.isPresentExecutableOrRedirect;
import static stub.module.api.CommonConstants.MODEL_TYPE;
import static stub.module.runtime.utils.TranslatorUtils.jdrlInputToStubOutputFunction;

public class JdrlRuntimeService implements KieRuntimeService<String, String, JdrlInput, StubOutput,
        EfestoRuntimeContext> {

    private static final Logger logger = LoggerFactory.getLogger(JdrlRuntimeService.class.getName());

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(JdrlInput.class, Collections.singletonList(String.class));
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return isPresentExecutableOrRedirect(toEvaluate.getModelLocalUriId(), context);
    }

    @Override
    public Optional<StubOutput> evaluateInput(JdrlInput toEvaluate, EfestoRuntimeContext context) {
        return jdrlInputToStubOutputFunction.apply(toEvaluate, context);
    }

    @Override
    public String getModelType() {
        return MODEL_TYPE;
    }
}

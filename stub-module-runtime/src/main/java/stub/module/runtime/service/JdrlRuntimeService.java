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

import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.*;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub.module.runtime.api.model.JdrlInput;
import stub.module.runtime.api.model.JdrlOutput;

import java.util.*;

import static org.kie.efesto.common.core.utils.JSONUtils.getInputData;
import static org.kie.efesto.common.core.utils.JSONUtils.getObjectMapper;
import static org.kie.efesto.runtimemanager.api.utils.GeneratedResourceUtils.isPresentExecutableOrRedirect;
import static org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager;
import static stub.module.api.CommonConstants.JDRL_MODEL_TYPE;

public class JdrlRuntimeService implements KieRuntimeService<EfestoMapInputDTO, Map<String, Object>, JdrlInput, JdrlOutput,
        EfestoRuntimeContext> {

    private static final Logger logger = LoggerFactory.getLogger(JdrlRuntimeService.class.getName());
    private static RuntimeManager runtimeManager = getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve " +
            "RuntimeManager"));

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(JdrlInput.class, EfestoMapInputDTO.class);
    }


    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return toEvaluate instanceof JdrlInput && isPresentExecutableOrRedirect(toEvaluate.getModelLocalUriId(),
                context);
    }

    @Override
    public Optional<JdrlOutput> evaluateInput(JdrlInput toEvaluate, EfestoRuntimeContext context) {
        if (!canManageInput(toEvaluate, context)) {
            throw new KieRuntimeServiceException("Unexpected parameters  " + toEvaluate.getClass() + "  " + context.getClass());
        }
        return getJdrlOutput(toEvaluate, context);
    }

    @Override
    public String getModelType() {
        return JDRL_MODEL_TYPE;
    }

    @Override
    public Optional<JdrlInput> parseJsonInput(String modelLocalUriIdString, String inputDataString) {
        ModelLocalUriId modelLocalUriId;
        try {
            modelLocalUriId = getObjectMapper().readValue(modelLocalUriIdString, ModelLocalUriId.class);
            if (!modelLocalUriId.model().equals(getModelType())) {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to parse %s as ModelLocalUriId", modelLocalUriIdString));
        }
        Map<String, Object> inputData;
        try {
            inputData = getInputData(inputDataString);
            return Optional.of(new JdrlInput(modelLocalUriId, getDrlMapInput(inputData)));
        } catch (Exception e) {
            throw new KieEfestoCommonException(String.format("Failed to parse %s as Map<String, Object>", inputDataString));
        }
    }

    private Optional<JdrlOutput> getJdrlOutput(JdrlInput jdrlInput, EfestoRuntimeContext context) {
        ModelLocalUriId originalModelLocalUriId = jdrlInput.getModelLocalUriId();
        String targetLocalUriString = originalModelLocalUriId.asLocalUri().path().replace("jdrl", "drl");
        LocalUri targetLocalUri = LocalUri.parse(targetLocalUriString);
        ModelLocalUriId targetModelLocalUriId = new ModelLocalUriId(targetLocalUri);
        EfestoInput<EfestoMapInputDTO> input = new BaseEfestoInput<>(targetModelLocalUriId, jdrlInput.getInputData());
        Collection<EfestoOutput> efestoOutputs = runtimeManager.evaluateInput(context, input);
        if (efestoOutputs.isEmpty()) {
            return Optional.empty();
        } else {
            EfestoOutput<Map<String, Object>> retrieved = efestoOutputs.iterator().next();
            return Optional.of(new JdrlOutput(jdrlInput.getModelLocalUriId(), retrieved.getOutputData()));
        }
    }

    private static EfestoMapInputDTO getDrlMapInput(Map<String, Object> inputData) {
        List<Object> inserts = new ArrayList<>();
        if (inputData.containsKey("inserts")) {
            inserts = (List<Object>) inputData.get("inserts");
            inputData.remove("inserts");
        }
        final Map<String, Object> globals = new HashMap<>();
        if (inputData.containsKey("globals")) {
            globals.putAll((Map<String, Object>)inputData.get("globals"));
            inputData.remove("globals");
        }
        String packageName = (String) inputData.get("packageName");
        inputData.remove("packageName");
        String modelName = (String) inputData.get("modelName");
        inputData.remove("modelName");
        final Map<String, EfestoOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        inputData.forEach((s, o) -> {
            String objectType = o.getClass().getCanonicalName();
            EfestoOriginalTypeGeneratedType toPut = new EfestoOriginalTypeGeneratedType(objectType, objectType);
            fieldTypeMap.put(s, toPut);
        });
        return new EfestoMapInputDTO(inserts, globals, inputData, fieldTypeMap, modelName, packageName);
    }
}

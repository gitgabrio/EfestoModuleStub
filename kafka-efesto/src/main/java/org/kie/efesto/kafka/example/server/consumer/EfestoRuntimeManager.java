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
package org.kie.efesto.kafka.example.server.consumer;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContextImpl;
import org.kie.efesto.kafka.example.server.storage.ContextStorage;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.AbstractEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContextImpl;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.api.utils.SPIUtils;
import org.kie.pmml.api.identifiers.AbstractModelLocalUriIdPmml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.kafka.example.serialization.JSONUtil.getModelLocalUriId;
import static org.kie.efesto.kafka.example.serialization.JSONUtil.objectMapper;

public class EfestoRuntimeManager {

    private static final RuntimeManager runtimeManager;

    private static final Logger logger = LoggerFactory.getLogger(ServerCompileConsumer.class);

    private static final String CHECK_CLASSPATH = "check classpath and dependencies!";

    static {
        runtimeManager = SPIUtils.getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve" +
                                                                                                          " RuntimeManager"));
    }

    public static EfestoOutput evaluateModel(JsonNode jsonNode, Map<String, Object> inputData) throws JsonProcessingException {
        AbstractEfestoInput efestoInput = null;
        ModelLocalUriId modelLocalUriId = getModelLocalUriId(jsonNode);
        if (modelLocalUriId instanceof AbstractModelLocalUriIdPmml) {
            efestoInput = getPMMLEfestoInput((AbstractModelLocalUriIdPmml) modelLocalUriId, inputData);
        } else {
            logger.warn("{} not managed", modelLocalUriId);
            return null;
        }

        EfestoRuntimeContext runtimeContext = getEfestoRuntimeContext(modelLocalUriId);
        try {
            Collection<EfestoOutput> retrieved = runtimeManager.evaluateInput(runtimeContext, efestoInput);
            if (retrieved.isEmpty()) {
                throw new KieEfestoCommonException("Failed to retrieve result for modelLocalUriId");
            } else {
                return retrieved.iterator().next();
            }
        } catch (Exception t) {
            String errorMessage = String.format("Evaluation error for %s@%s using %s due to %s: please %s",
                                                efestoInput.getModelLocalUriId(),
                                                efestoInput.getInputData(),
                                                efestoInput,
                                                t.getMessage(),
                                                CHECK_CLASSPATH);
            logger.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage, t);
        }
    }

    private EfestoRuntimeManager() {
    }

//    static ModelLocalUriId getModelLocalUriId(JsonNode jsonNode) throws JsonProcessingException {
//        String modelLocalUriIdString = jsonNode.get("modelLocalUriIdString").asText();
//        ModelLocalUriId toReturn = objectMapper.readValue(modelLocalUriIdString, ModelLocalUriId.class);
//        if (toReturn.model().equals("pmml")) {
//            toReturn = objectMapper.readValue(modelLocalUriIdString, AbstractModelLocalUriIdPmml.class);
//        }
//        return toReturn;
//    }

    private static EfestoRuntimeContext getEfestoRuntimeContext(ModelLocalUriId modelLocalUriId) {
        EfestoRuntimeContext toReturn = ContextStorage.getEfestoRuntimeContext(modelLocalUriId);
        if (toReturn == null) {
            toReturn = instantiateRuntimeContextFromCompilationContext(modelLocalUriId);
        }
        return toReturn;
    }

    private static EfestoRuntimeContext instantiateRuntimeContextFromCompilationContext(ModelLocalUriId modelLocalUriId) {
        EfestoCompilationContextImpl compilationContext = ContextStorage.getEfestoCompilationContext(modelLocalUriId);
        if (compilationContext == null) {
            String errorMessage = String.format("Failed to retrieve EfestoCompilationContext for %s", modelLocalUriId);
            logger.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage);
        }
        Map<String, byte[]> generatedClasses = compilationContext.getGeneratedClasses(modelLocalUriId);
        if (generatedClasses == null) {
            String errorMessage = String.format("Failed to retrieve generatedClasses for %s", modelLocalUriId);
            logger.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage);
        }
        String model = modelLocalUriId.model();
        GeneratedResources generatedResources =
                (GeneratedResources) compilationContext.getGeneratedResourcesMap().get(model);
        if (generatedResources == null) {
            String errorMessage = String.format("Failed to retrieve GeneratedResources for %s", modelLocalUriId);
            logger.error(errorMessage);
            throw new KieRuntimeServiceException(errorMessage);
        }
        EfestoRuntimeContextImpl toReturn =
                (EfestoRuntimeContextImpl) EfestoRuntimeContext.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        toReturn.addGeneratedClasses(modelLocalUriId, generatedClasses);
        toReturn.getGeneratedResourcesMap().put(model, generatedResources);
        ContextStorage.putEfestoRuntimeContext(modelLocalUriId, toReturn);
        return toReturn;
    }

    private static AbstractEfestoInput getPMMLEfestoInput(AbstractModelLocalUriIdPmml modelLocalUriId,
                                                          Map<String, Object> inputData) {
        PMMLRequestData pmmlRequestData = getPMMLRequestData(UUID.randomUUID().toString(),
                                                             modelLocalUriId,
                                                             inputData);
        return new AbstractEfestoInput<>(modelLocalUriId, pmmlRequestData) {
        };
    }

    private static PMMLRequestData getPMMLRequestData(String correlationId, AbstractModelLocalUriIdPmml modelLocalUriId,
                                                      Map<String, Object> inputData) {
        String fileName = modelLocalUriId.getFileName();
        String modelName = modelLocalUriId.name();
        PMMLRequestData toReturn = new PMMLRequestData(correlationId, modelName);

        inputData.forEach(toReturn::addRequestParam);
        toReturn.addRequestParam("_pmml_file_name_", fileName);
        return toReturn;
    }
}

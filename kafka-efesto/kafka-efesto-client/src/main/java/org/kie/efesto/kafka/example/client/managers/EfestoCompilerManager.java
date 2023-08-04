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
package org.kie.efesto.kafka.example.client.managers;

import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedRedirectResource;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.common.api.utils.CollectionUtils;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EfestoCompilerManager {

    private static final CompilationManager compilationManager;

    private static final Logger logger = LoggerFactory.getLogger(EfestoCompilerManager.class);

    static {
        compilationManager = SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve CompilationManager"));
    }

    public static ModelLocalUriId compileModel(String toCompile, String fileName) {
        EfestoInputStreamResource efestoResource = new EfestoInputStreamResource(new ByteArrayInputStream(toCompile.getBytes(StandardCharsets.UTF_8)),
                fileName);
        EfestoCompilationContextImpl compilationContext = (EfestoCompilationContextImpl) EfestoCompilationContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        try {
            compilationManager.processResource(compilationContext, efestoResource);
            ModelLocalUriId toReturn = getModelLocalUriIdFromGeneratedResourcesMap(compilationContext.getGeneratedResourcesMap(), efestoResource.getModelType());
            ContextStorage.putEfestoCompilationContext(toReturn, compilationContext);
            //compilationContext.createIndexFiles(Paths.get("kafka-efesto/kafka-efesto-client/target/classes")).values();
            return toReturn;
        } catch (Exception e) {
            logger.error("Failed to process {}", fileName, e);
            throw new KieEfestoCommonException(e);
        }
    }

    static ModelLocalUriId getModelLocalUriIdFromGeneratedResourcesMap(Map<String, GeneratedResources> generatedResourcesMap, String modelType) {
        List<GeneratedResource> generatedResources = generatedResourcesMap.get(modelType).stream()
                .collect(Collectors.toList());
        GeneratedResource generatedResource = CollectionUtils.findAtMostOne(generatedResources,
                        resource -> resource instanceof GeneratedExecutableResource || resource instanceof GeneratedRedirectResource,
                        (s1, s2) -> new KieCompilerServiceException("Found more than one GeneratedExecutableResource or GeneratedRedirectResource: " + s1 + " and " + s2))
                .orElseThrow(() -> new KieCompilerServiceException("Failed to retrieve a GeneratedExecutableResource or GeneratedRedirectResource"));
        return generatedResource instanceof GeneratedExecutableResource ? ((GeneratedExecutableResource)generatedResource).getModelLocalUriId() :
                ((GeneratedRedirectResource)generatedResource).getModelLocalUriId();
    }


}

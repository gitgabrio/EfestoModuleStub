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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.efesto.common.api.exceptions.KieEfestoCommonException;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.model.GeneratedExecutableResource;
import org.kie.efesto.common.api.model.GeneratedResource;
import org.kie.efesto.common.api.model.GeneratedResources;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextUtils;
import org.kie.efesto.kafka.example.server.storage.ContextStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.utils.CollectionUtils.findAtMostOne;

public class EfestoCompilerManager {

    private static final CompilationManager compilationManager;

    private static final Logger logger = LoggerFactory.getLogger(ServerCompileConsumer.class);

    static {
        compilationManager = SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve CompilationManager"));
    }

    public static ModelLocalUriId compileModel(String toCompile, String fileName) {
        EfestoInputStreamResource efestoResource = new EfestoInputStreamResource(new ByteArrayInputStream(toCompile.getBytes(StandardCharsets.UTF_8)),
                                                                      fileName);
        EfestoCompilationContextImpl compilationContext = (EfestoCompilationContextImpl) EfestoCompilationContextUtils.buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        try {
            compilationManager.processResource(compilationContext, efestoResource);
            ModelLocalUriId toReturn = getModelLocalUriIdFromGeneratedResourcesMap(compilationContext.getGeneratedResourcesMap());
            ContextStorage.putEfestoCompilationContext(toReturn, compilationContext);
            return toReturn;
        } catch (Exception e) {
            logger.error("Failed to process {}", fileName, e);
            throw new KieEfestoCommonException(e);
        }

    }

    static ModelLocalUriId getModelLocalUriIdFromGeneratedResourcesMap(Map<String, GeneratedResources> generatedResourcesMap) {
        List<GeneratedResource> generatedResources =
                generatedResourcesMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());

        GeneratedExecutableResource generatedExecutableResource = findAtMostOne(generatedResources,
                                                                                generatedResource -> generatedResource instanceof GeneratedExecutableResource,
                                                                                (s1, s2) -> new KieCompilerServiceException("Found more than one GeneratedExecutableResource: " + s1 + " and " + s2))
                .map(GeneratedExecutableResource.class::cast)
                .orElseThrow(() -> new KieCompilerServiceException("Failed to retrieve a GeneratedExecutableResource"));
        return generatedExecutableResource.getModelLocalUriId();
    }


}

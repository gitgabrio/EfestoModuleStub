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
package org.kie.efesto.kafka.example.server.storage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.efesto.runtimemanager.core.model.EfestoRuntimeContextImpl;

public class ContextStorage {

    private static final Map<ModelLocalUriId, EfestoCompilationContextImpl> COMPILATION_CONTEXT_MAP = new HashMap<>();
    private static final Map<ModelLocalUriId, EfestoRuntimeContextImpl> RUNTIME_CONTEXT_MAP = new HashMap<>();


    public static void putEfestoCompilationContext(ModelLocalUriId modelLocalUriId, EfestoCompilationContextImpl compilationContext) {
        COMPILATION_CONTEXT_MAP.put(modelLocalUriId, compilationContext);
    }

    public static EfestoCompilationContextImpl getEfestoCompilationContext(ModelLocalUriId modelLocalUriId) {
       return  COMPILATION_CONTEXT_MAP.get(modelLocalUriId);
    }

    public static Collection<ModelLocalUriId> getAllModelLocalUriId() {
        return COMPILATION_CONTEXT_MAP.keySet();
    }

    public static void putEfestoRuntimeContext(ModelLocalUriId modelLocalUriId, EfestoRuntimeContextImpl runtimeContext) {
        RUNTIME_CONTEXT_MAP.put(modelLocalUriId, runtimeContext);
    }

    public static EfestoRuntimeContextImpl getEfestoRuntimeContext(ModelLocalUriId modelLocalUriId) {
        return RUNTIME_CONTEXT_MAP.get(modelLocalUriId);
    }

}

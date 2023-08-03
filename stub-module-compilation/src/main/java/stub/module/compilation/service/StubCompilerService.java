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
package stub.module.compilation.service;

import org.kie.efesto.common.api.identifiers.ReflectiveAppRoot;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import stub.module.api.ExecutorA;
import stub.module.api.ExecutorB;
import stub.module.api.StubExecutor;
import stub.module.api.identifiers.LocalComponentIdStub;
import stub.module.api.identifiers.StubIdFactory;
import stub.module.compilation.model.StubCallableOutput;
import stub.module.compilation.model.StubResource;

import java.util.Collections;
import java.util.List;

import static stub.module.api.CommonConstants.STUB_MODEL_TYPE;

public class StubCompilerService implements KieCompilerService<EfestoCompilationOutput, EfestoCompilationContext> {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof StubResource;
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException("Unexpected EfestoResource " + toProcess.getClass());
        }
        return Collections.singletonList(getEfestoCompilationOutput((StubResource) toProcess));
    }

    @Override
    public String getModelType() {
        return STUB_MODEL_TYPE;
    }

    private EfestoCompilationOutput getEfestoCompilationOutput(StubResource resource) {
        String content = resource.getContent();
        boolean even = !content.isEmpty() && content.length() % 2 == 0;
        return getStubCallableOutput(even);
    }

    private StubCallableOutput getStubCallableOutput(boolean even) {
        LocalComponentIdStub modelLocalUriId;
        Class<? extends StubExecutor> executor;
        if (even) {
            modelLocalUriId = new ReflectiveAppRoot("")
                    .get(StubIdFactory.class)
                    .get("EventA");
            executor = ExecutorA.class;
        } else {
            modelLocalUriId = new ReflectiveAppRoot("")
                    .get(StubIdFactory.class)
                    .get("EventB");
            executor = ExecutorB.class;
        }
        return new StubCallableOutput(modelLocalUriId, executor.getCanonicalName());
    }

}

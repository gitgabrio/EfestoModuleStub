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

import java.util.Collections;
import java.util.List;

import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContextImpl;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import stub.module.compilation.model.JDrlCompilationContext;

import static stub.module.api.CommonConstants.MODEL_TYPE;
import static stub.module.compilation.utils.TranslatorUtils.resourceToCompilationOutputFunction;

public class JDrlCompilerService implements KieCompilerService<EfestoCompilationOutput, EfestoCompilationContext> {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof EfestoInputStreamResource && ((EfestoInputStreamResource) toProcess).getModelType().equalsIgnoreCase(MODEL_TYPE);
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException("Unexpected EfestoResource " + toProcess.getClass());
        }
        if (!(context instanceof JDrlCompilationContext)) {
            context = JDrlCompilationContext.buildWithEfestoCompilationContext((EfestoCompilationContextImpl) context);
        }
        return Collections.singletonList(resourceToCompilationOutputFunction.apply((EfestoInputStreamResource) toProcess,
                                                                                   (JDrlCompilationContext) context));
    }

    @Override
    public String getModelType() {
        return MODEL_TYPE;
    }

}

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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.io.ByteArrayResource;
import org.kie.api.io.Resource;
import org.kie.efesto.common.api.identifiers.ReflectiveAppRoot;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;
import org.kie.efesto.compilationmanager.core.model.EfestoCompilationContextImpl;
import org.kie.internal.conf.CompositeConfiguration;
import stub.module.api.identifiers.JdrlIdFactory;
import stub.module.api.identifiers.LocalComponentIdJdrl;
import stub.module.compilation.model.EfestoRedirectOutputJDrl;
import stub.module.compilation.model.JDRL;
import stub.module.compilation.model.JDrlCompilationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static stub.module.api.CommonConstants.JDRL_MODEL_TYPE;
import static stub.module.compilation.utils.JDRLUtils.getDrlString;
import static stub.module.compilation.utils.JSONUtils.getJDRLObject;

public class JdrlCompilerService implements KieCompilerService<EfestoCompilationOutput, EfestoCompilationContext> {

    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof EfestoInputStreamResource && ((EfestoInputStreamResource) toProcess).getModelType().equalsIgnoreCase(JDRL_MODEL_TYPE);
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException("Unexpected EfestoResource " + toProcess.getClass());
        }
        if (!(context instanceof JDrlCompilationContext)) {
            context = JDrlCompilationContext.buildWithEfestoCompilationContext((EfestoCompilationContextImpl) context);
        }
        return Collections.singletonList(getEfestoCompilationOutput((EfestoInputStreamResource) toProcess,
                (JDrlCompilationContext) context));
    }

    @Override
    public String getModelType() {
        return JDRL_MODEL_TYPE;
    }

    EfestoCompilationOutput getEfestoCompilationOutput(EfestoInputStreamResource resource,
                                                       JDrlCompilationContext context) {
        String content = new BufferedReader(new InputStreamReader(resource.getContent()))
                .lines().collect(Collectors.joining("\n"));
        return getEfestoRedirectOutputJDrl(resource.getFileName(), content, context);
    }

    static EfestoRedirectOutputJDrl getEfestoRedirectOutputJDrl(String fileName, String content, JDrlCompilationContext context) {
        try {
            JDRL jdrl = getJDRLObject(content);
            return getEfestoRedirectOutputJDrl(fileName, jdrl, context);
        } catch (JsonProcessingException e) {
            throw new KieCompilerServiceException(e);
        }
    }

    static EfestoRedirectOutputJDrl getEfestoRedirectOutputJDrl(String fileName, JDRL jdrl, JDrlCompilationContext context) {
        String drlString = getDrlString(jdrl);
        CompositeConfiguration compositeConfiguration = (CompositeConfiguration) context.newKnowledgeBuilderConfiguration();
        KnowledgeBuilderConfigurationImpl conf = new KnowledgeBuilderConfigurationImpl(compositeConfiguration);
        DrlResourceHandler drlResourceHandler = new DrlResourceHandler(conf);
        Resource resource = new ByteArrayResource(drlString.getBytes(StandardCharsets.UTF_8));
        PackageDescr packageDescr = resourceToPackageDescr(drlResourceHandler, resource);
        String cleanedFileName = fileName.contains(".") ? fileName.substring(0, fileName.indexOf('.')) : fileName;
        LocalComponentIdJdrl modelLocalUriId = new ReflectiveAppRoot("")
                .get(JdrlIdFactory.class)
                .get(cleanedFileName);
        return new EfestoRedirectOutputJDrl(modelLocalUriId, Collections.singleton(packageDescr));
    }

    private static PackageDescr resourceToPackageDescr(DrlResourceHandler drlResourceHandler, Resource resource) {
        try {
            return drlResourceHandler.process(resource);
        } catch (DroolsParserException | IOException e) {
            throw new KieCompilerServiceException(e);
        }
    }
}

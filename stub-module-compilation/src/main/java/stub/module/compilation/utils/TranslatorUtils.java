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
package stub.module.compilation.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.io.ByteArrayResource;
import org.kie.api.io.Resource;
import org.kie.efesto.common.api.identifiers.ReflectiveAppRoot;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import stub.module.api.identifiers.JdrlIdFactory;
import stub.module.api.identifiers.LocalComponentIdJdrl;
import stub.module.compilation.model.EfestoRedirectOutputJDrl;
import stub.module.compilation.model.JDRL;
import stub.module.compilation.model.JDrlCompilationContext;

import static stub.module.compilation.utils.JDRLUtils.getDrlString;
import static stub.module.compilation.utils.JSONUtils.getJDRLObject;

public class TranslatorUtils {

    private static final Function<String, Resource> jdrlContentToResourceFunction = (content) -> {
        try {
            JDRL jdrl = getJDRLObject(content);
            String drlString = getDrlString(jdrl);
            return new ByteArrayResource(drlString.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new KieCompilerServiceException(e);
        }
    };

    private static final BiFunction<JDrlCompilationContext, Resource, PackageDescr> resourceToPackageDescrFunction =
            (context, resource) -> {
                try {
                    KnowledgeBuilderConfigurationImpl conf =
                            (KnowledgeBuilderConfigurationImpl) context.newKnowledgeBuilderConfiguration();
                    DrlResourceHandler drlResourceHandler = new DrlResourceHandler(conf);
                    return drlResourceHandler.process(resource);
                } catch (DroolsParserException | IOException e) {
                    throw new KieCompilerServiceException(e);
                }
            };

    private static final BiFunction<String, PackageDescr, EfestoRedirectOutputJDrl> packageDescrToRedirectOutputFunction =
            (fileName, packageDescr) -> {
                String cleanedFileName = fileName.contains(".") ? fileName.substring(0, fileName.indexOf('.')) :
                        fileName;
                LocalComponentIdJdrl modelLocalUriId = new ReflectiveAppRoot("")
                        .get(JdrlIdFactory.class)
                        .get(cleanedFileName);
                return new EfestoRedirectOutputJDrl(modelLocalUriId, packageDescr);
            };

    public static BiFunction<EfestoInputStreamResource, JDrlCompilationContext, EfestoCompilationOutput> resourceToCompilationOutputFunction = (resource, context) -> {
        String content = new BufferedReader(new InputStreamReader(resource.getContent()))
                .lines().collect(Collectors.joining("\n"));
        Resource jdrlResource = jdrlContentToResourceFunction.apply(content);
        PackageDescr packageDescr = resourceToPackageDescrFunction.apply(context, jdrlResource);
        return packageDescrToRedirectOutputFunction.apply(resource.getFileName(), packageDescr);
    };
}

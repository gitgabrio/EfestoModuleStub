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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.drools.drl.ast.descr.PackageDescr;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoInputStreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stub.module.compilation.model.EfestoCallableOutputJDrl;
import stub.module.compilation.model.EfestoRedirectOutputJDrl;
import stub.module.compilation.model.JDrlCompilationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileName;

class TranslatorUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(TranslatorUtilsTest.class);
    private static final String fileName = "LoanRules.jdrl";
    private static File jdrlFile;

    @BeforeAll
    static void setUp() {
        jdrlFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get jdrlFile"));
    }

    @Test
    void resourceToCompilationOutputFunction() throws IOException {
        JDrlCompilationContext compilationContext = JDrlCompilationContext
                .buildWithParentClassLoader(Thread.currentThread().getContextClassLoader());
        EfestoInputStreamResource efestoResource =
                new EfestoInputStreamResource(Files.newInputStream(jdrlFile.toPath()),
                                                                                 fileName);

        List<EfestoCompilationOutput> retrieved = TranslatorUtils.resourceToCompilationOutputFunction.apply(efestoResource,
                                                                                                            compilationContext);
        assertThat(retrieved).isNotNull();
        Optional<EfestoRedirectOutputJDrl> redirect = retrieved.stream().filter(out -> out instanceof EfestoRedirectOutputJDrl)
                .map(EfestoRedirectOutputJDrl.class::cast)
                .findFirst();
        assertThat(redirect).isPresent();
        EfestoRedirectOutputJDrl efestoRedirectOutputJDrl = redirect.get();
        assertThat(efestoRedirectOutputJDrl.getContent()).isNotNull();
        Set<PackageDescr> packageDescrs = efestoRedirectOutputJDrl.getContent();
        assertThat(packageDescrs.size()).isEqualTo(1);
        assertThat(efestoRedirectOutputJDrl.getModelLocalUriId().model()).isEqualTo("drl");
        Optional<EfestoCallableOutputJDrl> callable = retrieved.stream().filter(out -> out instanceof EfestoCallableOutputJDrl)
                .map(EfestoCallableOutputJDrl.class::cast)
                .findFirst();
        assertThat(callable).isPresent();
        EfestoCallableOutputJDrl efestoCallableOutputJDrl = callable.get();
        assertThat(efestoCallableOutputJDrl.getModelLocalUriId().model()).isEqualTo("jdrl");

    }
}
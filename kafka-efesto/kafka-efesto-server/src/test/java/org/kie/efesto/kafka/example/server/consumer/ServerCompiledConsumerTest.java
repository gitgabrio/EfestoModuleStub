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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.kafka.example.server.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;
import static org.kie.efesto.kafka.example.server.Main.compileJdrl;
import static org.kie.efesto.kafka.example.server.consumer.EfestoCompilerManager.compileModel;
import static org.kie.efesto.kafka.example.server.consumer.ServerCompileConsumer.startCompileConsumer;
import static org.kie.efesto.kafka.example.server.consumer.ServerCompiledConsumer.startCompiledConsumer;
import static org.kie.efesto.kafka.example.server.consumer.ServerEvaluateConsumer.startEvaluateConsumer;

class ServerCompiledConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(ServerCompiledConsumerTest.class);

    private static final String fileName = "LoanApprovalRegression.pmml";

    public static void main(String... args) throws Exception {
        compileJdrl();
        compilePmml();
        startCompiledConsumer();

    }


    public static void compilePmml() throws IOException {
        File pmmlFile = getFileFromFileName(fileName).orElseThrow(() -> new RuntimeException("Failed to get " +
                                                                                                         "pmmlFile"));
        String toCompile = Files.readString(pmmlFile.toPath());
        ModelLocalUriId retrieved = compileModel(toCompile, fileName);
        logger.info("ModelLocalUriId for jdrl {}", retrieved );
    }
}
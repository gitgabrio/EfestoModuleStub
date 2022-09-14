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
package org.kie.efesto.kafka.example.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;

import static org.kie.efesto.common.api.utils.FileUtils.getFileFromFileName;
import static org.kie.efesto.kafka.example.client.consumer.ClientCompiledConsumer.startCompiledConsumer;
import static org.kie.efesto.kafka.example.client.consumer.ClientEvaluatedConsumer.startEvaluatedConsumer;
import static org.kie.efesto.kafka.example.client.producer.ClientCompileProducer.runProducer;
import static org.kie.efesto.kafka.example.server.consumer.ServerCompileConsumer.startCompileConsumer;
import static org.kie.efesto.kafka.example.server.consumer.ServerEvaluateConsumer.startEvaluateConsumer;

public class App {

    private static final AtomicLong counter = new AtomicLong(10000000);
    private static final String pmmlCompilationFileName = "PmmlCompilationMessage.json";

    public static void main(String... args) throws Exception {
        startCompileConsumer();
        startEvaluateConsumer();
        startCompiledConsumer();
        startEvaluatedConsumer();
        compileAndExecutePmml();
    }

    static void compileAndExecutePmml() throws IOException {
        File pmmlCompilationFile = getFileFromFileName(pmmlCompilationFileName).orElseThrow(() -> new RuntimeException("Failed to get pmmlCompilationFile"));;
        String pmmlCompilationJson = Files.readString(pmmlCompilationFile.toPath());
        runProducer(counter.getAndAdd(1), pmmlCompilationJson);
    }




}

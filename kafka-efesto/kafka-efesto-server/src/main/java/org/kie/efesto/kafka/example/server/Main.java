package org.kie.efesto.kafka.example.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.efesto.common.api.utils.MemoryFileUtils.getFileFromFileName;
import static org.kie.efesto.kafka.example.server.consumer.EfestoCompilerManager.compileModel;
import static org.kie.efesto.kafka.example.server.consumer.ServerCompileConsumer.startCompileConsumer;
import static org.kie.efesto.kafka.example.server.consumer.ServerCompiledConsumer.startCompiledConsumer;
import static org.kie.efesto.kafka.example.server.consumer.ServerEvaluateConsumer.startEvaluateConsumer;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String fileName = "LoanRules.jdrl";
    private static final String fullFilePath = "org/drools/example/" + fileName;

    public static void main(String[] args) throws Exception {
        compileJdrl();
        startCompileConsumer();
        startEvaluateConsumer();
        startCompiledConsumer();
    }

    public static void compileJdrl() throws IOException {
        File jdrlFile = getFileFromFileName(fullFilePath).orElseThrow(() -> new RuntimeException("Failed to get " +
                                                                                                         "jdrlFile"));
        String toCompile = Files.readString(jdrlFile.toPath());
        ModelLocalUriId retrieved = compileModel(toCompile, fileName);
        logger.info("ModelLocalUriId for jdrl {}", retrieved );
    }
}
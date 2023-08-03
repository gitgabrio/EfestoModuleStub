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
package org.kie.efesto.kafka.example.client.managers;

import org.kie.efesto.kafka.api.service.KafkaKieRuntimeService;
import org.kie.efesto.kafka.api.utils.KafkaSPIUtils;
import org.kie.efesto.kafka.runtime.services.consumer.KieServicesDiscoverConsumer;
import org.kie.efesto.kafka.runtime.services.service.KafkaRuntimeServiceLocalProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class EfestoRuntimeManager {

    private static final Logger logger = LoggerFactory.getLogger(EfestoRuntimeManager.class);

    private static List<KafkaKieRuntimeService> KIERUNTIMESERVICES;

    public static void startRuntime() {
        logger.info("Strating KieServicesDiscoverConsumer...");
        KieServicesDiscoverConsumer.startEvaluateConsumer();
        KafkaRuntimeServiceLocalProvider runtimeServiceProvider = KafkaSPIUtils.getRuntimeServiceProviders(true)
                .stream()
                .filter(serviceProvider -> serviceProvider instanceof KafkaRuntimeServiceLocalProvider)
                .findFirst()
                .map(KafkaRuntimeServiceLocalProvider.class::cast)
                .orElseThrow(() -> new RuntimeException("Failed to find KafkaRuntimeServiceLocalProvider"));
        KIERUNTIMESERVICES = runtimeServiceProvider.getKieRuntimeServices()
                .stream()
                .filter(KafkaKieRuntimeService.class::isInstance)
                .map(KafkaKieRuntimeService.class::cast)
                .collect(Collectors.toList());
        logger.info("Discovered {} KieRuntimeServices", KIERUNTIMESERVICES.size());
        KIERUNTIMESERVICES.forEach(kieRuntimeService -> logger.info("Discovered {}", kieRuntimeService));
    }
}

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
package stub.module.runtime.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoInputDrlKieSessionLocal;
import org.kie.drl.engine.runtime.kiesession.local.model.EfestoOutputDrlKieSessionLocal;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.exceptions.KieRuntimeServiceException;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.efesto.runtimemanager.api.utils.SPIUtils;
import stub.module.runtime.model.JdrlInput;
import stub.module.runtime.model.StubOutput;

import static org.kie.efesto.common.api.constants.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;
import static stub.module.api.CommonConstants.MODEL_TYPE;

public class TranslatorUtils {

    static Function<String, JSONObject> stringToJsonNodeFunction = JSONObject::new;

    static final RuntimeManager runtimeManager =
            SPIUtils.getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve " +
                                                                                             "RuntimeManager"));

    static BiConsumer<Object, KieSession> insertInKieSessionConsumer = (toAdd, kieSession) -> {
        kieSession.insert(toAdd);
    };

    static List<Object> getLoanApplicationsfromPopulateKieSession(JSONObject jsonObject, KieSession kieSession) throws InstantiationException,
            IllegalAccessException {
        FactType loanApplicationFactType = factTypeFromKieSessionFunction.apply("org.drools.example.LoanApplication",
                                                                                kieSession);
        FactType applicantFactType = factTypeFromKieSessionFunction.apply("org.drools.example.Applicant", kieSession);
        Integer maxAmount = (Integer) jsonObject.get("maxAmount");
        kieSession.setGlobal("maxAmount", maxAmount);
        JSONArray loanApplications = jsonObject.getJSONArray("loanApplications");
        List<Object> toReturn = new ArrayList<>();
        for (int i = 0; i < loanApplications.length(); i++) {
            JSONObject loanApplicationJson = loanApplications.getJSONObject(i);
            Object applicant = applicantFactType.newInstance();
            JSONObject applicantJson = loanApplicationJson.getJSONObject("applicant");
            String name = (String) applicantJson.get("name");
            applicantFactType.set(applicant, "name", name);
            Integer age = (Integer) applicantJson.get("age");
            applicantFactType.set(applicant, "age", age);

            Object loanApplication = loanApplicationFactType.newInstance();
            loanApplicationFactType.set(loanApplication, "applicant", applicant);

            Integer amount = (Integer) loanApplicationJson.get("amount");
            loanApplicationFactType.set(loanApplication, "amount", amount);
            Integer deposit = (Integer) loanApplicationJson.get("deposit");
            loanApplicationFactType.set(loanApplication, "deposit", deposit);
            String id = (String) loanApplicationJson.get("id");
            loanApplicationFactType.set(loanApplication, "id", id);
            kieSession.insert(loanApplication);
            toReturn.add(loanApplication);
        }
        return toReturn;
    }

    static BiFunction<String, KieSession, FactType> factTypeFromKieSessionFunction = (fqdn, kieSession) -> {
        String packageName = fqdn.substring(0, fqdn.lastIndexOf('.'));
        String generatedTypeName = fqdn.substring(fqdn.lastIndexOf('.') + 1);
        FactType factType = kieSession.getKieBase().getFactType(packageName, generatedTypeName);
        if (factType == null) {
            String name = String.format(PACKAGE_CLASS_TEMPLATE, packageName, generatedTypeName);
            String error = String.format("Failed to retrieve FactType %s for input value %s", name, fqdn);
            throw new KieRuntimeServiceException(error);
        }
        return factType;
    };

    static BiFunction<EfestoInputDrlKieSessionLocal, EfestoRuntimeContext, KieSession> efestoInputToKieSessionFunction = (efestoInput, runtimeContext) -> {
        Collection<EfestoOutput> output = runtimeManager.evaluateInput(runtimeContext, efestoInput);
        if (output == null || output.isEmpty()) {
            throw new KieRuntimeServiceException("Failed to retrieve EfestoOutput for " + efestoInput.getModelLocalUriId());
        }
        EfestoOutputDrlKieSessionLocal efestoOutputDrlKieSessionLocal =
                (EfestoOutputDrlKieSessionLocal) output.iterator().next();
        return efestoOutputDrlKieSessionLocal.getOutputData();
    };

    static BiFunction<JdrlInput, EfestoRuntimeContext, KieSession> jdrlInputToKieSessionFunction = (jdrlInput,
                                                                                                    runtimeContext) -> {
        String path = SLASH + "drl" + jdrlInput.getModelLocalUriId().basePath();
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(parsed);
        EfestoInputDrlKieSessionLocal toEvaluate = new EfestoInputDrlKieSessionLocal(modelLocalUriId,
                                                                                     "");
        return efestoInputToKieSessionFunction.apply(toEvaluate, runtimeContext);
    };

    public static BiFunction<JdrlInput, EfestoRuntimeContext, Optional<StubOutput>> jdrlInputToStubOutputFunction =
            (jdrlInput, runtimeContext) -> {
                KieSession kieSession = jdrlInputToKieSessionFunction.apply(jdrlInput, runtimeContext);
                JSONObject object = stringToJsonNodeFunction.apply(jdrlInput.getInputData());
                try {
                    List<Object> loanApplications = getLoanApplicationsfromPopulateKieSession(object, kieSession);
                    kieSession.fireAllRules();
                    String payLoad =  new ObjectMapper().writeValueAsString(loanApplications);
                    return Optional.of(new StubOutput(jdrlInput.getModelLocalUriId(), payLoad));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
}

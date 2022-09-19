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

import stub.module.compilation.model.DeclaredType;
import stub.module.compilation.model.DeclaredTypeField;
import stub.module.compilation.model.Global;
import stub.module.compilation.model.JDRL;
import stub.module.compilation.model.Rule;

public class JDRLUtils {

    public static String getDrlString(JDRL jdrl) {
        StringBuilder builder = new StringBuilder();
        builder.append(getPackage(jdrl)).append("\r\n");
        builder.append(getGlobals(jdrl)).append("\r\n");
        builder.append(getDeclaredTypes(jdrl)).append("\r\n");
        builder.append(getRules(jdrl)).append("\r\n");
        return builder.toString();
    }

    private static String getPackage(JDRL jdrl) {
        String toReturn = String.format("package %s", jdrl.getPackageName());
        return addSemiColon(toReturn);
    }

    private static String getImports(JDRL jdrl) {
        StringBuilder builder = new StringBuilder();
        jdrl.getImports().forEach(importString -> builder.append(getImport(importString)).append("\r\n"));
        return builder.toString();
    }

    private static String getImport(String importString) {
        String toReturn = String.format("import %s", importString);
        return addSemiColon(toReturn);
    }

    private static String getGlobals(JDRL jdrl) {
        StringBuilder builder = new StringBuilder();
        if (jdrl.getGlobals() != null) {
            jdrl.getGlobals().forEach(globalString -> builder.append(getGlobal(globalString)).append("\r\n"));
        }
        return builder.toString();
    }

    private static String getGlobal(Global global) {
        String toReturn = String.format("global %s %s", global.getType(), global.getName());
        return addSemiColon(toReturn);
    }

    private static String getDeclaredTypes(JDRL jdrl) {
        StringBuilder builder = new StringBuilder();
        if (jdrl.getDeclaredTypes() != null) {
            jdrl.getDeclaredTypes().forEach(declaredType -> builder.append(getDeclaredType(declaredType)).append("\r\n"));
        }
        return builder.toString();
    }

    private static String getDeclaredType(DeclaredType declaredType) {
        StringBuilder builder = new StringBuilder(String.format("declare %s\r\n", declaredType.getName()));
        if (declaredType.getFields() != null) {
            declaredType.getFields().forEach(declaredTypeField -> builder.append(getDeclaredTypeField(declaredTypeField)).append("\r\n"));
        }
        builder.append("end\r\n");
        return builder.toString();
    }

    private static String getDeclaredTypeField(DeclaredTypeField declaredType) {
        return String.format("%s : %s\r\n", declaredType.getName(), declaredType.getType());
    }

    private static String getRules(JDRL jdrl) {
        StringBuilder builder = new StringBuilder();
        if (jdrl.getRules() != null) {
            jdrl.getRules().forEach(rule -> builder.append(getRule(rule)).append("\r\n"));
        }
        return builder.toString();
    }

    private static String getRule(Rule rule) {
        return String.format("rule %s when\n" +
                                     "    %s\n" +
                                     "then\n" +
                                     "    %s\n" +
                                     "end", rule.getName(), rule.getWhen(), rule.getThen());
    }

    private static String addSemiColon(String toComplete) {
        if (!toComplete.endsWith(";")) {
            toComplete += ";";
        }
        return toComplete;
    }
}

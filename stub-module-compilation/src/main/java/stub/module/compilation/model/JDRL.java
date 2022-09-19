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
package stub.module.compilation.model;

import java.util.List;

public class JDRL {

    private String packageName;

    private List<String> imports;

    private List<Global> globals;

    private List<DeclaredType> declaredTypes;

    private List<Rule> rules;

    private List<Query> queries;

    public JDRL() {
    }

    public JDRL(String packageName, List<String> imports, List<Global> globals, List<DeclaredType> declaredTypes, List<Rule> rules, List<Query> queries) {
        this.packageName = packageName;
        this.imports = imports;
        this.globals = globals;
        this.declaredTypes = declaredTypes;
        this.rules = rules;
        this.queries = queries;
    }

    public String getPackageName() {
        return packageName;
    }


    public List<String> getImports() {
        return imports;
    }

    public List<Global> getGlobals() {
        return globals;
    }

    public List<DeclaredType> getDeclaredTypes() {
        return declaredTypes;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public List<Query> getQueries() {
        return queries;
    }

    @Override
    public String toString() {
        return "JDRL{" +
                "packageName='" + packageName + '\'' +
                ", imports=" + imports +
                ", globals=" + globals +
                ", rules=" + rules +
                ", queries=" + queries +
                '}';
    }
}

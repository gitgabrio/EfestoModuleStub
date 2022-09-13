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

public class Rule {

    private String name;

    private String when;

    private String then;

    public Rule(String name, String when, String then) {
        this.name = name;
        this.when = when;
        this.then = then;
    }

    public Rule() {

    }

    public String getName() {
        return name;
    }

    public String getWhen() {
        return when;
    }

    public String getThen() {
        return then;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "name='" + name + '\'' +
                ", when='" + when + '\'' +
                ", then='" + then + '\'' +
                '}';
    }
}

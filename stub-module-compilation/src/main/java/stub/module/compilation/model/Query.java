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

public class Query {

    private String name;

    private String query;

    public Query(String name, String query) {
        this.name = name;
        this.query = query;
    }

    public Query() {

    }

    public String getName() {
        return name;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "Query{" +
                "name='" + name + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}

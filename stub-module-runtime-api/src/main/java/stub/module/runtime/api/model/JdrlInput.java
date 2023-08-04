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
package stub.module.runtime.api.model;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.BaseEfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoMapInputDTO;

import java.util.Objects;

public class JdrlInput extends BaseEfestoInput<EfestoMapInputDTO> {


    public JdrlInput(ModelLocalUriId modelLocalUriId, EfestoMapInputDTO inputData) {
        super(modelLocalUriId, inputData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModelLocalUriId(), getInputData());
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof JdrlInput)) {
            return false;
        }
        JdrlInput that = (JdrlInput) obj;
        return Objects.equals(getModelLocalUriId(), that.getModelLocalUriId()) && Objects.equals(getInputData(), that.getInputData());
    }
}

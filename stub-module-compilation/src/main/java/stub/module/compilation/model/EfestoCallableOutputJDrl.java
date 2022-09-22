package stub.module.compilation.model;

import java.util.Collections;
import java.util.List;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCallableOutput;

public class EfestoCallableOutputJDrl implements EfestoCallableOutput {

    private final ModelLocalUriId modelLocalUriId;

    public EfestoCallableOutputJDrl(ModelLocalUriId modelLocalUriId) {
        this.modelLocalUriId = modelLocalUriId;
    }

    @Override
    public ModelLocalUriId getModelLocalUriId() {
        return modelLocalUriId;
    }

    @Override
    public List<String> getFullClassNames() {
        return Collections.emptyList();
    }
}
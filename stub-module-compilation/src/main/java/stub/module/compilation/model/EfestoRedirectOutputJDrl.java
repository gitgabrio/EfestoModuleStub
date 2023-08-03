package stub.module.compilation.model;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoRedirectOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

import java.util.List;
import java.util.Set;

public class EfestoRedirectOutputJDrl extends EfestoRedirectOutput<Set<PackageDescr>> implements EfestoResource<Set<PackageDescr>> {

    public EfestoRedirectOutputJDrl(ModelLocalUriId modelLocalUriId, Set<PackageDescr> packageDescr) {
        super(modelLocalUriId, "drl", packageDescr);
    }

    @Override
    public List<String> getFullClassNames() {
        return null;
    }
}
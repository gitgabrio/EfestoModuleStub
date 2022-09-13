package stub.module.compilation.model;

import java.util.Collections;

import org.drools.drl.ast.descr.PackageDescr;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoSetResource;

public class EfestoRedirectOutputJDrl extends EfestoSetResource<PackageDescr> implements EfestoCompilationOutput {

    private final PackageDescr packageDescr;

    public EfestoRedirectOutputJDrl(ModelLocalUriId modelLocalUriId, PackageDescr packageDescr) {
        super(Collections.singleton(packageDescr), modelLocalUriId);
        this.packageDescr = packageDescr;
    }
}
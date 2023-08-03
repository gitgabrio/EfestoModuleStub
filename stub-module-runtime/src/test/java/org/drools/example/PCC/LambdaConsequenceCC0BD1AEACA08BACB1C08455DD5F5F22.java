package org.drools.example.PCC;


import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import org.drools.example.*;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaConsequenceCC0BD1AEACA08BACB1C08455DD5F5F22 implements org.drools.model.functions.Block2<org.drools.model.Drools, org.drools.example.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "59CF3BE488CDE6B70CF242189954CD4E";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    private final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE, "approved");

    @Override()
    public void execute(org.drools.model.Drools drools, org.drools.example.LoanApplication $l) throws java.lang.Exception {
        {
            {
                ($l).setApproved(false);
            }
            drools.update($l, mask_$l);
        }
    }
}

package org.drools.example.PFC;


import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import org.drools.example.*;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaConsequenceFCD6009004A2C4AA104AC4FE459F1BF4 implements org.drools.model.functions.Block2<org.drools.model.Drools, org.drools.example.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "75285300D51D5FCCC5B54DA1195A7678";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    private final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE, "approved");

    @Override()
    public void execute(org.drools.model.Drools drools, org.drools.example.LoanApplication $l) throws java.lang.Exception {
        {
            {
                ($l).setApproved(true);
            }
            drools.update($l, mask_$l);
        }
    }
}

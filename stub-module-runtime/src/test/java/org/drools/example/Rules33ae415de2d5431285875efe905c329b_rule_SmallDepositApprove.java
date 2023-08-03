package org.drools.example;

import org.drools.modelcompiler.dsl.pattern.D;
import org.drools.model.Index.ConstraintType;
import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;

public class Rules33ae415de2d5431285875efe905c329b_rule_SmallDepositApprove {

    /**
     * Rule name: SmallDepositApprove
     */
    public static org.drools.model.Rule rule_SmallDepositApprove() {
        final org.drools.model.Variable<org.drools.example.LoanApplication> var_$l = D.declarationOf(org.drools.example.LoanApplication.class,
                                                                                                     DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE,
                                                                                                     "$l");
        final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE,
                                                                                         "approved");
        org.drools.model.Rule rule = D.rule("org.drools.example",
                                            "SmallDepositApprove")
                                      .build(D.pattern(var_$l).expr("GENERATED_7EC5C564C8A294A5896E34A8C4A8BF38",
                                                                    org.drools.example.PDA.LambdaPredicateDA4A03D422FD3C445171BA218CBFB787.INSTANCE,
                                                                    D.alphaIndexedBy(int.class,
                                                                                     org.drools.model.Index.ConstraintType.GREATER_OR_EQUAL,
                                                                                     -1,
                                                                                     org.drools.example.PF9.LambdaExtractorF9D9EC35030E912CC9DCFF3868AE5671.INSTANCE,
                                                                                     20),
                                                                    D.reactOn("applicant")).expr("GENERATED_6D87E7A4C292FF3B9B693019C99D69B7",
                                                                                                 org.drools.example.PA4.LambdaPredicateA4A538252603D113DF5C3325E301D457.INSTANCE,
                                                                                                 D.alphaIndexedBy(int.class,
                                                                                                                  org.drools.model.Index.ConstraintType.LESS_THAN,
                                                                                                                  DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE.getPropertyIndex("deposit"),
                                                                                                                  org.drools.example.PEB.LambdaExtractorEB7215397765962D1D219992FE81D9BF.INSTANCE,
                                                                                                                  1000),
                                                                                                 D.reactOn("deposit")).expr("GENERATED_1A524C986B3484486ED747786331F418",
                                                                                                                            org.drools.example.PDE.LambdaPredicateDED75B5AC37C4BE574A0D96BB77B68A3.INSTANCE,
                                                                                                                            D.alphaIndexedBy(int.class,
                                                                                                                                             org.drools.model.Index.ConstraintType.LESS_OR_EQUAL,
                                                                                                                                             DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE.getPropertyIndex("amount"),
                                                                                                                                             org.drools.example.PB9.LambdaExtractorB95EC773E141140DC07012C539C210EF.INSTANCE,
                                                                                                                                             2000),
                                                                                                                            D.reactOn("amount")),
                                             D.on(var_$l).execute(org.drools.example.PFC.LambdaConsequenceFCD6009004A2C4AA104AC4FE459F1BF4.INSTANCE));
        return rule;
    }
}

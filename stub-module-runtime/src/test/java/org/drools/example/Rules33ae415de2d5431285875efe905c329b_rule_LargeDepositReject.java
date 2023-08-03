package org.drools.example;

import org.drools.modelcompiler.dsl.pattern.D;
import org.drools.model.Index.ConstraintType;
import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;

public class Rules33ae415de2d5431285875efe905c329b_rule_LargeDepositReject {

    /**
     * Rule name: LargeDepositReject
     */
    public static org.drools.model.Rule rule_LargeDepositReject() {
        final org.drools.model.Variable<org.drools.example.LoanApplication> var_$l = D.declarationOf(org.drools.example.LoanApplication.class,
                                                                                                     DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE,
                                                                                                     "$l");
        final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE,
                                                                                         "approved");
        org.drools.model.Rule rule = D.rule("org.drools.example",
                                            "LargeDepositReject")
                                      .build(D.pattern(var_$l).expr("GENERATED_7EC5C564C8A294A5896E34A8C4A8BF38",
                                                                    org.drools.example.PDA.LambdaPredicateDA4A03D422FD3C445171BA218CBFB787.INSTANCE,
                                                                    D.alphaIndexedBy(int.class,
                                                                                     org.drools.model.Index.ConstraintType.GREATER_OR_EQUAL,
                                                                                     -1,
                                                                                     org.drools.example.PF9.LambdaExtractorF9D9EC35030E912CC9DCFF3868AE5671.INSTANCE,
                                                                                     20),
                                                                    D.reactOn("applicant")).expr("GENERATED_EE3E57C70F41A4DACC0C8381A0C850D3",
                                                                                                 org.drools.example.P58.LambdaPredicate58AB6F1244A68DDBDF5B3500297C60B3.INSTANCE,
                                                                                                 D.alphaIndexedBy(int.class,
                                                                                                                  org.drools.model.Index.ConstraintType.GREATER_OR_EQUAL,
                                                                                                                  DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE.getPropertyIndex("deposit"),
                                                                                                                  org.drools.example.PEB.LambdaExtractorEB7215397765962D1D219992FE81D9BF.INSTANCE,
                                                                                                                  1000),
                                                                                                 D.reactOn("deposit")).expr("GENERATED_8156CFE077808D4A890C40EA53AC2C4F",
                                                                                                                            var_maxAmount,
                                                                                                                            org.drools.example.P1A.LambdaPredicate1A7060AC165662654A57B36E54647362.INSTANCE,
                                                                                                                            D.reactOn("amount")),
                                             D.on(var_$l).execute(org.drools.example.PCC.LambdaConsequenceCC0BD1AEACA08BACB1C08455DD5F5F22.INSTANCE));
        return rule;
    }
}

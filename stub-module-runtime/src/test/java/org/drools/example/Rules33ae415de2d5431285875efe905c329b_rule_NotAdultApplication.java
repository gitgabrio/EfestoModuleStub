package org.drools.example;

import org.drools.modelcompiler.dsl.pattern.D;
import org.drools.model.Index.ConstraintType;
import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;

public class Rules33ae415de2d5431285875efe905c329b_rule_NotAdultApplication {

    /**
     * Rule name: NotAdultApplication
     */
    public static org.drools.model.Rule rule_NotAdultApplication() {
        final org.drools.model.Variable<org.drools.example.LoanApplication> var_$l = D.declarationOf(org.drools.example.LoanApplication.class,
                                                                                                     DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE,
                                                                                                     "$l");
        final org.drools.model.BitMask mask_$l = org.drools.model.BitMask.getPatternMask(DomainClassesMetadata33ae415de2d5431285875efe905c329b.org_drools_example_LoanApplication_Metadata_INSTANCE,
                                                                                         "approved");
        org.drools.model.Rule rule = D.rule("org.drools.example",
                                            "NotAdultApplication")
                                      .build(D.pattern(var_$l).expr("GENERATED_03C8BF5550E8B2F7B139C09548522EA8",
                                                                    org.drools.example.P74.LambdaPredicate741759C3ABBD1DA1CD51DB5297D8A32A.INSTANCE,
                                                                    D.alphaIndexedBy(int.class,
                                                                                     org.drools.model.Index.ConstraintType.LESS_THAN,
                                                                                     -1,
                                                                                     org.drools.example.PF9.LambdaExtractorF9D9EC35030E912CC9DCFF3868AE5671.INSTANCE,
                                                                                     20),
                                                                    D.reactOn("applicant")),
                                             D.on(var_$l).execute(org.drools.example.PCC.LambdaConsequenceCC0BD1AEACA08BACB1C08455DD5F5F22.INSTANCE));
        return rule;
    }
}

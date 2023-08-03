package org.drools.example.PD1;


import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import org.drools.example.*;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicateD1F575155DA8974A912BE3E2FA4EF8A9 implements org.drools.model.functions.Predicate1<org.drools.example.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "C9D5EFB4F1A0639376A538EA6070D740";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.drools.example.LoanApplication _this) throws java.lang.Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.greaterThanNumbers(_this.getAmount(), 2000);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("amount > 2000");
        info.addRuleNames("SmallDepositReject", "");
        return info;
    }
}

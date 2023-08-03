package org.drools.example.PDE;


import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import org.drools.example.*;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicateDED75B5AC37C4BE574A0D96BB77B68A3 implements org.drools.model.functions.Predicate1<org.drools.example.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "19A8E3AE4234C6F9F7AFD2BFA81F5679";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.drools.example.LoanApplication _this) throws java.lang.Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.lessOrEqualNumbers(_this.getAmount(), 2000);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("amount <= 2000");
        info.addRuleNames("SmallDepositApprove", "");
        return info;
    }
}

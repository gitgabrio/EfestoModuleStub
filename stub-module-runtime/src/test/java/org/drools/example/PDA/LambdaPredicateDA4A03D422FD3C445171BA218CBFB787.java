package org.drools.example.PDA;


import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import org.drools.example.*;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicateDA4A03D422FD3C445171BA218CBFB787 implements org.drools.model.functions.Predicate1<org.drools.example.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "8CF122E4B7966047AD63C915B9B382E6";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.drools.example.LoanApplication _this) throws java.lang.Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.greaterOrEqualNumbers(_this.getApplicant().getAge(), 20);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("applicant.age >= 20");
        info.addRuleNames("LargeDepositApprove", "", "LargeDepositReject", "", "SmallDepositApprove", "", "SmallDepositReject", "");
        return info;
    }
}

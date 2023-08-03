package org.drools.example.P58;


@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicate58AB6F1244A68DDBDF5B3500297C60B3 implements org.drools.model.functions.Predicate1<org.drools.example.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "2F3DB0915E0A943ED10D19E97CAC3E33";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.drools.example.LoanApplication _this) throws java.lang.Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.greaterOrEqualNumbers(_this.getDeposit(), 1000);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("deposit >= 1000");
        info.addRuleNames("LargeDepositApprove", "", "LargeDepositReject", "");
        return info;
    }
}

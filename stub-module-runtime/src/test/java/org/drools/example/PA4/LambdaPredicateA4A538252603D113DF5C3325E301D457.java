package org.drools.example.PA4;


@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicateA4A538252603D113DF5C3325E301D457 implements org.drools.model.functions.Predicate1<org.drools.example.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "EE1BCDF29B8D8D039752674CF52847E8";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.drools.example.LoanApplication _this) throws java.lang.Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.lessThanNumbers(_this.getDeposit(), 1000);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("deposit < 1000");
        info.addRuleNames("SmallDepositApprove", "", "SmallDepositReject", "");
        return info;
    }
}

package org.drools.example.P74;


@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicate74DA0F4F18206418D7279864AA4890FC implements org.drools.model.functions.Predicate2<org.drools.example.LoanApplication, java.lang.Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "D3BD4E759B2A014AC2B7ED3361F59D28";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.drools.example.LoanApplication _this, java.lang.Integer maxAmount) throws java.lang.Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.lessOrEqualNumbers(_this.getAmount(), maxAmount);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("amount <= maxAmount");
        info.addRuleNames("LargeDepositApprove", "");
        return info;
    }
}

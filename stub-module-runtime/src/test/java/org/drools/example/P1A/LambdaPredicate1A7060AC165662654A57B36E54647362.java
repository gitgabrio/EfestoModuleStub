package org.drools.example.P1A;


@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicate1A7060AC165662654A57B36E54647362 implements org.drools.model.functions.Predicate2<org.drools.example.LoanApplication, java.lang.Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "BC90371D268DB500143AD93145CA2B41";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.drools.example.LoanApplication _this, java.lang.Integer maxAmount) throws java.lang.Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.greaterThanNumbers(_this.getAmount(), maxAmount);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("amount > maxAmount");
        info.addRuleNames("LargeDepositReject", "");
        return info;
    }
}

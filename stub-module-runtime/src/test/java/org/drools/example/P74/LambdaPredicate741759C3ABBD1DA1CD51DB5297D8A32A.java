package org.drools.example.P74;


@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicate741759C3ABBD1DA1CD51DB5297D8A32A implements org.drools.model.functions.Predicate1<org.drools.example.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "7298A6124A8EEF7F636AFFB4C177AB73";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.drools.example.LoanApplication _this) throws java.lang.Exception {
        return org.drools.modelcompiler.util.EvaluationUtil.lessThanNumbers(_this.getApplicant().getAge(), 20);
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("applicant.age < 20");
        info.addRuleNames("NotAdultApplication", "");
        return info;
    }
}

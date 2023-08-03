package org.drools.example.PB9;


@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaExtractorB95EC773E141140DC07012C539C210EF implements org.drools.model.functions.Function1<org.drools.example.LoanApplication, Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "FF79F52543596DF5B186B25C552D3DA9";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public Integer apply(org.drools.example.LoanApplication _this) {
        return _this.getAmount();
    }
}

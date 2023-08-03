package org.drools.example.PEB;


import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import org.drools.example.*;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaExtractorEB7215397765962D1D219992FE81D9BF implements org.drools.model.functions.Function1<org.drools.example.LoanApplication, Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "760560CF9E706AABA4A14ECDED8E855E";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public Integer apply(org.drools.example.LoanApplication _this) {
        return _this.getDeposit();
    }
}

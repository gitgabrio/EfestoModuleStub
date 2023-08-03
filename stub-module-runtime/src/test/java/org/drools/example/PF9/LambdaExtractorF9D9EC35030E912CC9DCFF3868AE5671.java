package org.drools.example.PF9;


import static org.drools.example.Rules33ae415de2d5431285875efe905c329b.*;
import org.drools.example.*;
import org.drools.modelcompiler.dsl.pattern.D;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaExtractorF9D9EC35030E912CC9DCFF3868AE5671 implements org.drools.model.functions.Function1<org.drools.example.LoanApplication, Integer>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "1AB0FFF25DD1169AFA8EE7F6F90B2D14";

    public java.lang.String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public Integer apply(org.drools.example.LoanApplication _this) {
        return _this.getApplicant().getAge();
    }
}

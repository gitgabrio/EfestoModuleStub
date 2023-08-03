package org.drools.example;

import org.drools.modelcompiler.dsl.pattern.D;
import org.drools.model.Index.ConstraintType;

public class Rules33ae415de2d5431285875efe905c329b implements org.drools.model.Model {

    public final static java.time.format.DateTimeFormatter DATE_TIME_FORMATTER = new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(org.drools.util.DateUtils.getDateFormatMask()).toFormatter(java.util.Locale.ENGLISH);

    @Override
    public String getName() {
        return "org.drools.example";
    }

    @Override
    public java.util.List<org.drools.model.Global> getGlobals() {
        return globals;
    }

    @Override
    public java.util.List<org.drools.model.TypeMetaData> getTypeMetaDatas() {
        return typeMetaDatas;
    }

    public static final org.drools.model.Global<java.lang.Integer> var_maxAmount = D.globalOf(java.lang.Integer.class,
                                                                                              "org.drools.example",
                                                                                              "maxAmount");

    public static final org.drools.model.Global<java.util.List> var_approvedApplications = D.globalOf(java.util.List.class,
                                                                                                      "org.drools.example",
                                                                                                      "approvedApplications");

    protected java.util.List<org.drools.model.Global> globals = new java.util.ArrayList<>();

    java.util.List<org.drools.model.TypeMetaData> typeMetaDatas = java.util.Collections.emptyList();

    /**
     * With the following expression ID:
     * org.drools.model.codegen.execmodel.generator.DRLIdGenerator@443a06ad
     */
    @Override
    public java.util.List<org.drools.model.Rule> getRules() {
        return rules;
    }

    public java.util.List<org.drools.model.Rule> getRulesList() {
        return java.util.Arrays.asList(Rules33ae415de2d5431285875efe905c329b_rule_SmallDepositApprove.rule_SmallDepositApprove(),
                                       Rules33ae415de2d5431285875efe905c329b_rule_SmallDepositReject.rule_SmallDepositReject(),
                                       Rules33ae415de2d5431285875efe905c329b_rule_LargeDepositApprove.rule_LargeDepositApprove(),
                                       Rules33ae415de2d5431285875efe905c329b_rule_LargeDepositReject.rule_LargeDepositReject(),
                                       Rules33ae415de2d5431285875efe905c329b_rule_NotAdultApplication.rule_NotAdultApplication());
    }

    java.util.List<org.drools.model.Rule> rules = getRulesList();

    @Override
    public java.util.List<org.drools.model.Query> getQueries() {
        return java.util.Collections.emptyList();
    }

    @Override
    public java.util.List<org.drools.model.EntryPoint> getEntryPoints() {
        return java.util.Collections.emptyList();
    }

    {
        globals.add(var_maxAmount);
        globals.add(var_approvedApplications);
    }
}

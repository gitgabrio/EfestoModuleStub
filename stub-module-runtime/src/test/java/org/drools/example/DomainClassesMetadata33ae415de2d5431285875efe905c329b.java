package org.drools.example;
public class DomainClassesMetadata33ae415de2d5431285875efe905c329b {

    public static final org.drools.model.DomainClassMetadata org_drools_example_LoanApplication_Metadata_INSTANCE = new org_drools_example_LoanApplication_Metadata();
    private static class org_drools_example_LoanApplication_Metadata implements org.drools.model.DomainClassMetadata {

        @Override
        public Class<?> getDomainClass() {
            return org.drools.example.LoanApplication.class;
        }

        @Override
        public int getPropertiesSize() {
            return 6;
        }

        @Override
        public int getPropertyIndex( String name ) {
            switch(name) {
                case "this": return 0;
                case "amount": return 1;
                case "applicant": return 2;
                case "approved": return 3;
                case "deposit": return 4;
                case "id": return 5;
             }
             throw new RuntimeException("Unknown property '" + name + "' for class class class org.drools.example.LoanApplication");
        }
    }
    public static final org.drools.model.DomainClassMetadata java_util_List_Metadata_INSTANCE = new java_util_List_Metadata();
    private static class java_util_List_Metadata implements org.drools.model.DomainClassMetadata {

        @Override
        public Class<?> getDomainClass() {
            return java.util.List.class;
        }

        @Override
        public int getPropertiesSize() {
            return 10;
        }

        @Override
        public int getPropertyIndex( String name ) {
            switch(name) {
                case "this": return 0;
                case "empty": return 1;
                case "parallelStream": return 2;
                case "stream": return 3;
                case "iterator": return 4;
                case "listIterator": return 5;
                case "of": return 6;
                case "size": return 7;
                case "spliterator": return 8;
                case "toArray": return 9;
             }
             throw new RuntimeException("Unknown property '" + name + "' for class class interface java.util.List");
        }
    }
    public static final org.drools.model.DomainClassMetadata java_lang_Integer_Metadata_INSTANCE = new java_lang_Integer_Metadata();
    private static class java_lang_Integer_Metadata implements org.drools.model.DomainClassMetadata {

        @Override
        public Class<?> getDomainClass() {
            return java.lang.Integer.class;
        }

        @Override
        public int getPropertiesSize() {
            return 7;
        }

        @Override
        public int getPropertyIndex( String name ) {
            switch(name) {
                case "this": return 0;
                case "byteValue": return 1;
                case "doubleValue": return 2;
                case "floatValue": return 3;
                case "intValue": return 4;
                case "longValue": return 5;
                case "shortValue": return 6;
             }
             throw new RuntimeException("Unknown property '" + name + "' for class class class java.lang.Integer");
        }
    }
}
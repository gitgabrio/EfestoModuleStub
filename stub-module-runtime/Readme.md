Efesto runtime submodule - step-by-step
===========================================

(this guide refers to the creation of a runtime submodule with a single **KieRuntimeService** implementation)

1. create runtime submodule (i.e. the current **stub-module-runtime**)
2. declare efesto-runtime dependencies
3. implement *KieRuntimeService* interface
4. declare above implementation inside **src/main/resources/META-INF/services/org.kie.efesto.runtimemanager.api.service.KieRuntimeService**

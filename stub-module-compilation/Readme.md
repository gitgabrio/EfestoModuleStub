Efesto compilation submodule - step-by-step
===========================================

(this guide refers to the creation of a compilation submodule with a single **KieCompilationService** implementation)

1. create compilation submodule (i.e. the current **stub-module-compilation**)
2. declare efesto-compilation dependencies
3. implement *KieCompilerService* interface
4. fix generics to have a better typed implementation
5. declare above implementation inside **src/main/resources/META-INF/services/org.kie.efesto.compilationmanager.api.service.KieCompilerService**

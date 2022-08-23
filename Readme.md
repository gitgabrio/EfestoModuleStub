Efesto module - step-by-step
============================

(this guide refers to the creation of a module with an *api*, *compilation* and *runtime* submodules)

1. create container module (i.e. the current **EfestoModuleStub**)
2. define drools referred version
3. declare efesto dependency management
4. implement *compilation* submodule (see specific [guide](stub-module-compilation/Readme.md))
5. implement *runtime* submodule (see specific [guide](stub-module-runtime/Readme.md))
6. put shared code inside the *api* submodule (if needed)
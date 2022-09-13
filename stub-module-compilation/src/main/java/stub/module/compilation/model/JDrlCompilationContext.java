package stub.module.compilation.model;

import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContextImpl;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.memorycompiler.KieMemoryCompiler;

public interface JDrlCompilationContext extends EfestoCompilationContext {

    static JDrlCompilationContext buildWithParentClassLoader(ClassLoader parentClassLoader) {
        return new JDrlCompilationContextImpl(new KieMemoryCompiler.MemoryCompilerClassLoader(parentClassLoader));
    }

    static JDrlCompilationContext buildWithMemoryCompilerClassLoader(KieMemoryCompiler.MemoryCompilerClassLoader memoryCompilerClassLoader) {
        return new JDrlCompilationContextImpl(memoryCompilerClassLoader);
    }

    static JDrlCompilationContext buildWithEfestoCompilationContext(EfestoCompilationContextImpl context) {
        return (JDrlCompilationContext) EfestoCompilationContext.buildFromContext(context, JDrlCompilationContextImpl.class);
    }

    KnowledgeBuilderConfiguration newKnowledgeBuilderConfiguration();
}
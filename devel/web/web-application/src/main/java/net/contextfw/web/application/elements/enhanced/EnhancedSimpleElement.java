package net.contextfw.web.application.elements.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;
import net.contextfw.web.service.application.WebApplicationException;

import com.google.inject.Inject;
import com.google.inject.Injector;

public abstract class EnhancedSimpleElement implements CSimpleElement {

    private Injector injector;
    private EnhancedElementBuilder builder;
    
    @Override
    public void build(DOMBuilder superBuilder) {
        try {
            DOMBuilder b = superBuilder.descend(builder.getActualClass(this).getSimpleName());
            builder.build(b, this);
        }
        catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @Inject
    public void setInjector(Injector injector) {
        this.injector = injector;
    }

    protected Injector getInjector() {
        return injector;
    }

    @Inject
    public void setBuilder(EnhancedElementBuilder builder) {
        this.builder = builder;
    }
}
package net.contextfw.web.application.elements.enhanced;

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.dom.RemoteCallBuilder;
import net.contextfw.web.application.elements.CElement;

import com.google.inject.Inject;
import com.google.inject.Injector;

public abstract class EnhancedElement extends CElement {

    private Injector injector;
    private EnhancedElementBuilder builder;
    
    private Set<String> updateModes = new HashSet<String>();
    
    private Set<String> partialUpdates = new HashSet<String>();

    public void refresh(String... names) {
        if (partialUpdates != null) {
            for (String partialUpdate : names) {
                partialUpdates.add(partialUpdate);
            }
        }
        refresh();
    }
    
    public void addUpdateMode(String updateMode) {
        updateModes.add(updateMode.intern());
    }
    
    public boolean containsUpdateMode(String mode) {
        return updateModes.contains(mode);
    }
    
    public boolean hasUpdateModes() {
        return !updateModes.isEmpty();
    }
    
    @Override
    public final void buildUpdate(DOMBuilder superBuilder) {
        try {
            if (partialUpdates.isEmpty()) {
                DOMBuilder b  = superBuilder.descend(builder.getActualClass(this).getSimpleName() + ".update");
                addCommonAttributes(b);
                builder.buildUpdate(b, this, updateModes);
            } else {
                builder.buildUpdate(superBuilder, this, partialUpdates);
            }
        }
        catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }
    
    @Override
    public void clearCascadedUpdate() {
        super.clearCascadedUpdate();
        updateModes.clear();
        partialUpdates.clear();
    }

    protected <T extends CElement> T createChild(Class<T> clazz) {
        T child = injector.getInstance(clazz);
        this.registerChild(child);
        return child;
    }

    @Override
    public final void build(DOMBuilder superBuilder) {
        try {
            DOMBuilder b = superBuilder.descend(builder.getActualClass(this).getSimpleName());
            addCommonAttributes(b);
            builder.build(b, this);
        }
        catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @Override
    @EmbeddedAttribute
    public boolean isVisible() {
        return super.isVisible();
    }
    
    public void jsCall(DOMBuilder b, CElement element, String method, Object... args) {
    	RemoteCallBuilder.buildCall(b, builder.getActualClass(element).getSimpleName(), element.getId(), method, args);
    }

    public void jsCall(DOMBuilder b, String method, Object... args) {
        jsCall(b, this, method, args);
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
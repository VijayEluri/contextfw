package net.contextfw.web.application.internal.component;

import java.util.Set;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.dom.DOMBuilder;

import com.google.inject.ImplementedBy;

@ImplementedBy(ComponentBuilderImpl.class)
public interface ComponentBuilder {
    
    void build(DOMBuilder b, Object object);
    
    void buildUpdate(DOMBuilder b, Component component, String updateName);
    
    void buildPartialUpdate(DOMBuilder b, Component component, String updateName, Set<String> updates);
    
    boolean isBuildable(Class<?> cl);
    
}
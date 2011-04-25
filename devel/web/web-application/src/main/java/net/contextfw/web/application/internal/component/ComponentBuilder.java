package net.contextfw.web.application.internal.component;

import java.util.Set;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.DOMBuilder;

import com.google.inject.ImplementedBy;

@ImplementedBy(ComponentBuilderImpl.class)
public interface ComponentBuilder {
    
    void build(DOMBuilder b, Object object, Object... buildIns);
    
    void buildUpdate(DOMBuilder b, Component component, String updateName);
    
    void buildPartialUpdate(DOMBuilder b, Component component, String updateName, Set<String> updates);
    
    boolean isBuildable(Class<?> cl);
    
    String getBuildName(Class<?> cl);
    
}
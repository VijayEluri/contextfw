package net.contextfw.web.application.internal.component;

import java.util.Set;

import net.contextfw.web.application.component.DOMBuilder;

abstract class Builder {

    private final String accessName;
        
    abstract void build(DOMBuilder b, Object buildable);

    protected Builder(String name) {
        this.accessName = name;
    }
    
    boolean isUpdateBuildable(Set<String> updateModes) {
        if (accessName == null || updateModes.size() == 0) {
            return true;
        }
        return updateModes.contains(accessName);
    }
}
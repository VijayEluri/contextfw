package net.contextfw.web.application.internal.enhanced;

import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

abstract class Builder {

    private Set<String> updateModes = new HashSet<String>();
        
    abstract void build(DOMBuilder b, CSimpleElement element);

    void addModes(String... modes) {
        for (String mode : modes) {
            updateModes.add(mode.intern());
        }
    }
    
    boolean isUpdateBuildable(Set<String> updateModes) {
        
        if (this.updateModes.size() == 0 && updateModes.size() == 0) {
            return true;
        }
        for (String mode : this.updateModes) {
            if (updateModes.contains(mode)) {
                return true;
            }
        }
        
        return false;
    }
}
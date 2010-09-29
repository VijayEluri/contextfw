package net.contextfw.web.application.elements.enhanced;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

abstract class EmbeddedBuilder<T> {
    
    private Field field;
    private Method method;
    
    private String name;
    private Set<String> updateModes = new HashSet<String>();
    
    protected EmbeddedBuilder() {
        
    }
    
    protected EmbeddedBuilder(Field field, String name) {
        field.setAccessible(true);
        this.field = field;
        this.setName(name);
    }
    
    protected EmbeddedBuilder(Method method, String name) {
        this.method = method;
        this.setName(name);
    }
    
    abstract void build(DOMBuilder b, CSimpleElement element);
    
    @SuppressWarnings("unchecked")
    protected T getValue(CSimpleElement element) {
        try {
            return (T) (method != null ? method.invoke(element) : field.get(element));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

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
package net.contextfw.web.application.internal.component;

import java.lang.reflect.Method;

import net.contextfw.web.application.component.DOMBuilder;

class MethodCustomBuilder extends Builder {

    private final Method method;
    
    private final String name;
    
    public MethodCustomBuilder(Method method, String name) {
        super(method.getName());
        this.method = method;
        this.name = name;
    }
    
    @Override
    public void build(DOMBuilder b, Object buildable) {
        try {
            method.invoke(buildable, name == null ? b : b.descend(name));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
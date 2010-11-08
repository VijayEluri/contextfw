package net.contextfw.web.application.internal.component;

import java.lang.reflect.Method;

import net.contextfw.web.application.dom.DOMBuilder;

class MethodCustomBuilder extends Builder {

    private final Method method;
    
    public MethodCustomBuilder(Method method) {
        super(method.getName());
        this.method = method;
    }
    
    @Override
    public void build(DOMBuilder b, Object buildable) {
        try {
            method.invoke(buildable, b);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
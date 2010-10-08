package net.contextfw.web.application.internal.enhanced;

import java.lang.reflect.Method;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

class MethodCustomBuilder extends Builder {

    private final Method method;
    
    public MethodCustomBuilder(Method method) {
        this.method = method;
    }
    
    @Override
    public void build(DOMBuilder b, CSimpleElement element) {
        try {
            method.invoke(element, b);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
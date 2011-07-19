package net.contextfw.web.application.internal.component;

import java.lang.reflect.Method;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.DOMBuilder;

class MethodCustomBuilder extends Builder {

    private final Method method;
    
    private final String name;
    
    public MethodCustomBuilder(Method method, String name) {
        super(method.getName());
        
        if (method.getParameterTypes().length == 0 
                || method.getParameterTypes().length > 1 
                || method.getParameterTypes()[0] != DOMBuilder.class) {
            throw new WebApplicationException("Method " + method.getDeclaringClass().getName() 
                    + "." + method.getName() + "() must take only one parameter of type DOMBuilder");
        }
        
        this.method = method;
        this.name = name;
    }
    
    @Override
    public void build(DOMBuilder b, Object buildable) {
        try {
            method.invoke(buildable, name == null ? b : b.descend(name));
        }
        catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }
}
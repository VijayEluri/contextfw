package net.contextfw.web.application.internal.component;

import net.contextfw.web.application.dom.DOMBuilder;

abstract class NamedBuilder extends PropertyBuilder {

    private final String name;
    
    protected NamedBuilder(PropertyAccess<Object> propertyAccess, String name, String methodName) {
        super(propertyAccess, methodName);
        this.name = name;
    }

    @Override
    final void buildValue(DOMBuilder b, Object value) {
        buildNamedValue(b, name, value);
    }
    
    abstract void buildNamedValue(DOMBuilder b, String name, Object value);
}

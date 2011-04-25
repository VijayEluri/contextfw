package net.contextfw.web.application.internal.component;

import net.contextfw.web.application.component.DOMBuilder;

abstract class PropertyBuilder extends Builder {

    private PropertyAccess<Object> propertyAccess;
    
    protected PropertyBuilder(PropertyAccess<Object> propertyAccess, String methodName) {
        super(methodName);
        this.propertyAccess = propertyAccess;
    }
    
    final void build(DOMBuilder b, Object buildable) {
        buildValue(b, propertyAccess.getValue(buildable));
    }
    
    abstract void buildValue(DOMBuilder b, Object value);
}
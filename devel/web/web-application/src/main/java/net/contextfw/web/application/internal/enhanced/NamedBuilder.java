package net.contextfw.web.application.internal.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;

public abstract class NamedBuilder extends PropertyBuilder {

    private final String name;
    
    protected NamedBuilder(PropertyAccess propertyAccess, String name) {
        super(propertyAccess);
        this.name = name;
    }

    @Override
    final void buildValue(DOMBuilder b, Object value) {
        buildNamedValue(b, name, value);
    }
    
    abstract void buildNamedValue(DOMBuilder b, String name, Object value);
}

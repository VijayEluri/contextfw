package net.contextfw.web.application.internal.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;

public class AttributeBuilder extends NamedBuilder {

    protected AttributeBuilder(PropertyAccess<Object> propertyAccess, String name) {
        super(propertyAccess, name);
    }

    @Override
    void buildNamedValue(DOMBuilder b, String name, Object value) {
        b.attr(name, value);
    }
}

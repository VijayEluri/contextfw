package net.contextfw.web.application.internal.component;

import net.contextfw.web.application.component.DOMBuilder;

public class AttributeBuilder extends NamedBuilder {

    protected AttributeBuilder(PropertyAccess<Object> propertyAccess, String name, String accessName) {
        super(propertyAccess, name, accessName);
    }

    @Override
    void buildNamedValue(DOMBuilder b, String name, Object value) {
        if (value != null) {
            b.attr(name, value);
        }
    }
}

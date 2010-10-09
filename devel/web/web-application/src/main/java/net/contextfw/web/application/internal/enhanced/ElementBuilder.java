package net.contextfw.web.application.internal.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

class ElementBuilder extends NamedBuilder {

    protected ElementBuilder(PropertyAccess<Object> propertyAccess, String name) {
        super(propertyAccess, name);
    }

    @Override
    void buildNamedValue(DOMBuilder b, String name, Object value) {
        if (value instanceof CSimpleElement) {
            ((CSimpleElement) value).build(b.descend(name));
        } else {
            b.descend(name).text(value);
        }
    }
}

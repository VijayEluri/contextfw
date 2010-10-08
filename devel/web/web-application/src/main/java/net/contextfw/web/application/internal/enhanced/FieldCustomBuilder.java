package net.contextfw.web.application.internal.enhanced;

import java.lang.reflect.Field;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

class FieldCustomBuilder extends Builder {

    private PropertyAccess<CSimpleElement> access;

    public FieldCustomBuilder(Field field) {
        if (CSimpleElement.class.isAssignableFrom(field.getType())) {
            access = new FieldPropertyAccess<CSimpleElement>(field);
        } else {
            throw new WebApplicationException(
                    "CustomBuild-annotation can be used only with fields that contains a subclass of CSimpleElement");
        }
    }

    @Override
    public void build(DOMBuilder b, CSimpleElement element) {
        CSimpleElement sElement = access.getValue(element);
        if (sElement != null) {
            sElement.build(b);
        }
    }
}

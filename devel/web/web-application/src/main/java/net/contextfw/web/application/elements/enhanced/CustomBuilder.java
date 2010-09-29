package net.contextfw.web.application.elements.enhanced;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

class CustomBuilder extends EmbeddedBuilder<Object> {

    private Method method;
    private Field field;
    
    public CustomBuilder(Method method) {
        this.method = method;
    }
    
    public CustomBuilder(Field field) {
        if (CSimpleElement.class.isAssignableFrom(field.getType())) {
            this.field = field;
            field.setAccessible(true);
        }
        else {
            throw new RuntimeException("CustomBuild-annotation can be used only with fields that contains a subclass of CSimpleElement");
        }
    }
    
    @Override
    public void build(DOMBuilder b, CSimpleElement element) {
        try {
            if (method != null) {
                method.invoke(element, b);
            }
            else if (field != null) {
                CSimpleElement sElement = (CSimpleElement) field.get(element);
                sElement.build(b);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
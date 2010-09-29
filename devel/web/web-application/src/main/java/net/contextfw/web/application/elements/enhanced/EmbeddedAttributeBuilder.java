package net.contextfw.web.application.elements.enhanced;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

public class EmbeddedAttributeBuilder extends EmbeddedBuilder<Object> {

    private AttributeConverter<Object> converter;
    
    public EmbeddedAttributeBuilder(Field field, String name, AttributeConverter<Object> converter) {
        super(field, name);
        this.converter = converter;
    }
    
    public EmbeddedAttributeBuilder(Method method, String name, AttributeConverter<Object> converter) {
        super(method, name);
        this.converter = converter;
    }

    @Override
    public void build(DOMBuilder b, CSimpleElement element) {
        Object value = getValue(element);
        if (value != null) {
            b.attr(getName(), converter.convert(value));
        }
    }
}
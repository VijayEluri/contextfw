package net.contextfw.web.application.elements.enhanced;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

public class EmbeddedElementBuilder extends EmbeddedBuilder<Object> {

    @SuppressWarnings("unchecked")
    private ElementBuilder elementBuilder;
    
    @SuppressWarnings("unchecked")
    public EmbeddedElementBuilder(String name, ElementBuilder elementBuilder) {
        this.setName(name);
        this.setElementBuilder(elementBuilder);
    }
    
    @SuppressWarnings("unchecked")
    public EmbeddedElementBuilder(Field field, String name, ElementBuilder elementBuilder) {
        super(field, name);
        this.setElementBuilder(elementBuilder);
    }
    
    @SuppressWarnings("unchecked")
    public EmbeddedElementBuilder(Method method, String name, ElementBuilder elementBuilder) {
        super(method, name);
        this.setElementBuilder(elementBuilder);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void build(DOMBuilder b, CSimpleElement element) {
        Object value = getValue(element);
        if (value != null) {
            getElementBuilder().build(b, getName(), value);
        }
    }

    @SuppressWarnings("unchecked")
    private void setElementBuilder(ElementBuilder elementBuilder) {
        this.elementBuilder = elementBuilder;
    }

    @SuppressWarnings("unchecked")
    public ElementBuilder getElementBuilder() {
        return elementBuilder;
    }
}
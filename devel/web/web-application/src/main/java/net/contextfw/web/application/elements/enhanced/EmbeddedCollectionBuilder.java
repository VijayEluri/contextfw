package net.contextfw.web.application.elements.enhanced;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

public class EmbeddedCollectionBuilder extends EmbeddedBuilder<Iterable<Object>> {
    
    private EmbeddedElementBuilder builder;

    public EmbeddedCollectionBuilder(Field field, String name, EmbeddedElementBuilder builder) {
        super(field, name);
        this.builder = builder;
    }
    
    public EmbeddedCollectionBuilder(Method method, String name, EmbeddedElementBuilder builder) {
        super(method, name);
        this.builder = builder;
    }
    
    @SuppressWarnings("unchecked")
    public void build(DOMBuilder b, CSimpleElement element) {
        
        Iterable<Object> children = getValue(element);
        DOMBuilder childBuilder = b.descend(getName());
        if (children != null) {
            for (Object child : children) {
                builder.getElementBuilder().build(childBuilder, builder.getName(), child);
            }
        }
    }
}
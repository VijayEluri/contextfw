package net.contextfw.web.application.elements.enhanced;

import java.lang.reflect.Method;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.elements.CSimpleElement;

class PartialUpdateBuilder extends EmbeddedBuilder<Object> {

    private final Method method;
    private final String name;
    
    public PartialUpdateBuilder(PartialUpdate partialUpdate, Method method) {
        this.method = method;
        if (partialUpdate.name().equals("")) {
            this.name = method.getDeclaringClass().getSimpleName() + "." + method.getName();
            this.addModes(method.getName());
        } else {
            this.name = partialUpdate.name();
            this.addModes(this.name);
        }
    }

    @Override
    public void build(DOMBuilder b, CSimpleElement element) {
        try {
            method.invoke(element, b.descend(name).attr("id", ((CElement)element).getId()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
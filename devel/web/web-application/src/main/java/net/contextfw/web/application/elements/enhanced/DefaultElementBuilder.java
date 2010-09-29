package net.contextfw.web.application.elements.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

@SuppressWarnings("unchecked")
public class DefaultElementBuilder implements ElementBuilder {

    @Override
    public void build(DOMBuilder superBuilder, String name, Object buildable) {
        DOMBuilder b = superBuilder.descend(name);
        if (Iterable.class.isAssignableFrom(buildable.getClass())) {
            for (Object obj : (Iterable) buildable) {
                buildElement(b, obj);
            }
        }
        else {
            buildElement(b, buildable);
        }
    }

    private void buildElement(DOMBuilder b, Object buildable) {
        if (CSimpleElement.class.isAssignableFrom(buildable.getClass())) {
            b.child((CSimpleElement) buildable);
        }
        else {
            b.text(buildable);
        }
    }
}
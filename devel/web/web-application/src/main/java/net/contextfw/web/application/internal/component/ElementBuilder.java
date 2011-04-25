package net.contextfw.web.application.internal.component;

import net.contextfw.web.application.component.DOMBuilder;

class ElementBuilder extends NamedBuilder {

    private final ComponentBuilder componentBuilder;
    
    protected ElementBuilder(ComponentBuilder componentBuilder, PropertyAccess<Object> propertyAccess, String name, String accessName) {
        super(propertyAccess, name, accessName);
        this.componentBuilder = componentBuilder;
    }

    @Override
    void buildNamedValue(DOMBuilder b, String name, Object value) {
        if (value != null) {
            if (componentBuilder.isBuildable(value.getClass())) {
                componentBuilder.build(name == null ? b : b.descend(name), value);
            } else if (value instanceof Iterable) {
                DOMBuilder child = b.descend(name);
                for (Object i : ((Iterable<?>) value)) {
                    componentBuilder.build(child, i);
                }
            } else {
                b.descend(name).text(value);
            }
        }
    }
}

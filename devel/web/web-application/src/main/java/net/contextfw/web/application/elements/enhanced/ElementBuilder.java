package net.contextfw.web.application.elements.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;

public interface ElementBuilder<T> {
    public void build(DOMBuilder b, String name, T buildable);
}

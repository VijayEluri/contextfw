package net.contextfw.web.application.elements.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;

public interface Builder<T> {
    void build(DOMBuilder b, T buildable);
}

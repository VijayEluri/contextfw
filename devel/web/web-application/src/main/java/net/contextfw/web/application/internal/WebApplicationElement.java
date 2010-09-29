package net.contextfw.web.application.internal;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CElement;

public class WebApplicationElement extends CElement {

    private final ElementRegister elementRegister;

    private CElement child;
    
    public WebApplicationElement(ElementRegister elementRegister) {
        this.elementRegister = elementRegister;
        elementRegister.register(this);
    }

    @Override
    protected boolean bubbleRegisterUp(CElement el) {
        elementRegister.register(el);
        return true;
    }

    @Override
    protected void bubbleUnregisterUp(CElement el) {
        elementRegister.unregister(el);
    }

    @Override
    public void build(DOMBuilder b) {
        child.build(b);
    }

    @Override
    public void buildUpdate(DOMBuilder b) {
        child.buildUpdate(b);
    }

    public void registerChild(CElement child) {
        super.registerChild(child);
        this.child = child;
    }
}
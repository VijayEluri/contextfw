package net.contextfw.web.application.internal.component;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.dom.DOMBuilder;

public class WebApplicationComponent extends Component {

    private final ComponentRegister elementRegister;

    private Component child;
    
    public WebApplicationComponent(ComponentRegister elementRegister) {
        this.elementRegister = elementRegister;
        elementRegister.register(this);
    }

    @Override
    protected boolean bubbleRegisterUp(Component el) {
        elementRegister.register(el);
        return true;
    }

    @Override
    protected void bubbleUnregisterUp(Component el) {
        elementRegister.unregister(el);
    }

    @Override
    public <T extends Component> T registerChild(T el) {
        this.child = super.registerChild(el);
        return el;
    }
    
    public void buildChild(DOMBuilder b) {
        b.child(child);
    }
    
    public void buildChildUpdate(DOMBuilder b, ComponentBuilder componentBuilder) {
        child.buildComponentUpdate(b, componentBuilder);
    }
}

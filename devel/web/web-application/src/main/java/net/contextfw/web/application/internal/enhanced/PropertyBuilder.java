package net.contextfw.web.application.internal.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;

abstract class PropertyBuilder extends Builder {

    private PropertyAccess propertyAccess;
    
    protected PropertyBuilder(PropertyAccess propertyAccess) {
        this.propertyAccess = propertyAccess;
    }
    
    final void build(DOMBuilder b, CSimpleElement element) {
        buildValue(b, propertyAccess.getValue(element));
    }
    
    abstract void buildValue(DOMBuilder b, Object value);
}
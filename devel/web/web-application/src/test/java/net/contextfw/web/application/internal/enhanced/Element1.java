package net.contextfw.web.application.internal.enhanced;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CSimpleElement;
import net.contextfw.web.application.elements.enhanced.CustomBuild;
import net.contextfw.web.application.elements.enhanced.EmbeddedAttribute;
import net.contextfw.web.application.elements.enhanced.EmbeddedElement;
import net.contextfw.web.application.elements.enhanced.EnhancedElement;

public class Element1 extends EnhancedElement {
    
    @EmbeddedAttribute
    String attr1 = "attr1_value";
    
    @EmbeddedAttribute
    Long attr2 = 2L;
    
    @EmbeddedElement
    String attr3 = "FOO\nBAR";
    
    @CustomBuild
    public void build1(DOMBuilder b) {
        b.descend("customBuild");
    }
    
    @CustomBuild
    private CSimpleElement attr4 =  new CSimpleElement() {

        @Override
        public void build(DOMBuilder b) {
            b.descend("attr4CustomBuild");
        }
    };
}
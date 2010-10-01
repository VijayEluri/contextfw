#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.elements;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CElement;

public abstract class Label extends CElement {

    public Label() {
        super();
    }

    @Override
    public void buildUpdate(DOMBuilder superBuilder) {
        DOMBuilder b = superBuilder.descend("Label.update");
        addCommonAttributes(b);
        buildContent(b);
    }

    @Override
    public void build(DOMBuilder superBuilder) {
        DOMBuilder b = superBuilder.descend("Label");
        addCommonAttributes(b);
        buildContent(b);
    }

    protected abstract void buildContent(DOMBuilder b);
}
#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.elements;

import net.contextfw.web.application.dom.DOMBuilder;

public class TextLabel extends Label {

    private String value = "";

    @Override
    protected void buildContent(DOMBuilder b) {
        b.text(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
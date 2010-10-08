package net.contextfw.application;

import net.contextfw.web.application.dom.AttributeHandler;

public class TstAttributeHandler implements AttributeHandler {

    @Override
    public String toString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

}

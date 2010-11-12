package net.contextfw.web.application.internal;

import net.contextfw.web.application.converter.AttributeSerializer;

/**
 * Default implementation for Object serialization.
 * @author marko
 *
 */
public class ToStringSerializer implements AttributeSerializer<Object> {

    @Override
    public String serialize(Object source) {
        return source == null ? null : source.toString();
    }
}

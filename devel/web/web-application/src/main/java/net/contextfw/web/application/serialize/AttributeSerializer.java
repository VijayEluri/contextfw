package net.contextfw.web.application.serialize;

/**
 * Defines a method to serialize object to DOM-attribute
 */
public interface AttributeSerializer<S> {
    String serialize(S source);
}

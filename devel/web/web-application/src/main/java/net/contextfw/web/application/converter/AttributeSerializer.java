package net.contextfw.web.application.converter;

public interface AttributeSerializer<S> {
    String serialize(S source);
}

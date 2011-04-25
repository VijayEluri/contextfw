package net.contextfw.web.application.serialize;

public interface AttributeSerializer<S> {
    String serialize(S source);
}

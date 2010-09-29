package net.contextfw.web.application.elements.enhanced;

public interface AttributeConverter<T> {
    Object convert(T object);
}

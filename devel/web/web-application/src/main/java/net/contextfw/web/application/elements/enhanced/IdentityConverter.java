package net.contextfw.web.application.elements.enhanced;

import com.google.inject.Singleton;

@Singleton
public class IdentityConverter implements AttributeConverter<Object> {
    @Override
    public Object convert(Object object) {
        return object;
    }
}

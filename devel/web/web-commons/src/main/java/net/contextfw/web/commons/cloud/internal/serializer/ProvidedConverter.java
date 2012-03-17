package net.contextfw.web.commons.cloud.internal.serializer;

import net.contextfw.web.application.WebApplicationException;

import com.google.inject.Injector;
import com.thoughtworks.xstream.converters.SingleValueConverter;

public class ProvidedConverter implements SingleValueConverter {

    private final Injector injector;
    
    private final String type;

    private ClassLoader classLoader;
    
    public ProvidedConverter(Injector injector, Class<?> type, ClassLoader classLoader) {
        this.type = type.getCanonicalName();
        this.injector = injector;
        this.classLoader = classLoader;
    }
    
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return true;
    }

    @Override
    public String toString(Object obj) {
        return type;
    }

    @Override
    public Object fromString(String str) {
        try {
            return injector.getInstance(classLoader.loadClass(type));
        } catch (ClassNotFoundException e) {
            throw new WebApplicationException(e);
        }
    }
}

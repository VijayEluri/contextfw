package net.contextfw.web.commons.cloud.internal.serializer;

import net.contextfw.web.application.WebApplicationException;

import com.google.inject.Key;
import com.thoughtworks.xstream.converters.SingleValueConverter;

public class KeyConverter implements SingleValueConverter {

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    
    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
        return Key.class.isAssignableFrom(type);
    }

    @Override
    public String toString(Object obj) {
        return ((Key<?>) obj).getTypeLiteral().getRawType().getCanonicalName();
    }

    @Override
    public Object fromString(String str) {
        try {
            return Key.get(classLoader.loadClass(str));
        } catch (ClassNotFoundException e) {
            throw new WebApplicationException(e);
        }
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}

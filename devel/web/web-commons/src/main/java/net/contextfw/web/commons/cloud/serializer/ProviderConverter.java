package net.contextfw.web.commons.cloud.serializer;

import net.contextfw.web.application.WebApplicationException;

import com.google.inject.Injector;
import com.thoughtworks.xstream.converters.SingleValueConverter;

public class ProviderConverter implements SingleValueConverter {

    private final Injector injector;
    
    private final String type;

    private ClassLoader classLoader;
    
    public ProviderConverter(Injector injector, Class<?> type, ClassLoader classLoader) {
        this.type = type == null ? null : type.getCanonicalName();
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
            return injector.getProvider(classLoader.loadClass(str));
        } catch (ClassNotFoundException e) {
            throw new WebApplicationException(e);
        }
    }
}

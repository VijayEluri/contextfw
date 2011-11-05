package net.contextfw.web.commons.cloud.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.scope.Provided;

import com.google.inject.Injector;
import com.google.inject.Provider;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class DependencyMapper extends MapperWrapper {

    private static final String RAWTYPES = "rawtypes";
    private final Injector injector;
    private ClassLoader classLoader;

    @Override
    public SingleValueConverter getConverterFromItemType(
            String fieldName,
            @SuppressWarnings(RAWTYPES)
            Class type,
            @SuppressWarnings(RAWTYPES)
            Class definedIn) {
        try {
            Field field = definedIn.getDeclaredField(fieldName);
            if (Provider.class.isAssignableFrom(type)) {

                if (field.getGenericType() instanceof ParameterizedType) {
                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                    Type actualType = genericType.getActualTypeArguments()[0];
                    if (actualType instanceof Class) {
                        return new ProviderConverter(injector,
                                (Class<?>) genericType.getActualTypeArguments()[0], classLoader);
                    } else if (actualType instanceof TypeVariable) {
                        return new ProviderConverter(injector, type, classLoader);
                    }
                }
            } else if (field.isAnnotationPresent(Provided.class)) {
                return new ProvidedConverter(injector, field.getType(), classLoader);
            }
        } catch (SecurityException e) {
            throw new WebApplicationException(e);
        } catch (NoSuchFieldException e) {
            throw new WebApplicationException(e);
        }
        return super.getConverterFromItemType(fieldName, type, definedIn);
    }

    @Override
    public SingleValueConverter getConverterFromAttribute(
            @SuppressWarnings(RAWTYPES)
            Class definedIn, 
            String attribute,
            @SuppressWarnings(RAWTYPES)
            Class type) {

        try {
            Field field = definedIn.getDeclaredField(attribute);
            if (field.isAnnotationPresent(Provided.class)) {
                return new ProvidedConverter(injector, type, classLoader);
            }
        } catch (SecurityException e) {
            throw new WebApplicationException(e);
        } catch (NoSuchFieldException e) {
            throw new WebApplicationException(e);
        }
        if (Provider.class.isAssignableFrom(type)) {
            return new ProviderConverter(injector, null, classLoader);
        } else {
            return super.getConverterFromAttribute(definedIn, attribute, type);
        }
    }

    public DependencyMapper(Mapper wrapped, 
                            Injector injector,
                            ClassLoader classLoader) {
        super(wrapped);
        this.injector = injector;
        this.classLoader = classLoader;
    }
}
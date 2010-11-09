package net.contextfw.web.application.internal.component;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Attribute;
import net.contextfw.web.application.component.Buildable;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.CustomBuild;
import net.contextfw.web.application.component.Element;
import net.contextfw.web.application.dom.DOMBuilder;

import com.google.inject.Singleton;

@Singleton
public class ComponentBuilderImpl implements ComponentBuilder {

    private Map<Class<?>, List<Builder>> builders = new HashMap<Class<?>, List<Builder>>();
    private Map<Class<?>, List<Builder>> updateBuilders = new HashMap<Class<?>, List<Builder>>();
    private Map<Class<?>, Buildable> annotations = new HashMap<Class<?>, Buildable>();

    private synchronized List<Builder> getBuilder(Class<?> cl) {
        if (builders.containsKey(cl)) {
            return builders.get(cl);
        }
        createBuilders(cl);
        return builders.get(cl);
    }

    private void createBuilders(Class<?> cl) {
        builders.put(cl, new ArrayList<Builder>());
        updateBuilders.put(cl, new ArrayList<Builder>());

        try {
            addEmbeddeds(cl);
        }
        catch (IllegalAccessException e) {
            throw new WebApplicationException(e);
        }
        catch (InvocationTargetException e) {
            throw new WebApplicationException(e);
        }
        catch (NoSuchMethodException e) {
            throw new WebApplicationException(e);
        }
        catch (IntrospectionException e) {
            throw new WebApplicationException(e);
        }
    }

    private void addEmbeddeds(Class<?> cl) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, IntrospectionException {

        Class<?> currentClass = cl;

        while (currentClass instanceof Object) {

            for (Field field : currentClass.getDeclaredFields()) {
                
                PropertyAccess<Object> propertyAccess = new FieldPropertyAccess<Object>(field);
                String name = null;
                Builder builder = null;
                
                if (field.getAnnotation(Element.class) != null) {
                    Element element = field.getAnnotation(Element.class);
                    name = "".equals(element.name()) ? field.getName() : element.name();
                    builder = new ElementBuilder(this, propertyAccess, name, field.getName());
                    addToBuilders(cl, element.onCreate(), element.onUpdate(), builder);
                }
                else if (field.getAnnotation(Attribute.class) != null) {
                    Attribute attribute = field.getAnnotation(Attribute.class);
                    name = "".equals(attribute.name()) ? field.getName() : attribute.name();
                    builder = new AttributeBuilder(propertyAccess, name, field.getName());
                    addToBuilders(cl, attribute.onCreate(), attribute.onUpdate(), builder);
                }
            }

            for (Method method : currentClass.getDeclaredMethods()) {
                
                PropertyAccess<Object> propertyAccess = new MethodPropertyAccess(method);
                String name = null;
                Builder builder = null;
                
                if (method.getAnnotation(Element.class) != null) {
                    Element annotation = method.getAnnotation(Element.class);
                    name = "".equals(annotation.name()) ? method.getName() : annotation.name();
                    builder = new ElementBuilder(this, propertyAccess, name, method.getName());
                    addToBuilders(cl, annotation.onCreate(), annotation.onUpdate(), builder);
                }
                else if(method.getAnnotation(Attribute.class) != null) {
                    Attribute annotation = method.getAnnotation(Attribute.class);
                    name = "".equals(annotation.name()) ? method.getName() : annotation.name();
                    builder = new AttributeBuilder(propertyAccess, name, method.getName());
                    addToBuilders(cl, annotation.onCreate(), annotation.onUpdate(), builder); 
                } else if (method.getAnnotation(CustomBuild.class) != null) {
                    CustomBuild annotation = method.getAnnotation(CustomBuild.class);
                    builder = new MethodCustomBuilder(method);
                    addToBuilders(cl, annotation.onCreate(), annotation.onUpdate(), builder);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private void addToBuilders(Class<?> cl, boolean onCreate, boolean onUpdate, Builder builder) {
        if (onCreate) {
            builders.get(cl).add(builder);
        }
        if (onUpdate) {
            updateBuilders.get(cl).add(builder);
        }
    }

    private synchronized List<Builder> getUpdateBuilder(Class<?> cl) {
        if (updateBuilders.containsKey(cl)) {
            return updateBuilders.get(cl);
        }
        createBuilders(cl);
        return updateBuilders.get(cl);
    }

    public static Class<?> getActualClass(Object element) {
        Class<?> cl = element.getClass();
        while (cl.getSimpleName().contains("EnhancerByGuice")) {
            cl = cl.getSuperclass();
        }
        return cl;
    }

    @Override
    public void build(DOMBuilder sb, Object component) {
        Class<?> cl = getActualClass(component);
        if (isBuildable(cl)) {
            DOMBuilder b;
            if (annotations.get(cl).noWrapping()) {
                b = sb;
            } else {
                b = sb.descend(cl.getSimpleName());
            }
            for (Builder builder : getBuilder(cl)) {
                builder.build(b, component);
            }
        } else {
            throw new WebApplicationException("Object must be annotated with @Buildable in order to be built");
        }
    }

    @Override
    public void buildUpdate(DOMBuilder sb, Component component, String updateName, Set<String> updates) {
        
        Class<?> cl = getActualClass(component);
        
        DOMBuilder b = sb.descend(cl.getSimpleName() + "." + updateName);
        
        List<Builder> builders = getUpdateBuilder(cl);
        
        for (Builder builder : builders) {
            if (builder.isUpdateBuildable(updates)) {
                builder.build(b, component);
            }
        }        
    }

    @Override
    public boolean isBuildable(Class<?> cl) {
        if (builders.containsKey(cl)) {
            return true;
        } else {
            Class<?> current = cl;
            while (current instanceof Object) {
                if (current.isAnnotationPresent(Buildable.class)) {
                    createBuilders(cl);
                    annotations.put(cl, current.getAnnotation(Buildable.class));
                    return true;
                }
                current = current.getSuperclass();
            }
            return false;
        }
    }
}
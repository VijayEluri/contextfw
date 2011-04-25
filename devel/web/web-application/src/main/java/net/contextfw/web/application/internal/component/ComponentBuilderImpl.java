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
import net.contextfw.web.application.component.DOMBuilder;
import net.contextfw.web.application.component.Element;
import net.contextfw.web.application.internal.util.AttributeHandler;
import net.contextfw.web.application.lifecycle.AfterBuild;
import net.contextfw.web.application.lifecycle.BeforeBuild;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ComponentBuilderImpl implements ComponentBuilder {

    private Map<Class<?>, List<Method>> beforeBuilds = new HashMap<Class<?>, List<Method>>();
    private Map<Class<?>, List<Method>> afterBuilds = new HashMap<Class<?>, List<Method>>();
    private Map<Class<?>, List<Builder>> builders = new HashMap<Class<?>, List<Builder>>();
    private Map<Class<?>, List<Builder>> updateBuilders = new HashMap<Class<?>, List<Builder>>();
    private Map<Class<?>, List<Builder>> partialBuilders = new HashMap<Class<?>, List<Builder>>();

    @Inject
    private AttributeHandler attributeHandler;
    
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
        partialBuilders.put(cl, new ArrayList<Builder>());

        try {
            addEmbeddeds(cl);
        } catch (IllegalAccessException e) {
            throw new WebApplicationException(e);
        } catch (InvocationTargetException e) {
            throw new WebApplicationException(e);
        } catch (NoSuchMethodException e) {
            throw new WebApplicationException(e);
        } catch (IntrospectionException e) {
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
                    builder = new ElementBuilder(this, propertyAccess, element.wrap() ? name : null, field.getName());
                    addToBuilders(cl, element.onCreate(), element.onUpdate(), element.onPartialUpdate(), builder);
                } else if (field.getAnnotation(Attribute.class) != null) {
                    Attribute attribute = field.getAnnotation(Attribute.class);
                    name = "".equals(attribute.name()) ? field.getName() : attribute.name();
                    builder = new AttributeBuilder(propertyAccess, name, field.getName());
                    addToBuilders(cl, attribute.onCreate(), attribute.onUpdate(), attribute.onPartialUpdate(), builder);
                }
            }

            for (Method method : currentClass.getDeclaredMethods()) {

                PropertyAccess<Object> propertyAccess = new MethodPropertyAccess(method);
                String name = null;
                Builder builder = null;

                if (method.getAnnotation(Element.class) != null) {
                    Element annotation = method.getAnnotation(Element.class);
                    name = "".equals(annotation.name()) ? method.getName() : annotation.name();
                    builder = new ElementBuilder(this, propertyAccess, annotation.wrap() ? name : null, method.getName());
                    addToBuilders(cl, annotation.onCreate(), annotation.onUpdate(), annotation.onPartialUpdate(), builder);
                } else if (method.getAnnotation(Attribute.class) != null) {
                    Attribute annotation = method.getAnnotation(Attribute.class);
                    name = "".equals(annotation.name()) ? method.getName() : annotation.name();
                    builder = new AttributeBuilder(propertyAccess, name, method.getName());
                    addToBuilders(cl, annotation.onCreate(), annotation.onUpdate(), annotation.onPartialUpdate(), builder);
                } else if (method.getAnnotation(CustomBuild.class) != null) {
                    CustomBuild annotation = method.getAnnotation(CustomBuild.class);
                    name = "".equals(annotation.name()) ? method.getName() : annotation.name();
                    builder = new MethodCustomBuilder(method, annotation.wrap() ? name : null);
                    addToBuilders(cl, annotation.onCreate(), annotation.onUpdate(), annotation.onPartialUpdate(), builder);
                } else if (method.getAnnotation(AfterBuild.class) != null) {
                    addAfterBuild(cl, method);
                } else if (method.getAnnotation(BeforeBuild.class) != null) {
                    addBeforeBuild(cl, method);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private void addAfterBuild(Class<?> cl, Method method) {
        if (afterBuilds.get(cl) == null) {
            afterBuilds.put(cl, new ArrayList<Method>());
        }
        afterBuilds.get(cl).add(method);
    }

    private void addBeforeBuild(Class<?> cl, Method method) {
        if (beforeBuilds.get(cl) == null) {
            beforeBuilds.put(cl, new ArrayList<Method>());
        }
        beforeBuilds.get(cl).add(method);
    }

    private void addToBuilders(Class<?> cl, boolean onCreate, boolean onUpdate, boolean onPartialUpdate, Builder builder) {
        if (onCreate) {
            builders.get(cl).add(builder);
        }
        if (onUpdate) {
            updateBuilders.get(cl).add(builder);
        }
        if (onPartialUpdate) {
            partialBuilders.get(cl).add(builder);
        }
    }

    private synchronized List<Builder> getUpdateBuilder(Class<?> cl) {
        if (updateBuilders.containsKey(cl)) {
            return updateBuilders.get(cl);
        }
        createBuilders(cl);
        return updateBuilders.get(cl);
    }

    private synchronized List<Builder> getPartialUpdateBuilder(Class<?> cl) {
        if (partialBuilders.containsKey(cl)) {
            return partialBuilders.get(cl);
        }
        createBuilders(cl);
        return partialBuilders.get(cl);
    }

    public static Class<?> getActualClass(Object element) {
        Class<?> cl = element.getClass();
        while (cl.getSimpleName().contains("EnhancerByGuice")) {
            cl = cl.getSuperclass();
        }
        return cl;
    }

    @Override
    public String getBuildName(Class<?> cl) {
        if (isBuildable(cl)) {
            Buildable bd = annotations.get(cl);
            return ("".equals(bd.name()) ? cl.getSimpleName() : bd.name());
        } else {
            throw new WebApplicationException("Class " + cl.getName() + " is not buildable");
        }
    }

    @Override
    public void build(DOMBuilder sb, Object component, Object... buildins) {
        Class<?> cl = getActualClass(component);
        if (component instanceof Component) {
            if (!((Component) component).isEnabled()) {
                return;
            }
        }
        if (isBuildable(cl)) {
            if (beforeBuilds.containsKey(cl)) {
                for (Method method : beforeBuilds.get(cl)) {
                    try {
                        method.invoke(component);
                    } catch (IllegalArgumentException e) {
                        throw new WebApplicationException(e);
                    } catch (IllegalAccessException e) {
                        throw new WebApplicationException(e);
                    } catch (InvocationTargetException e) {
                        throw new WebApplicationException(e);
                    }
                }
            }
            DOMBuilder b;
            Buildable bd = annotations.get(cl);
            if (bd.wrap()) {
                b = sb.descend(getBuildName(cl));
            } else {
                b = sb;
            }
            for (Builder builder : getBuilder(cl)) {
                builder.build(b, component);
            }

            // Handling buildins
            
            if (buildins != null) {
                for (Object buildIn : buildins) {
                    if (buildIn != null) {
                        Class<?> bcl = getActualClass(buildIn);
                        if (isBuildable(bcl)) {
                            for (Builder builder : getBuilder(bcl)) {
                                builder.build(b, buildIn);
                            }
                        }
                    }
                }
            }

            if (afterBuilds.containsKey(cl)) {
                for (Method method : afterBuilds.get(cl)) {
                    try {
                        method.invoke(component);
                    } catch (IllegalArgumentException e) {
                        throw new WebApplicationException(e);
                    } catch (IllegalAccessException e) {
                        throw new WebApplicationException(e);
                    } catch (InvocationTargetException e) {
                        throw new WebApplicationException(e);
                    }
                }
            }
        } else {
            sb.text(attributeHandler.serialize(component));
        }
    }

    @Override
    public void buildUpdate(DOMBuilder sb, Component component, String updateName) {
        Class<?> cl = getActualClass(component);
        if (isBuildable(cl)) {
            DOMBuilder b = sb.descend(getBuildName(cl) + "." + updateName);
            List<Builder> builders = getUpdateBuilder(cl);

            for (Builder builder : builders) {
                builder.build(b, component);
            }
        }
    }

    @Override
    public void buildPartialUpdate(DOMBuilder sb, Component component, String updateName, Set<String> updates) {

        Class<?> cl = getActualClass(component);

        DOMBuilder b = sb.descend(getBuildName(cl) + "." + updateName);

        List<Builder> builders = getPartialUpdateBuilder(cl);

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
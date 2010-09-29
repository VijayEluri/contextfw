package net.contextfw.web.application.elements.enhanced;

import java.beans.IntrospectionException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.elements.CSimpleElement;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class EnhancedElementBuilder {

    @Inject
    private Injector injector;

    @SuppressWarnings("unchecked")
    private Map<Class<?>, List<EmbeddedBuilder>> builders = new HashMap<Class<?>, List<EmbeddedBuilder>>();
    @SuppressWarnings("unchecked")
    private Map<Class<?>, List<EmbeddedBuilder>> updateBuilders = new HashMap<Class<?>, List<EmbeddedBuilder>>();

    @SuppressWarnings("unchecked")
    private synchronized List<EmbeddedBuilder> getBuilder(Class<?> cl) {

        if (builders.containsKey(cl)) {
            return builders.get(cl);
        }

        builders.put(cl, new ArrayList<EmbeddedBuilder>());
        updateBuilders.put(cl, new ArrayList<EmbeddedBuilder>());

        try {
            addEmbeddeds(cl);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (IntrospectionException e) {
            e.printStackTrace();
        }

        return builders.get(cl);
    }

    @SuppressWarnings("unchecked")
    private void addEmbeddeds(Class<?> cl) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, IntrospectionException {

        Class<?> currentClass = cl;

        while (currentClass instanceof Object) {

            for (Field field : currentClass.getDeclaredFields()) {
                {
                    EmbeddedElement embeddedElement = field.getAnnotation(EmbeddedElement.class);

                    if (embeddedElement != null) {
                        String name = "".equals(embeddedElement.name()) ? field.getName() : embeddedElement.name();
                        ElementBuilder elementBuilder = injector.getInstance(embeddedElement.builder());
                        EmbeddedElementBuilder builder = new EmbeddedElementBuilder(field, name, elementBuilder);
                        builder.addModes(embeddedElement.updateModes());
                        addToBuilders(cl, embeddedElement.phase(), builder);
                    }
                }
                {
                    EmbeddedAttribute embeddedAttribute = field.getAnnotation(EmbeddedAttribute.class);
                    if (embeddedAttribute != null) {
                        String name = "".equals(embeddedAttribute.name()) ? field.getName() : embeddedAttribute.name();
                        AttributeConverter converter = injector.getInstance(embeddedAttribute.converter());
                        EmbeddedAttributeBuilder builder = new EmbeddedAttributeBuilder(field, name, converter);
                        builder.addModes(embeddedAttribute.updateModes());                        
                        addToBuilders(cl, embeddedAttribute.phase(), builder);
                    }
                }
                {
                    EmbeddedCollection embeddedCollection = field.getAnnotation(EmbeddedCollection.class);

                    if (embeddedCollection != null) {
                        
                        String name = "".equals(embeddedCollection.name()) ? field.getName() : embeddedCollection.name();
                        String elementName = embeddedCollection.elementName();
                        
                        ElementBuilder elementBuilder = injector.getInstance(embeddedCollection.elementBuilder());
                        EmbeddedElementBuilder embeddedBuilder = new EmbeddedElementBuilder((Field) null, elementName, elementBuilder);
                        EmbeddedCollectionBuilder builder = new EmbeddedCollectionBuilder(field, name, embeddedBuilder);
                        addToBuilders(cl, embeddedCollection.phase(), builder);
                    }
                }
                {
                    CustomBuild customBuild = field.getAnnotation(CustomBuild.class);

                    if (customBuild != null) {
                        CustomBuilder builder = new CustomBuilder(field);
                        builder.addModes(customBuild.updateModes());
                        addToBuilders(cl, customBuild.phase(), builder);
                    }
                }
            }

            for (Method method : currentClass.getDeclaredMethods()) {
                {
                    EmbeddedElement embeddedElement = method.getAnnotation(EmbeddedElement.class);

                    if (embeddedElement != null) {
                        String name = "".equals(embeddedElement.name()) ? method.getName() : embeddedElement.name();
                        ElementBuilder elementBuilder = injector.getInstance(embeddedElement.builder());
                        EmbeddedElementBuilder builder = new EmbeddedElementBuilder(method, name, elementBuilder);
                        builder.addModes(embeddedElement.updateModes());
                        addToBuilders(cl, embeddedElement.phase(), builder);
                    }
                }
                {
                    EmbeddedAttribute embeddedAttribute = method.getAnnotation(EmbeddedAttribute.class);
                    if (embeddedAttribute != null) {
                        String name = "".equals(embeddedAttribute.name()) ? method.getName() : embeddedAttribute.name();
                        AttributeConverter converter = injector.getInstance(embeddedAttribute.converter());
                        EmbeddedAttributeBuilder builder = new EmbeddedAttributeBuilder(method, name, converter);
                        builder.addModes(embeddedAttribute.updateModes());                        
                        addToBuilders(cl, embeddedAttribute.phase(), builder);
                    }
                }
                {
                    EmbeddedCollection embeddedCollection = method.getAnnotation(EmbeddedCollection.class);

                    if (embeddedCollection != null) {
                        
                        String name = "".equals(embeddedCollection.name()) ? method.getName() : embeddedCollection.name();
                        String elementName = embeddedCollection.elementName();
                        
                        ElementBuilder elementBuilder = injector.getInstance(embeddedCollection.elementBuilder());
                        EmbeddedElementBuilder embeddedBuilder = new EmbeddedElementBuilder((Method) null, elementName, elementBuilder);
                        EmbeddedCollectionBuilder builder = new EmbeddedCollectionBuilder(method, name, embeddedBuilder);
                        builder.addModes(embeddedCollection.updateModes());
                        addToBuilders(cl, embeddedCollection.phase(), builder);
                    }
                }
                {
                    CustomBuild customBuild = method.getAnnotation(CustomBuild.class);

                    if (customBuild != null) {
                        CustomBuilder builder = new CustomBuilder(method);
                        builder.addModes(customBuild.updateModes());
                        addToBuilders(cl, customBuild.phase(), builder);
                    }
                }
                {
                    PartialUpdate partialUpdate = method.getAnnotation(PartialUpdate.class);

                    if (partialUpdate != null) {
                        PartialUpdateBuilder builder = new PartialUpdateBuilder(partialUpdate, method);
                        addToBuilders(cl, BuildPhase.PARTIAL, builder);
                    }
                }
            }

            currentClass = currentClass.getSuperclass();
        }
    }

    @SuppressWarnings("unchecked")
    private void addToBuilders(Class<?> cl, BuildPhase phase, EmbeddedBuilder builder) {
        if (phase == BuildPhase.BOTH) {
            
            builders.get(cl).add(builder);
            updateBuilders.get(cl).add(builder);
        }
        else if (phase == BuildPhase.CREATE) {
            builders.get(cl).add(builder);
        }
        else if (phase == BuildPhase.UPDATE) {
            updateBuilders.get(cl).add(builder);
        }
        else if (phase == BuildPhase.PARTIAL) {
            updateBuilders.get(cl).add(builder);
        }
    }

    @SuppressWarnings("unchecked")
    private List<EmbeddedBuilder> getUpdateBuilder(Class<?> cl) {
        if (updateBuilders.containsKey(cl)) {
            return updateBuilders.get(cl);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void build(DOMBuilder b, CSimpleElement element) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        Class<?> cl = getActualClass(element);
        for (EmbeddedBuilder builder : getBuilder(cl)) {
            builder.build(b, element);
        }
    }

    @SuppressWarnings("unchecked")
    public void buildUpdate(DOMBuilder b, CElement element, Set<String> updateModes) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Class<?> cl = getActualClass(element);
        
        List<EmbeddedBuilder> builders = getUpdateBuilder(cl);
        
        for (EmbeddedBuilder builder : builders) {
            if (builder.isUpdateBuildable(updateModes)) {
                builder.build(b, element);
            }
        }
    }

    public Class<?> getActualClass(CSimpleElement element) {
        Class<?> cl = element.getClass();
        while (cl.getSimpleName().contains("EnhancerByGuice")) {
            cl = cl.getSuperclass();
        }
        return cl;
    }
}
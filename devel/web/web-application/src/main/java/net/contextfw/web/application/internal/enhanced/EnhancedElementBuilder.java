package net.contextfw.web.application.internal.enhanced;

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
import net.contextfw.web.application.dom.DOMBuilder;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.elements.CSimpleElement;
import net.contextfw.web.application.elements.enhanced.BuildPhase;
import net.contextfw.web.application.elements.enhanced.CustomBuild;
import net.contextfw.web.application.elements.enhanced.EmbeddedAttribute;
import net.contextfw.web.application.elements.enhanced.EmbeddedElement;

import com.google.inject.Singleton;

@Singleton
public class EnhancedElementBuilder {

    private Map<Class<?>, List<Builder>> builders = new HashMap<Class<?>, List<Builder>>();
    private Map<Class<?>, List<Builder>> updateBuilders = new HashMap<Class<?>, List<Builder>>();

    private synchronized List<Builder> getBuilder(Class<?> cl) {

        if (builders.containsKey(cl)) {
            return builders.get(cl);
        }

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

        return builders.get(cl);
    }

    @SuppressWarnings("unchecked")
    private void addEmbeddeds(Class<?> cl) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, IntrospectionException {

        Class<?> currentClass = cl;

        while (currentClass instanceof Object) {

            for (Field field : currentClass.getDeclaredFields()) {
                
                PropertyAccess<Object> propertyAcess = new FieldPropertyAccess<Object>(field);
                String name = null;
                String[] updateModes = null;
                BuildPhase phase = null;
                Builder builder = null;
                
                {
                    EmbeddedElement embeddedElement = field.getAnnotation(EmbeddedElement.class);
                    if (embeddedElement != null) {
                        name = "".equals(embeddedElement.name()) ? field.getName() : embeddedElement.name();
                        builder = new ElementBuilder(propertyAcess, name);
                        updateModes = embeddedElement.updateModes();
                        phase = embeddedElement.phase();
                    }
                }
                {
                    EmbeddedAttribute embeddedAttribute = field.getAnnotation(EmbeddedAttribute.class);
                    if (embeddedAttribute != null) {
                        name = "".equals(embeddedAttribute.name()) ? field.getName() : embeddedAttribute.name();
                        builder = new AttributeBuilder(new FieldPropertyAccess<Object>(field), name);
                        updateModes = embeddedAttribute.updateModes();
                        phase = embeddedAttribute.phase();   
                    }
                }
//                {
//                    EmbeddedCollection embeddedCollection = field.getAnnotation(EmbeddedCollection.class);
//
//                    if (embeddedCollection != null) {
//                        
//                        String name = "".equals(embeddedCollection.name()) ? field.getName() : embeddedCollection.name();
//                        String elementName = embeddedCollection.elementName();
//                        
//                        ElementBuilder elementBuilder = injector.getInstance(embeddedCollection.elementBuilder());
//                        EmbeddedElementBuilder embeddedBuilder = new EmbeddedElementBuilder((Field) null, elementName, elementBuilder);
//                        EmbeddedCollectionBuilder builder = new EmbeddedCollectionBuilder(field, name, embeddedBuilder);
//                        addToBuilders(cl, embeddedCollection.phase(), builder);
//                    }
//                }
                {
                    CustomBuild customBuild = field.getAnnotation(CustomBuild.class);
                    if (customBuild != null) {
                        builder = new FieldCustomBuilder(field);
                        updateModes = customBuild.updateModes();
                        phase = customBuild.phase();
                    }
                }
                
                if (builder != null) {
                    builder.addModes(updateModes);                        
                    addToBuilders(cl, phase, builder);
                }
            }

            for (Method method : currentClass.getDeclaredMethods()) {
                
                PropertyAccess<Object> propertyAccess = new MethodPropertyAccess(method);
                String name = null;
                String[] updateModes = null;
                BuildPhase phase = null;
                Builder builder = null;
                
                {
                    EmbeddedElement embeddedElement = method.getAnnotation(EmbeddedElement.class);

                    if (embeddedElement != null) {
                        name = "".equals(embeddedElement.name()) ? method.getName() : embeddedElement.name();
                        builder = new ElementBuilder(propertyAccess, name);
                        updateModes = embeddedElement.updateModes();
                        phase = embeddedElement.phase();
                    }
                }
                {
                    EmbeddedAttribute embeddedAttribute = method.getAnnotation(EmbeddedAttribute.class);
                    if (embeddedAttribute != null) {
                        name = "".equals(embeddedAttribute.name()) ? method.getName() : embeddedAttribute.name();
                        builder = new AttributeBuilder(propertyAccess, name);
                        updateModes = embeddedAttribute.updateModes();                        
                        phase = embeddedAttribute.phase();
                    }
                }
                {
                    CustomBuild customBuild = method.getAnnotation(CustomBuild.class);

                    if (customBuild != null) {
                        builder = new MethodCustomBuilder(method);
                        updateModes = customBuild.updateModes();
                        phase = customBuild.phase();
                    }
                }
//                {
//                    PartialUpdate partialUpdate = method.getAnnotation(PartialUpdate.class);
//
//                    if (partialUpdate != null) {
//                        PartialUpdateBuilder builder = new PartialUpdateBuilder(partialUpdate, method);
//                        addToBuilders(cl, BuildPhase.PARTIAL, builder);
//                    }
//                }
                
                if (builder != null) {
                    builder.addModes(updateModes);                        
                    addToBuilders(cl, phase, builder);
                }
            }

            currentClass = currentClass.getSuperclass();
        }
    }

    private void addToBuilders(Class<?> cl, BuildPhase phase, Builder builder) {
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

    private List<Builder> getUpdateBuilder(Class<?> cl) {
        if (updateBuilders.containsKey(cl)) {
            return updateBuilders.get(cl);
        }
        return null;
    }

    public void build(DOMBuilder b, CSimpleElement element) {
        Class<?> cl = getActualClass(element);
        for (Builder builder : getBuilder(cl)) {
            builder.build(b, element);
        }
    }

    public void buildUpdate(DOMBuilder b, CElement element, Set<String> updateModes) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Class<?> cl = getActualClass(element);
        
        List<Builder> builders = getUpdateBuilder(cl);
        
        for (Builder builder : builders) {
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
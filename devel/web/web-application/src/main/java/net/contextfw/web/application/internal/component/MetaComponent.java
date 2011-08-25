/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.application.internal.component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Attribute;
import net.contextfw.web.application.component.Buildable;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.CustomBuild;
import net.contextfw.web.application.component.Element;
import net.contextfw.web.application.component.ScriptContext;
import net.contextfw.web.application.component.ScriptElement;
import net.contextfw.web.application.internal.servlet.UriMapping;
import net.contextfw.web.application.lifecycle.AfterBuild;
import net.contextfw.web.application.lifecycle.BeforeBuild;
import net.contextfw.web.application.remote.PathParam;
import net.contextfw.web.application.remote.RequestParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public final class MetaComponent {

    private static Map<Class<?>, Class<?>> primitives = new HashMap<Class<?>, Class<?>>();

    static {
        primitives.put(boolean.class, Boolean.class);
        primitives.put(byte.class, Byte.class);
        primitives.put(char.class, Character.class);
        primitives.put(short.class, Short.class);
        primitives.put(int.class, Integer.class);
        primitives.put(long.class, Long.class);
        primitives.put(float.class, Float.class);
        primitives.put(double.class, Double.class);
    }

    Logger log = LoggerFactory.getLogger(MetaComponent.class);

    private final Set<String> registeredNames = new HashSet<String>();
    private final List<Method> beforeBuilds = new ArrayList<Method>();
    private final List<Method> afterBuilds = new ArrayList<Method>();
    public final List<Builder> builders = new ArrayList<Builder>();
    public final List<Builder> updateBuilders = new ArrayList<Builder>();
    public final List<Builder> partialBuilders = new ArrayList<Builder>();
    private final List<Field> pathParamFields = new ArrayList<Field>();
    private final List<Method> pathParamMethods = new ArrayList<Method>();
    private final List<Field> requestParamFields = new ArrayList<Field>();
    private final List<Method> requestParamMethods = new ArrayList<Method>();
    private final List<Field> autoregisterFields = new ArrayList<Field>();
    
    public final String buildName;
    public final Buildable annotation;

    private final Class<?> cl;
    private final ComponentBuilder componentBuilder;
    private final Gson gson;
    private final ScriptContext scriptContext;

    public MetaComponent(Class<?> rawCl,
                         ComponentBuilder componentBuilder,
                         Gson gson,
                         ScriptContext scriptContext) {
        cl = getActualClass(rawCl);
        this.componentBuilder = componentBuilder;
        this.gson = gson;
        this.scriptContext = scriptContext;
        annotation = findBuildable();
        if (annotation != null) {
            buildName = getBuildableName();
            iterateFields();
            iterateMethods();
        } else {
            buildName = null;
        }
    }

    private final boolean processFieldBuilders(Field field) {
        PropertyAccess<Object> propertyAccess =
                new FieldPropertyAccess<Object>(field);

        String name = null;
        Builder builder = null;

        if (field.getAnnotation(Element.class) != null) {
            Element element = field.getAnnotation(Element.class);
            name = "".equals(element.name()) ? field.getName()
                    : element.name();
            builder = new ElementBuilder(componentBuilder, propertyAccess,
                    element.wrap() ? name : null, field.getName());
            addToBuilders(element.onCreate(), element.onUpdate(), builder);
            if (element.autoRegister()) {
                autoregisterFields.add(field);
            }
        } else if (field.getAnnotation(Attribute.class) != null) {
            Attribute attribute = field.getAnnotation(Attribute.class);
            name = "".equals(attribute.name()) ? field.getName()
                    : attribute.name();
            builder = new AttributeBuilder(propertyAccess, name,
                    field.getName());
            addToBuilders(attribute.onCreate(), attribute.onUpdate(), builder);
        } else if (field.getAnnotation(ScriptElement.class) != null) {
            ScriptElement scriptElement = field
                    .getAnnotation(ScriptElement.class);
            name = scriptElement.wrapper();
            builder = new ScriptElementBuilder(scriptContext, gson,
                    propertyAccess, name, field.getName());
            addToBuilders(scriptElement.onCreate(), scriptElement.onUpdate(), builder);
        }

        return builder != null;
    }

    private boolean processMethodBuilders(Method method) {
        String name = null;
        Builder builder = null;

        if (method.getAnnotation(Element.class) != null) {
            Element element = method.getAnnotation(Element.class);
            name = "".equals(element.name()) ? method.getName()
                    : element.name();
            builder = new ElementBuilder(componentBuilder, new MethodPropertyAccess(
                    method),
                    element.wrap() ? name : null, method.getName());
            addToBuilders(element.onCreate(), element.onUpdate(), builder);
        } else if (method.getAnnotation(Attribute.class) != null) {
            Attribute attribute = method
                    .getAnnotation(Attribute.class);
            name = "".equals(attribute.name()) ? method.getName()
                    : attribute.name();
            builder = new AttributeBuilder(new MethodPropertyAccess(
                    method), name,
                    method.getName());
            addToBuilders(attribute.onCreate(), attribute.onUpdate(), builder);
        } else if (method.getAnnotation(CustomBuild.class) != null) {
            CustomBuild customBuild = method
                    .getAnnotation(CustomBuild.class);
            name = "".equals(customBuild.name()) ? method.getName()
                    : customBuild.name();
            builder = new MethodCustomBuilder(method,
                    customBuild.wrap() ? name : null);
            addToBuilders(customBuild.onCreate(), customBuild.onUpdate(), builder);
        } else if (method.getAnnotation(ScriptElement.class) != null) {
            ScriptElement scriptElement = method
                    .getAnnotation(ScriptElement.class);
            name = scriptElement.wrapper();
            builder = new ScriptElementBuilder(scriptContext, gson,
                    new MethodPropertyAccess(method), name, method.getName());
            addToBuilders(scriptElement.onCreate(), scriptElement.onUpdate(), builder);
        }
        return builder != null;
    }

    private boolean canProcess(Field field) {
        return !registeredNames.contains(field.getName());
    }

    private void setProcessed(Field field) {
        registeredNames.add(field.getName());
    }

    private boolean canProcess(Method method) {
        return !registeredNames.contains(method.getName());
    }

    private void setProcessed(Method method) {
        registeredNames.add(method.getName());
    }

    private final void iterateFields() {
        Class<?> currentClass = cl;
        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (canProcess(field) && processFieldBuilders(field)) {
                    setProcessed(field);
                }
                if (canProcess(field) && processPathParam(field)) {
                    setProcessed(field);
                }
                if (canProcess(field) && processRequestParam(field)) {
                    setProcessed(field);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private final void iterateMethods() {
        Class<?> currentClass = cl;
        while (currentClass != null) {
            for (Method method : currentClass.getDeclaredMethods()) {
                method.setAccessible(true);
                if (canProcess(method) && processMethodBuilders(method)) {
                    setProcessed(method);
                }
                if (canProcess(method) && processBeforeBuilds(method)) {
                    setProcessed(method);
                }
                if (canProcess(method) && processAfterBuilds(method)) {
                    setProcessed(method);
                }
                if (canProcess(method) && processPathParam(method)) {
                    setProcessed(method);
                }
                if (canProcess(method) && processRequestParam(method)) {
                    setProcessed(method);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    public static Class<?> getActualClass(Class<?> cl) {
        Class<?> actual = cl;
        while (actual.getSimpleName().contains("EnhancerByGuice")) {
            actual = actual.getSuperclass();
        }
        return actual;
    }

    private Buildable findBuildable() {
        Class<?> current = cl;
        while (current != null) {
            if (current.isAnnotationPresent(Buildable.class)) {
                return current.getAnnotation(Buildable.class);
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private String getBuildableName() {
        if (annotation.wrap()) {
            return ("".equals(annotation.name()) ? cl
                        .getSimpleName() : annotation.name());
        } else {
            return null;
        }
    }

    private void addToBuilders(boolean onCreate,
                               boolean onUpdate,
                               Builder builder) {
        if (onCreate) {
            builders.add(builder);
        }
        if (onUpdate) {
            updateBuilders.add(builder);
        }
        partialBuilders.add(builder);
    }

    public boolean processBeforeBuilds(Method method) {
        if (method.getAnnotation(BeforeBuild.class) != null) {
            beforeBuilds.add(method);
            return true;
        } else {
            return false;
        }
    }

    public boolean processAfterBuilds(Method method) {
        if (method.getAnnotation(AfterBuild.class) != null) {
            afterBuilds.add(method);
            return true;
        } else {
            return false;
        }
    }

    public boolean processPathParam(Field field) {
        if (field.isAnnotationPresent(PathParam.class)) {
            if (!primitives.containsKey(field.getType())) {
                try {
                    field.getType().getConstructor(String.class);
                } catch (SecurityException e) {
                    throw new WebApplicationException(e);
                } catch (NoSuchMethodException e) {
                    throw new WebApplicationException(field,
                            "@PathParam-annotated field " +
                                    "type does not contain constructor " +
                                    "having String as parameter", e);
                }
            }
            pathParamFields.add(field);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean processRequestParam(Field field) {
        if (field.isAnnotationPresent(RequestParam.class)) {
            if (!primitives.containsKey(field.getType())) {
                try {
                    field.getType().getConstructor(String.class);
                } catch (SecurityException e) {
                    throw new WebApplicationException(e);
                } catch (NoSuchMethodException e) {
                    throw new WebApplicationException(field,
                            "@RequestParam-annotated field " +
                                    "type does not contain constructor " +
                                    "having String as parameter", e);
                }
            }
            requestParamFields.add(field);
            return true;
        } else {
            return false;
        }
    }

    public boolean processPathParam(Method method) {
        if (method.isAnnotationPresent(PathParam.class)) {
            Class<?>[] types = method.getParameterTypes();
            if (types.length != 1) {
                throw new WebApplicationException(method,
                        "@PathParam annotated method does not take 1 parameter", null);
            }
            if (!primitives.containsKey(types[0])) {
                try {
                    types[0].getConstructor(String.class);
                } catch (SecurityException e) {
                    throw new WebApplicationException(e);
                } catch (NoSuchMethodException e) {
                    throw new WebApplicationException(method,
                            "@PathParam-annotated method parameter " +
                                    "type does not contain constructor " +
                                    "having String as parameter", e);
                }
            }
            pathParamMethods.add(method);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean processRequestParam(Method method) {
        if (method.isAnnotationPresent(RequestParam.class)) {
            Class<?>[] types = method.getParameterTypes();
            if (types.length != 1) {
                throw new WebApplicationException(method,
                        "@RequestParam annotated method does not take 1 parameter", null);
            }
            if (!primitives.containsKey(types[0])) {
                try {
                    types[0].getConstructor(String.class);
                } catch (SecurityException e) {
                    throw new WebApplicationException(e);
                } catch (NoSuchMethodException e) {
                    throw new WebApplicationException(method,
                            "@RequestParam-annotated method parameter " +
                                    "type does not contain constructor " +
                                    "having String as parameter", e);
                }
            }
            requestParamMethods.add(method);
            return true;
        } else {
            return false;
        }
    }

    public void applyBeforeBuilds(Object obj) {
        for (Method method : beforeBuilds) {
            try {
                method.invoke(obj);
            } catch (IllegalArgumentException e) {
                throw new WebApplicationException(e);
            } catch (IllegalAccessException e) {
                throw new WebApplicationException(e);
            } catch (InvocationTargetException e) {
                throw new WebApplicationException(e);
            }
        }
    }

    public void applyAfterBuilds(Object obj) {
        for (Method method : afterBuilds) {
            try {
                method.invoke(obj);
            } catch (IllegalArgumentException e) {
                throw new WebApplicationException(e);
            } catch (IllegalAccessException e) {
                throw new WebApplicationException(e);
            } catch (InvocationTargetException e) {
                throw new WebApplicationException(e);
            }
        }
    }

    public void applyRequestParams(Object obj,
                                   HttpServletRequest request) {
        for (Field field : requestParamFields) {
            RequestParam annotation = field.getAnnotation(RequestParam.class);
            String name = "".equals(annotation.name()) ? field.getName() : annotation.name();
            try {
                field.set(obj, getValue(annotation,
                                        field.getType(),
                                        name,
                                        request));
            } catch (Exception e) {
                if (e instanceof WebApplicationException) {
                    throw (RuntimeException) e;
                } else {
                    throw new WebApplicationException(e);
                }
            }
        }
        for (Method method : requestParamMethods) {
            RequestParam annotation = method.getAnnotation(RequestParam.class);
            String name = "".equals(annotation.name()) ? method.getName() : annotation.name();
            try {
                method.invoke(obj, getValue(annotation,
                                        method.getParameterTypes()[0],
                                        name,
                                        request));
            } catch (Exception e) {
                if (e instanceof WebApplicationException) {
                    throw (RuntimeException) e;
                } else {
                    throw new WebApplicationException(e);
                }
            }
        }
    }
    
    public void applyPathParams(Object obj,
                                UriMapping mapping,
                                String uri) {

        for (Field field : pathParamFields) {
            PathParam annotation = field.getAnnotation(PathParam.class);
            String name = "".equals(annotation.name()) ? field.getName() : annotation.name();
            try {
                field.set(obj, getValue(annotation,
                                        field.getType(),
                                        name,
                                        mapping,
                                        uri));
            } catch (Exception e) {
                if (e instanceof WebApplicationException) {
                    throw (RuntimeException) e;
                } else {
                    throw new WebApplicationException(e);
                }
            }
        }
        for (Method method : pathParamMethods) {
            PathParam annotation = method.getAnnotation(PathParam.class);
            String name = "".equals(annotation.name()) ? method.getName() : annotation.name();
            try {
                method.invoke(obj, getValue(annotation,
                                        method.getParameterTypes()[0],
                                        name,
                                        mapping,
                                        uri));
            } catch (Exception e) {
                if (e instanceof WebApplicationException) {
                    throw (RuntimeException) e;
                } else {
                    throw new WebApplicationException(e);
                }
            }
        }
    }

    private Object getValue(RequestParam annotation,
                            Class<?> type,
                            String name, 
                            HttpServletRequest request) {
        
        String val = request.getParameter(name);
        
        if (val == null) {
            switch (annotation.onNull()) {
            case SET_TO_NULL:
                return null;
            case RETHROW_CAUSE:
                throw new WebApplicationException(cl, "Null value for request param: " + name, null);
            case SEND_NOT_FOUND_ERROR:
            case SEND_BAD_REQUEST_ERROR:
                throw new MetaComponentException(annotation.onNull());
            }
        }
        Object rv = null;
        if (String.class == type) {
            rv = val;
        } else {
            try {
                if (primitives.containsKey(type)) {
                    rv = primitives.get(type).getConstructor(String.class).newInstance(val);
                } else {
                    rv = type.getConstructor(String.class).newInstance(val);
                }
            } catch (Exception e) {
                switch (annotation.onError()) {
                case SET_TO_NULL:
                    return null;
                case RETHROW_CAUSE:
                    throw new WebApplicationException(e);
                case SEND_NOT_FOUND_ERROR:
                case SEND_BAD_REQUEST_ERROR:
                    throw new MetaComponentException(annotation.onError());
                }
            }
        }
        return rv;
    }
    
    private Object getValue(PathParam annotation,
                            Class<?> type,
                            String name,
                            UriMapping mapping,
                            String uri) {

        String val = mapping.findValue(uri, name);
        if (val == null) {
            switch (annotation.onNull()) {
            case SET_TO_NULL:
                return null;
            case RETHROW_CAUSE:
                throw new WebApplicationException(cl, "Null value for path param: " + name, null);
            case SEND_NOT_FOUND_ERROR:
            case SEND_BAD_REQUEST_ERROR:
                throw new MetaComponentException(annotation.onNull());
            }
        }
        Object rv = null;
        if (String.class == type) {
            rv = val;
        } else {
            try {
                if (primitives.containsKey(type)) {
                    rv = primitives.get(type).getConstructor(String.class).newInstance(val);
                } else {
                    rv = type.getConstructor(String.class).newInstance(val);
                }
            } catch (Exception e) {
                switch (annotation.onError()) {
                case SET_TO_NULL:
                    return null;
                case RETHROW_CAUSE:
                    throw new WebApplicationException(e);
                case SEND_NOT_FOUND_ERROR:
                case SEND_BAD_REQUEST_ERROR:
                    throw new MetaComponentException(annotation.onError());
                }
            }
        }
        return rv;
    }
    
    public void registerChildren(Component parent) {
        for (Field field : autoregisterFields) {
            try {
                Object child = field.get(parent);
                if (child instanceof Component) {
                    parent.registerChild((Component)child);
                }
            } catch (IllegalArgumentException e) {
                throw new WebApplicationException(e);
            } catch (IllegalAccessException e) {
                throw new WebApplicationException(e);
            }
        }
    }
}
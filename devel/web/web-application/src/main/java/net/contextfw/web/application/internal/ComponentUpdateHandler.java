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

package net.contextfw.web.application.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.util.ClassScanner;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.remote.Delayed;
import net.contextfw.web.application.remote.ResourceBody;

import com.google.gson.Gson;

public class ComponentUpdateHandler {

    private final Gson gson;
    private final String key;
    private final Method method;
    private final Delayed delayed;
    private final boolean resource;
    private final LifecycleListener listener;

    public ComponentUpdateHandler(String key, Method method, Gson gson, LifecycleListener listener) {
        this.key = key;
        this.method = method;
        this.gson = gson;
        this.delayed = method.getAnnotation(Delayed.class);
        this.resource = method.getAnnotation(ResourceBody.class) != null;
        this.listener = listener;
    }

    public static String getKey(Class<? extends Component> elClass, String methodName) {
        return elClass.getCanonicalName() + "." + methodName;
    }

    public String getKey() {
        return key;
    }

    public Object invoke(Component rootComponent, Component element, HttpServletRequest request) {
        try {
            if (element != null && element.isEnabled()) {
                return invokeWithParams(rootComponent, element, request);
            }
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e);
        } catch (IllegalAccessException e) {
            throw new WebApplicationException(e);
        } catch (InstantiationException e) {
            throw new WebApplicationException(e);
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object invokeWithParams(Component rootComponent, Component component, HttpServletRequest request)
            throws IllegalAccessException, InstantiationException {

        List<Class<?>> paramTypes = ClassScanner.getParamTypes(component.getClass(), method);
        Object[] params = new Object[paramTypes.size()];
        RuntimeException thrown = null;
        Object returnVal = null;
        try {
            for (int c = 0; c < paramTypes.size(); c++) {
    
                String value = request.getParameter("p" + c);
                if (value != null) {
                    Class<?> type = paramTypes.get(c);
                    try {
                        if (type.isEnum()) {
                            params[c] = Enum.valueOf((Class<Enum>) type, value);
                        } else {
                            Constructor<?> constructor = type.getConstructor(String.class);
                            params[c] = constructor.newInstance(value);
                        }
                    } catch (NoSuchMethodException e) {
                        params[c] = gson.fromJson(value, type);
                    } catch (InvocationTargetException ie) {
                        throw new WebApplicationException(ie);
                    }
                }
            }
            if (listener.beforeRemotedMethod(component, method, params)) {
                returnVal = method.invoke(component, params);
            } else {
                return null;
            }
        } catch (RuntimeException e) {
            rootComponent.clearCascadedUpdate();
            thrown = e;
        } catch (InvocationTargetException e) {
            thrown = new WebApplicationException(e);
        }
        listener.afterRemoteMethod(component, method, thrown);
        return returnVal;
    }

    public Delayed getDelayed() {
        return delayed;
    }

    public boolean isResource() {
        return resource;
    }
}
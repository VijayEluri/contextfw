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

    public Object invoke(Component element, HttpServletRequest request) {
        try {
            if (element != null && element.isEnabled()) {
                return invokeWithParams(element, request);
            }
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e);
        } catch (IllegalAccessException e) {
            throw new WebApplicationException(e);
        } catch (InvocationTargetException e) {
            throw new WebApplicationException(e);
        } catch (NoSuchMethodException e) {
            throw new WebApplicationException(e);
        } catch (InstantiationException e) {
            throw new WebApplicationException(e);
        }
        return null;
    }

    private Object invokeWithParams(Component element, HttpServletRequest request)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            InstantiationException {

        List<Class<?>> paramTypes = ClassScanner.getParamTypes(element.getClass(), method);
        Object[] params = new Object[paramTypes.size()];

        for (int c = 0; c < paramTypes.size(); c++) {

            String value = request.getParameter("p" + c);
            if (value != null) {
                try {
                    Constructor<?> constructor = paramTypes.get(c).getConstructor(String.class);
                    params[c] = constructor.newInstance(value);
                } catch (Exception e) {
                    try {
                        params[c] = gson.fromJson(value, paramTypes.get(c));
                    } catch (RuntimeException e1) {
                        throw new WebApplicationException(e1);
                    }
                }
            }
        }
        if (listener.beforeRemotedMethod(element, method, params)) {
            return method.invoke(element, params);
        } else {
            return null;
        }
    }

    public Delayed getDelayed() {
        return delayed;
    }

    public boolean isResource() {
        return resource;
    }
}
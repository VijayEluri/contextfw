package net.contextfw.web.application.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.remote.Delayed;
import net.contextfw.web.application.remote.ResourceBody;
import net.contextfw.web.application.util.Request;

import com.google.gson.Gson;

public class ComponentUpdateHandler {

    private final Gson gson;
    private final String key;
    private final Method method;
    private final Delayed delayed;
    private final boolean resource;

    public ComponentUpdateHandler(String key, Method method, Gson gson) {
        this.key = key;
        this.method = method;
        this.gson = gson;
        this.delayed = method.getAnnotation(Delayed.class);
        this.resource = method.getAnnotation(ResourceBody.class) != null;
    }

    public static String getKey(Class<? extends Component> elClass, String methodName) {
        return elClass.getCanonicalName() + "." + methodName;
    }

    public String getKey() {
        return key;
    }

    public Object invoke(Component element, Request request) {
        try {
            if (element != null && element.isEnabled()) {
                return invokeWithParams(element, request);
            }
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            if (RuntimeException.class.isAssignableFrom(e.getCause().getClass())) {
                throw (RuntimeException) e.getCause();
            }
            else {
                e.printStackTrace();
            }
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object invokeWithParams(Component element, Request request) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {

        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] params = new Object[paramTypes.length];

        for (int c = 0; c < paramTypes.length; c++) {
            String value = request.param("p" + c).getStringValue(null);
            if (value != null) {
                try {
                    Constructor<?> constructor = paramTypes[c].getConstructor(String.class);
                    params[c] = constructor.newInstance(value);
                }
                catch (Exception e) {
                    try {
                        params[c] = gson.fromJson(value, paramTypes[c]);
                    }
                    catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        return method.invoke(element, params);
    }

    public Delayed getDelayed() {
        return delayed;
    }

    public boolean isResource() {
        return resource;
    }
}
package net.contextfw.web.application;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A general exception thrown by application
 */ 
public class WebApplicationException extends RuntimeException {
    private static final long serialVersionUID = -3864752109086700032L;

    public WebApplicationException() {
        super();
    }

    public WebApplicationException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public WebApplicationException(String msg) {
        super(msg);
    }

    public WebApplicationException(Throwable arg0) {
        super(arg0);
    }
    
    public WebApplicationException(Class<?> cl, String msg) {
        super(cl.getName() +":" + msg);
    }
    
    public WebApplicationException(Method method, String msg) {
        super(method.getDeclaringClass().getName()+"."+method.getName()+"():" + msg);
    }
    
    public WebApplicationException(Field field, String msg) {
        super(field.getDeclaringClass().getName()+"."+field.getName()+":" + msg);
    }

}

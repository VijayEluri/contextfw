package net.contextfw.web.application.internal.initializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.initializer.InitializerContext;
import net.contextfw.web.application.internal.InternalWebApplicationException;

import com.google.inject.Injector;

public class InitializerContextImpl implements InitializerContext {

    private final List<Class<? extends CElement>> chain;
    private final Injector injector;
    
    private int currentIndex = 0;
    
    private String redirectUrl = null;
    private Integer errorCode = null;
    private String errorMsg = null;
    private Locale locale = null;
    
    public InitializerContextImpl(Injector injector, List<Class<? extends CElement>> chain) {
        this.chain = chain;
        this.injector = injector;
    }
    
    @Override
    public Class<? extends CElement> getChildClass() {
        if (currentIndex == chain.size()) {
            return null;
        } else {
            return chain.get(currentIndex);
        }
    }

    @Override
    public CElement initChild() {
        
        Class<? extends CElement> cl = getChildClass();
        
        if (cl == null) {
            throw new WebApplicationException("Error getting a child initializer. Initializer " 
                    + chain.get(currentIndex-1).getName() + " does not have any children");
        }

        CElement child = injector.getInstance(cl);
        
        Method initializerMethod = null;
        
        try {
            initializerMethod = cl.getMethod("initialize", InitializerContext.class);
        } catch (SecurityException e) {
            throw new WebApplicationException(e);
        } catch (NoSuchMethodException e) {
            // Ignored
        }
        
        if (initializerMethod != null) {
            currentIndex++;
            try {
                initializerMethod.invoke(child, this);
            } catch (IllegalArgumentException e) {
                throw new InternalWebApplicationException(e);
            } catch (IllegalAccessException e) {
                throw new InternalWebApplicationException(e);
            } catch (InvocationTargetException e) {
                throw new InternalWebApplicationException(e);
            }
        }
        
        return child;
    }

    @Override
    public void sendRedirect(String url) {
        this.redirectUrl = url;
    }

    @Override
    public void sendError(int code) {
        sendError(code, null);
    }

    @Override
    public void sendError(int code, String msg) {
        this.errorCode = code;
        this.errorMsg = msg;
        
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return locale;
    }
}
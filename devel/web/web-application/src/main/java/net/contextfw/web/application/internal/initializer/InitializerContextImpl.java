package net.contextfw.web.application.internal.initializer;

import java.util.List;
import java.util.Locale;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.ViewComponent;
import net.contextfw.web.application.lifecycle.ViewContext;

import com.google.inject.Injector;

public class InitializerContextImpl implements ViewContext {

    private final List<Class<? extends Component>> chain;
    private final Injector injector;
    
    private int currentIndex = 0;
    
    private Locale locale = null;
    
    private Component leaf;
    
    public InitializerContextImpl(Injector injector, List<Class<? extends Component>> chain) {
        this.chain = chain;
        this.injector = injector;
    }
    
    @Override
    public Class<? extends Component> getChildClass() {
        if (currentIndex == chain.size()) {
            return null;
        } else {
            return chain.get(currentIndex);
        }
    }

    @Override
    public Component initChild() {
        
        Class<? extends Component> cl = getChildClass();
        
        if (cl == null) {
            throw new WebApplicationException("Error getting a child initializer. Initializer " 
                    + chain.get(currentIndex-1).getName() + " does not have any children");
        }
        Component component = injector.getInstance(cl);
        
        leaf = component;
        
        if (ViewComponent.class.isAssignableFrom(cl)) {
            currentIndex++;
            ((ViewComponent) component).initialize(this);
        }
        
        return component;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return locale;
    }

    public Component getLeaf() {
        return leaf;
    }
}
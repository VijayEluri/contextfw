package net.contextfw.web.application.internal.initializer;

import java.util.List;
import java.util.Locale;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.initializer.InitializerContext;
import net.contextfw.web.application.initializer.InitializerElement;

import com.google.inject.Injector;

public class InitializerContextImpl implements InitializerContext {

    private final List<Class<? extends Component>> chain;
    private final Injector injector;
    
    private int currentIndex = 0;
    
    private Locale locale = null;
    
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

        Component child = injector.getInstance(cl);
        
        if (InitializerElement.class.isAssignableFrom(cl)) {
            currentIndex++;
            ((InitializerElement) child).initialize(this);
        }
        
        return child;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public Locale getLocale() {
        return locale;
    }
}
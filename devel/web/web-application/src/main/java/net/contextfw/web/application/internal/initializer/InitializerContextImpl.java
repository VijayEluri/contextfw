package net.contextfw.web.application.internal.initializer;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.component.ComponentBuilder;
import net.contextfw.web.application.internal.component.MetaComponent;
import net.contextfw.web.application.internal.servlet.UriMapping;
import net.contextfw.web.application.lifecycle.ViewComponent;
import net.contextfw.web.application.lifecycle.ViewContext;

import com.google.inject.Injector;

public class InitializerContextImpl implements ViewContext {

    private final List<Class<? extends Component>> chain;
    private final Injector injector;
    
    private int currentIndex = 0;
    
    private Locale locale = null;
    
    private Component leaf;
    
    private final ComponentBuilder componentBuilder;
    private final UriMapping mapping;
    private final String uri;
    private final HttpServletRequest request;
    
    public InitializerContextImpl(ComponentBuilder componentBuilder,
                                  UriMapping mapping,
                                  String uri,
                                  Injector injector,
                                  HttpServletRequest request,
                                  List<Class<? extends Component>> chain) {
        this.chain = chain;
        this.injector = injector;
        this.componentBuilder = componentBuilder;
        this.mapping = mapping;
        this.uri = uri;
        this.request = request;
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
        MetaComponent meta = componentBuilder.getMetaComponent(cl);
        meta.applyPathParams(component, mapping, uri);
        meta.applyRequestParams(component, request);
        
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
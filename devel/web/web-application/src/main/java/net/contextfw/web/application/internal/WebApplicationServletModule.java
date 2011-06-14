package net.contextfw.web.application.internal;

import java.util.ArrayList;
import java.util.List;

import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.internal.servlet.CSSServlet;
import net.contextfw.web.application.internal.servlet.InitServlet;
import net.contextfw.web.application.internal.servlet.ScriptServlet;
import net.contextfw.web.application.internal.servlet.UpdateServlet;
import net.contextfw.web.application.internal.util.ClassScanner;
import net.contextfw.web.application.lifecycle.View;
import net.contextfw.web.application.properties.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.ServletModule;

public class WebApplicationServletModule extends ServletModule {

    private Logger logger = LoggerFactory.getLogger(WebApplicationServletModule.class);

    private final PropertyProvider properties;
    
    private final String resourcePrefix;
    
    private final List<String> rootPackages = new ArrayList<String>();
    
    private final Properties configuration;
    
    public WebApplicationServletModule(Properties configuration, PropertyProvider propertyProvider) {
        resourcePrefix = configuration.get(Properties.RESOURCES_PREFIX);
        rootPackages.addAll(configuration.get(Properties.VIEW_COMPONENT_ROOT_PACKAGE));
        this.configuration = configuration;
        this.properties = propertyProvider;
    }
    
    @Override
    protected void configureServlets() {
        
        logger.info("Configuring default servlets");
        serve(resourcePrefix + ".js").with(
                ScriptServlet.class);
        serve(resourcePrefix + ".css").with(
                CSSServlet.class);
        serveRegex(".*/contextfw-update/.*").with(UpdateServlet.class);
        serveRegex(".*/contextfw-refresh/.*").with(UpdateServlet.class);
        serveRegex(".*/contextfw-remove/.*").with(UpdateServlet.class);
        requestInjection(this);
        serveViewComponents();
    }

    private void serveViewComponents() {
        
        logger.info("Configuring view components");

        List<Class<?>> classes = ClassScanner.getClasses(rootPackages);

        InitHandler initHandler = new InitHandler(configuration);
        requestInjection(initHandler);
        InitializerProvider initializerProvider = new InitializerProvider(properties);
        
        for (Class<?> cl : classes) {
            View annotation = cl.getAnnotation(View.class);
            if (annotation != null) {
                if (!Component.class.isAssignableFrom(cl)) {
                    throw new WebApplicationException("Class " + cl.getName() + " annotated with @View does " +
                    		"not extend Component");
                }
                
                List<Class<? extends Component>> chain = initializerProvider.getInitializerChain(cl.asSubclass(Component.class));
                InitServlet initServlet = new InitServlet(initHandler, chain);
                for (String url : annotation.url()) {
                    if (!"".equals(url)) {
                        serveInitServlet(cl, url, initServlet);
                    }
                }
                for (String property : annotation.property()) {
                    if (!"".equals(property)) {
                        if (!properties.get().containsKey(property)) {
                            throw new WebApplicationException("No url bound to property: " + property);
                        }
                        String url = properties.get().getProperty(property);
                        
                        if (url != null && !"".equals(url)) {
                            serveInitServlet(cl, url, initServlet);
                        } else {
                            throw new WebApplicationException("No url bound to view component. (class="
                                        +cl.getSimpleName()+", property="+property+")");
                        }
                    }
                }
            }
        }
    }
    
    private void serveInitServlet(Class<?> cl, String url, InitServlet servlet) {
        if (url.startsWith("regex:")) {
            String serveUrl = url.substring(6); 
            logger.info("  Serving url: "+cl.getName()+ " => {} (regex)", serveUrl);
            serveRegex(serveUrl).with(servlet);
        } else {
            logger.info("  Serving url: "+cl.getName()+ " => {}", url);
            serve(url).with(servlet);
        }
    }
}
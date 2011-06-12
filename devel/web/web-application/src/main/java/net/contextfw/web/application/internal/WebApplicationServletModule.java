package net.contextfw.web.application.internal;


import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
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

    @Inject
    private PropertyProvider properties;
    
    private final String resourcePrefix;
    private final List<String> rootPackages = new ArrayList<String>();
    
    public WebApplicationServletModule(Properties configuration) {
        resourcePrefix = configuration.get(Properties.RESOURCES_PREFIX);
        rootPackages.addAll(configuration.get(Properties.VIEW_COMPONENT_ROOT_PACKAGE));
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
        serveViewComponents();
    }

    private void serveViewComponents() {
        
        logger.info("Configuring view components");
        TreeSet<String> urls = new TreeSet<String>();

        List<Class<?>> classes = ClassScanner.getClasses(rootPackages);

        for (Class<?> cl : classes) {
            View annotation = cl.getAnnotation(View.class);
            if (annotation != null) {
                if (!Component.class.isAssignableFrom(cl)) {
                    throw new WebApplicationException("Class " + cl.getName() + " annotated with @View does " +
                    		"not extend Component");
                }
                for (String url : annotation.url()) {
                    if (!"".equals(url)) {
                        urls.add(url);
                    }
                }
                for (String property : annotation.property()) {
                    if (!"".equals(property)) {
                        String url = properties.get().getProperty(property);
                        
                        if (url != null && !"".equals(url)) {
                            urls.add(url);
                        } else {
                            throw new WebApplicationException("No url bound to view component. (class="
                                        +cl.getSimpleName()+", property="+property+")");
                        }
                    }
                }
            }
        }

        for (String url : urls.descendingSet()) {
            
            if (url.startsWith("regex:")) {
                String serveUrl = url.substring(6); 
                logger.info("  Serving url: {} (regex)", serveUrl);
                serveRegex(serveUrl).with(InitServlet.class);
            } else {
                logger.info("  Serving url: {}", url);
                serve(url).with(InitServlet.class);
            }
        }
    }
}
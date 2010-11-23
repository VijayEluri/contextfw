package net.contextfw.web.application;

import java.util.List;
import java.util.TreeSet;

import net.contextfw.web.application.internal.util.ClassScanner;
import net.contextfw.web.application.servlet.CSSServlet;
import net.contextfw.web.application.servlet.InitServlet;
import net.contextfw.web.application.servlet.ScriptServlet;
import net.contextfw.web.application.servlet.UpdateServlet;
import net.contextfw.web.application.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.ServletModule;

public class WebApplicationServletModule extends ServletModule {

    private Logger logger = LoggerFactory.getLogger(WebApplicationServletModule.class);

    private final ModuleConfiguration configuration;

    public WebApplicationServletModule(ModuleConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configureServlets() {
        
        String contextPath = configuration.getContextPath();
        
        logger.info("Configuring default servlets");
        serve(contextPath + configuration.getResourcesPrefix() + ".js").with(
                ScriptServlet.class);
        serve(contextPath + configuration.getResourcesPrefix() + ".css").with(
                CSSServlet.class);
        serveRegex(".*/contextfw-update/.*").with(UpdateServlet.class);
        serveRegex(".*/contextfw-refresh/.*").with(UpdateServlet.class);
        serveRegex(".*/contextfw-remove/.*").with(UpdateServlet.class);
        serveInitializers();
    }

    private void serveInitializers() {
        
        String contextPath = ""; //+ configuration.getContextPath();
        
        logger.info("Configuring initializer-servlets:");
        TreeSet<String> urls = new TreeSet<String>();

        List<Class<?>> classes = ClassScanner.getClasses(configuration.getInitializerRootPackages());

        for (Class<?> cl : classes) {
            View annotation = cl.getAnnotation(View.class);
            if (annotation != null) {
                for (String url : annotation.url()) {
                    if (!"".equals(url)) {
                        urls.add(contextPath + url);
                    }
                }
                for (String property : annotation.property()) {
                    if (!"".equals(property)) {
                        String url = System.getProperty(property);
                        if (url != null && !"".equals(url)) {
                            urls.add(contextPath + url);
                        }
                    }
                }
            }
        }

        for (String url : urls.descendingSet()) {
            logger.info("  Serving url: {}", url);
            serveRegex(contextPath + url).with(InitServlet.class);
        }
    }
}
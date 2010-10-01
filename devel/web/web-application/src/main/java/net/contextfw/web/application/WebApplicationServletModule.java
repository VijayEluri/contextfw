package net.contextfw.web.application;

import java.io.IOException;
import java.util.TreeSet;

import net.contextfw.web.application.initializer.Initializer;
import net.contextfw.web.application.internal.util.PackageUtils;
import net.contextfw.web.application.servlet.CSSServlet;
import net.contextfw.web.application.servlet.InitServlet;
import net.contextfw.web.application.servlet.ScriptServlet;
import net.contextfw.web.application.servlet.UpdateServlet;

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
        logger.info("Configuring default servlets");
        serve(configuration.getResourcesPrefix() + ".js").with(
                ScriptServlet.class);
        serve(configuration.getResourcesPrefix() + ".css").with(
                CSSServlet.class);
        serveRegex(".*/contextfw-update/.*").with(UpdateServlet.class);
        serveRegex(".*/contextfw-refresh/.*").with(UpdateServlet.class);
        serveRegex(".*/contextfw-remove/.*").with(UpdateServlet.class);
        serveInitializers();
    }

    private void serveInitializers() {
        logger.info("Configuring initializer-servlets:");
        TreeSet<String> urls = new TreeSet<String>();

        for (String pck : configuration.getInitializerRootPackages()) {
            try {
                for (Class<?> cl : PackageUtils.getClasses(pck, Thread
                        .currentThread().getContextClassLoader())) {
                    Initializer initializer = cl
                            .getAnnotation(Initializer.class);
                    if (initializer != null) {
                        if (!"".equals(initializer.urlMatcher())) {
                            urls.add(initializer.urlMatcher());
                        } else if (!"".equals(initializer.url())) {
                            urls.add(initializer.url());
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new WebApplicationException(e);
            } catch (IOException e) {
                throw new WebApplicationException(e);
            }
        }

        for (String url : urls.descendingSet()) {
            logger.info("  Serving url: {}", url);
            serveRegex(url).by(InitServlet.class);
        }
    }
}
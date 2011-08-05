package net.contextfw.web.application.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Pattern;

import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.service.DirectoryWatcher;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.internal.service.ReloadingClassLoaderConf;
import net.contextfw.web.application.internal.servlet.CSSServlet;
import net.contextfw.web.application.internal.servlet.DevelopmentFilter;
import net.contextfw.web.application.internal.servlet.InitServlet;
import net.contextfw.web.application.internal.servlet.ScriptServlet;
import net.contextfw.web.application.internal.servlet.UpdateServlet;
import net.contextfw.web.application.internal.servlet.UriMapping;
import net.contextfw.web.application.internal.servlet.UriPatternType;
import net.contextfw.web.application.internal.util.ClassScanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.servlet.ServletModule;

public class WebApplicationServletModule extends ServletModule {

    private Logger logger = LoggerFactory.getLogger(WebApplicationServletModule.class);
    
    private final Pattern classPattern = Pattern.compile(".+\\.class");

    private final PropertyProvider properties;

    private final String resourcePrefix;

    private final Set<String> rootPackages;

    private final Configuration configuration;

    private final Map<String, InitServlet> servlets = new HashMap<String, InitServlet>();
    
    private InitializerProvider initializerProvider;
    
    private ReloadingClassLoaderConf reloadConf;
    
    private DevelopmentFilter developmentFilter;
    
    private InitHandler initHandler;
    
    public WebApplicationServletModule(Configuration configuration,
            PropertyProvider propertyProvider) {
        
        resourcePrefix = configuration.get(Configuration.RESOURCES_PREFIX);
        this.configuration = configuration;
        this.properties = propertyProvider;

        rootPackages = configuration.get(Configuration.VIEW_COMPONENT_ROOT_PACKAGE);
        boolean reloadEnabled = configuration.get(Configuration.CLASS_RELOADING_ENABLED);
        
        if (reloadEnabled && configuration.get(Configuration.DEVELOPMENT_MODE)) {
            reloadConf = new ReloadingClassLoaderConf(configuration);
        }
        
    }

    @Override
    protected void configureServlets() {

        initHandler = new InitHandler(configuration);
        requestInjection(initHandler);
        initializerProvider = new InitializerProvider();
        
        serve(resourcePrefix + ".js").with(
                ScriptServlet.class);
        serve(resourcePrefix + ".css").with(
                CSSServlet.class);
        serveRegex(".*/contextfw-update/.*").with(UpdateServlet.class);
        serveRegex(".*/contextfw-refresh/.*").with(UpdateServlet.class);
        serveRegex(".*/contextfw-remove/.*").with(UpdateServlet.class);
        requestInjection(this);
        if (configuration.get(Configuration.DEVELOPMENT_MODE)) {
            serveDevelopmentMode();
        } else {
            serveProductionMode();
        }
    }

    private void serveDevelopmentMode() {
        logger.info("Serving view components in DEVELOPMENT mode");
        
        DirectoryWatcher classWatcher = reloadConf == null ?
            new DirectoryWatcher(configuration.get(Configuration.VIEW_COMPONENT_ROOT_PACKAGE),
                  classPattern) :  
            new DirectoryWatcher(reloadConf.getReloadablePackageNames(), 
                classPattern);
        
        developmentFilter = new DevelopmentFilter(
                rootPackages,
                initHandler,
                initializerProvider,
                reloadConf,
                classWatcher,
                properties);
        
        filter("/*").through(developmentFilter);
    }

    private void serveProductionMode() {
        logger.info("Serving view components in PRODUCTION mode");
        
        List<Class<?>> classes = ClassScanner.getClasses(rootPackages);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        SortedSet<UriMapping> mappings = UriMapping.createMappings(
                classes, 
                classLoader,
                initializerProvider,
                initHandler,
                properties);
        
        serveMappings(mappings);
    }

    private void serveMappings(SortedSet<UriMapping> mappings) {
        for (UriMapping mapping : mappings) {
            servlets.put(mapping.getViewClass().getCanonicalName(), mapping.getInitServlet());
            if (mapping.getPatternType() == UriPatternType.REGEX) {
                logger.info("  Serving url: " + mapping.getViewClass().getName() + " => {} (regex)", mapping.getPath());
                serveRegex(mapping.getPath()).with(mapping.getInitServlet());
            } else {
                logger.info("  Serving url: " + mapping.getViewClass().getName() + " => {}", mapping.getPath());
                serve(mapping.getPath()).with(mapping.getInitServlet());
            }   
        }
    }
}
package net.contextfw.web.application.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.internal.component.ComponentBuilderImpl;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.service.DirectoryWatcher;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.internal.service.ReloadingClassLoader;
import net.contextfw.web.application.internal.service.ReloadingClassLoaderConf;
import net.contextfw.web.application.internal.servlet.CSSServlet;
import net.contextfw.web.application.internal.servlet.InitServlet;
import net.contextfw.web.application.internal.servlet.ScriptServlet;
import net.contextfw.web.application.internal.servlet.UpdateServlet;
import net.contextfw.web.application.internal.util.ClassScanner;
import net.contextfw.web.application.lifecycle.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.ServletModule;

public class WebApplicationServletModule extends ServletModule {

    private static final String REGEX = "regex:";

    private Logger logger = LoggerFactory.getLogger(WebApplicationServletModule.class);

    private final PropertyProvider properties;

    private final String resourcePrefix;

    private final Set<String> rootPackages;

    private final Configuration configuration;

    private ClassLoader classLoader = null;

    private final Map<String, InitServlet> servlets = new HashMap<String, InitServlet>();
    
    private InitializerProvider initializerProvider;
    
    private DirectoryWatcher classWatcher;
    
    private ReloadingClassLoaderConf reloadConf;
    
    @Inject
    private ComponentBuilderImpl componentBuilder;

    public WebApplicationServletModule(Configuration configuration,
            PropertyProvider propertyProvider) {
        resourcePrefix = configuration.get(Configuration.RESOURCES_PREFIX);
        this.configuration = configuration;
        this.properties = propertyProvider;

        rootPackages = configuration.get(Configuration.VIEW_COMPONENT_ROOT_PACKAGE);
        boolean reloadEnabled = configuration.get(Configuration.CLASS_RELOADING_ENABLED);
        
        if (reloadEnabled && configuration.get(Configuration.DEVELOPMENT_MODE)) {
            reloadConf = new ReloadingClassLoaderConf(configuration);
            classLoader = new ReloadingClassLoader(reloadConf);
        }
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
        initializerProvider = new InitializerProvider();

        if (reloadConf != null) {
//            List<String> packages = new ArrayList<String>();
//            for (String pck : ) {
//                for (String prefix : reloadConf.getBuildPaths()) {
//                    String delim = prefix.endsWith("/") ? "" : "/";
//                    packages.add("file:" + prefix + delim + pck.replaceAll("\\.", "/"));
//                }
//            }
            classWatcher = new DirectoryWatcher(reloadConf.getReloadablePackageNames(), 
                    Pattern.compile(".+\\.class"));
        }
        
        for (Class<?> cl : classes) {
            View annotation = cl.getAnnotation(View.class);
            if (annotation != null) {
                if (!Component.class.isAssignableFrom(cl)) {
                    throw new WebApplicationException("Class " + cl.getName()
                            + " annotated with @View does " +
                            "not extend Component");
                }

                List<Class<? extends Component>> chain;
                if (reloadConf == null) {
                    chain = initializerProvider.getInitializerChain(cl.asSubclass(Component.class));
                } else {
                    try {
                        chain = initializerProvider.getInitializerChain(
                                classLoader.loadClass(cl.getCanonicalName()).asSubclass(Component.class));
                    } catch (ClassNotFoundException e) {
                        throw new WebApplicationException(e);
                    }
                }

                InitServlet initServlet = new InitServlet(
                        this, classWatcher, initHandler, chain);
                if (reloadConf != null) {
                    servlets.put(cl.getCanonicalName(), initServlet);
                }
                for (String url : annotation.url()) {
                    if (!"".equals(url)) {
                        serveInitServlet(cl, url, initServlet);
                    }
                }
                for (String property : annotation.property()) {
                    if (!"".equals(property)) {
                        if (!properties.get().containsKey(property)) {
                            throw new WebApplicationException("No url bound to property: "
                                    + property);
                        }
                        String url = properties.get().getProperty(property);

                        if (url != null && !"".equals(url)) {
                            serveInitServlet(cl, url, initServlet);
                        } else {
                            throw new WebApplicationException(
                                    "No url bound to view component. (class="
                                            + cl.getSimpleName() + ", property=" + property + ")");
                        }
                    }
                }
            }
        }
    }

    private void serveInitServlet(Class<?> cl, String url, InitServlet servlet) {
        if (url.startsWith(REGEX)) {
            String serveUrl = url.substring(REGEX.length());
            logger.info("  Serving url: " + cl.getName() + " => {} (regex)", serveUrl);
            serveRegex(serveUrl).with(servlet);
        } else {
            logger.info("  Serving url: " + cl.getName() + " => {}", url);
            serve(url).with(servlet);
        }
    }
    
    public void reloadClasses() {
        logger.info("Reloading classes");
        componentBuilder.clean();
        classLoader = new ReloadingClassLoader(reloadConf);
        for (Map.Entry<String, InitServlet> entry : servlets.entrySet()) {
            try {
                entry.getValue().setChain(
                        initializerProvider.getInitializerChain(
                                    classLoader.loadClass(entry.getKey()).asSubclass(Component.class)));
            } catch (ClassNotFoundException e) {
                throw new WebApplicationException(e);
            }
        }
    }
}
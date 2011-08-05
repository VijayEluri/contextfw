package net.contextfw.web.application.internal.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.service.DirectoryWatcher;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.internal.service.ReloadingClassLoader;
import net.contextfw.web.application.internal.service.ReloadingClassLoaderConf;
import net.contextfw.web.application.internal.service.ReloadingClassLoaderContext;
import net.contextfw.web.application.internal.util.ClassScanner;
import net.contextfw.web.application.lifecycle.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class DevelopmentFilter implements Filter, ReloadingClassLoaderContext {

    private static final String REGEX = "regex:";

    private Logger logger = LoggerFactory.getLogger(DevelopmentFilter.class);

    private static class Mapping {
        public final UriPatternMatcher matcher;
        public final HttpServlet servlet;

        public Mapping(UriPatternMatcher matcher, HttpServlet servlet) {
            this.matcher = matcher;
            this.servlet = servlet;
        }
    }

    private List<Mapping> mappings = new ArrayList<Mapping>();
    private final Set<String> rootPackages;
    private final InitHandler initHandler;
    private final InitializerProvider initializerProvider;
    private final ReloadingClassLoaderConf reloadConf;
    private final DirectoryWatcher classWatcher;
    private final PropertyProvider properties;

    public DevelopmentFilter(Set<String> rootPackages,
                             InitHandler initHandler,
                             InitializerProvider initializerProvider,
                             ReloadingClassLoaderConf reloadConf,
                             DirectoryWatcher classWatcher,
                             PropertyProvider properties) {
        this.rootPackages = rootPackages;
        this.initHandler = initHandler;
        this.initializerProvider = initializerProvider;
        this.reloadConf = reloadConf;
        this.classWatcher = classWatcher;
        this.properties = properties;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (mappings.isEmpty() || classWatcher.hasChanged()) {
            reloadClasses();
        }

        boolean served = false;

        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            String uri = req.getRequestURI();

            for (Mapping mapping : mappings) {
                if (mapping.matcher.matches(uri)) {
                    mapping.servlet.service(request, response);
                    served = true;
                    break;
                }
            }
        }

        if (!served) {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reloadClasses() {

        ClassLoader classLoader = new ReloadingClassLoader(reloadConf);

        TreeMap<String, Mapping> regularMappings = new TreeMap<String, Mapping>();
        List<Mapping> regexMappings = new ArrayList<Mapping>();

        List<Class<?>> classes = ClassScanner.getClasses(rootPackages);
        try {
            for (Class<?> rcl : classes) {
                Class<?> cl = classLoader.loadClass(rcl.getCanonicalName());
                View annotation = cl.getAnnotation(View.class);
                if (annotation != null) {
                    if (!Component.class.isAssignableFrom(cl)) {
                        throw new WebApplicationException("Class " + cl.getName()
                                + " annotated with @View does " +
                                "not extend Component");
                    }

                    List<Class<? extends Component>> chain;

                    chain = initializerProvider.getInitializerChain(
                                 classLoader.loadClass(cl.getCanonicalName()).asSubclass(
                                         Component.class));

                    InitServlet initServlet = new InitServlet(
                            this, classWatcher, initHandler, chain);

                    for (String url : annotation.url()) {
                        if (!"".equals(url)) {
                            serveInitServlet(cl, url, initServlet, regularMappings, regexMappings);
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
                                serveInitServlet(cl, url, initServlet, regularMappings,
                                        regexMappings);
                            } else {
                                throw new WebApplicationException(
                                        "No url bound to view component. (class="
                                                + cl.getSimpleName() + ", property=" + property
                                                + ")");
                            }
                        }
                    }
                }
            }

            List<Mapping> newMappings = new ArrayList<Mapping>();
            newMappings.addAll(regularMappings.values());
            Collections.reverse(newMappings);
            newMappings.addAll(regexMappings);

            mappings = newMappings;
        } catch (ClassNotFoundException e) {
            throw new WebApplicationException(e);
        }
    }

    private void serveInitServlet(Class<?> cl,
                                  String url,
                                  InitServlet servlet,
                                  TreeMap<String, Mapping> regularMappings,
                                  List<Mapping> regexMappings) {
        if (url.startsWith(REGEX)) {
            String serveUrl = url.substring(REGEX.length());
            regexMappings.add(new Mapping(
                    UriPatternType.get(UriPatternType.REGEX, serveUrl),
                    servlet));
            logger.info("  Serving url: " + cl.getName() + " => {} (regex)", serveUrl);
        } else {
            logger.info("  Serving url: " + cl.getName() + " => {}", url);
            regularMappings.put(url, new Mapping(
                    UriPatternType.get(UriPatternType.SERVLET, url),
                    servlet));
        }
    }
}

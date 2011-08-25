/**
 * Copyright 2010 Marko Lavikainen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.contextfw.web.application.internal.servlet;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.PropertyProvider;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.service.DirectoryWatcher;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.internal.service.ReloadingClassLoader;
import net.contextfw.web.application.internal.service.ReloadingClassLoaderConf;
import net.contextfw.web.application.internal.service.ReloadingClassLoaderContext;
import net.contextfw.web.application.internal.util.ClassScanner;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class DevelopmentFilter implements Filter, ReloadingClassLoaderContext {

    private Logger logger = LoggerFactory.getLogger(DevelopmentFilter.class);

    private SortedSet<UriMapping> mappings = new TreeSet<UriMapping>();
    
    private final UriMappingFactory fact = new UriMappingFactory();
    
    private final Set<String> rootPackages;
    private final InitHandler initHandler;
    private final InitializerProvider initializerProvider;
    private final ReloadingClassLoaderConf reloadConf;
    private final DirectoryWatcher classWatcher;
    private final PropertyProvider properties;
    private final RequestInvocationFilter filter;

    public DevelopmentFilter(Set<String> rootPackages,
                             InitHandler initHandler,
                             InitializerProvider initializerProvider,
                             ReloadingClassLoaderConf reloadConf,
                             DirectoryWatcher classWatcher,
                             PropertyProvider properties,
                             RequestInvocationFilter filter) {
        this.rootPackages = rootPackages;
        this.initHandler = initHandler;
        this.initializerProvider = initializerProvider;
        this.reloadConf = reloadConf;
        this.classWatcher = classWatcher;
        this.properties = properties;
        this.filter = filter;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (mappings.isEmpty() || classWatcher.hasChanged()) {
            reloadClasses();
        }

        boolean served = false;

        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            String uri = req.getRequestURI().substring(req.getContextPath().length());

            for (UriMapping mapping : mappings) {
                if (mapping.matches(uri)) {
                    mapping.getInitServlet().service(request, response);
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
    public void destroy() {}

    @Override
    public synchronized void reloadClasses() {
        logger.debug("Reloading view components");
        
        ClassLoader classLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return reloadConf == null ? 
                        Thread.currentThread().getContextClassLoader() : 
                        new ReloadingClassLoader(reloadConf);
            }
        });

        List<Class<?>> classes = ClassScanner.getClasses(rootPackages);
        
        mappings = fact.createMappings(
                classes, 
                classLoader,
                initializerProvider,
                initHandler,
                properties,
                filter);
    }
}

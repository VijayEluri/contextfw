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

package net.contextfw.web.application;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.ComponentRegister;
import net.contextfw.web.application.configuration.BindableProperty;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.development.DevelopmentTools;
import net.contextfw.web.application.internal.WebApplicationServletModule;
import net.contextfw.web.application.internal.component.AutoRegisterListener;
import net.contextfw.web.application.internal.component.InternalComponentRegister;
import net.contextfw.web.application.internal.configuration.KeyValue;
import net.contextfw.web.application.internal.development.DevelopmentToolsImpl;
import net.contextfw.web.application.internal.development.InternalDevelopmentTools;
import net.contextfw.web.application.internal.page.PageScope;
import net.contextfw.web.application.internal.service.DirectoryWatcher;
import net.contextfw.web.application.internal.service.PageScopedExecutorImpl;
import net.contextfw.web.application.internal.service.WebApplicationConf;
import net.contextfw.web.application.internal.util.AttributeHandler;
import net.contextfw.web.application.internal.util.ObjectAttributeSerializer;
import net.contextfw.web.application.lifecycle.LifecycleListener;
import net.contextfw.web.application.lifecycle.PageScoped;
import net.contextfw.web.application.lifecycle.PageScopedExecutor;
import net.contextfw.web.application.lifecycle.RequestInvocationFilter;
import net.contextfw.web.application.scope.WebApplicationStorage;
import net.contextfw.web.application.serialize.AttributeJsonSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public final class WebApplicationModule extends AbstractModule {

    private final Configuration configuration;
    
    @SuppressWarnings("rawtypes")
    private AutoRegisterListener autoRegisterListener 
            = new AutoRegisterListener();

    private DevelopmentToolsImpl developmentTools;

    public WebApplicationModule(Configuration configuration) {
        this.configuration = configuration;
    }
    
    private PageScope pageScope;

    @SuppressWarnings("unchecked")
    private <T> void bind(Class<T> type, BindableProperty<T> property) {
        Object obj = configuration.get(property);
        if (obj instanceof Class<?>) {
            bind(type).to((Class<T>) obj);
        } else {
            bind(type).toInstance((T) obj);
            requestInjection(obj);
        }
    }
    
    @Override
    protected void configure() {
        bind(WebApplicationStorage.class, Configuration.WEB_APPLICATION_STORAGE);
        
        pageScope = new PageScope();
        bindScope(PageScoped.class, pageScope);
        bind(PageScope.class).toInstance(pageScope);
        bind(Configuration.class).toInstance(configuration);
        bind(PropertyProvider.class).toInstance(configuration.get(Configuration.PROPERTY_PROVIDER));
        
        handleDevelopmentTools();
        
        //bind(LifecycleListener.class, Configuration.LIFECYCLE_LISTENER);
        bind(PageContext.class).toProvider(pageScope.scope(Key.get(PageContext.class), null));
        bind(PageHandle.class).toProvider(pageScope.scope(Key.get(PageHandle.class), null));
        bind(ObjectAttributeSerializer.class).to(AttributeHandler.class);
        bind(ComponentRegister.class).to(InternalComponentRegister.class);

        bind(RequestInvocationFilter.class).toInstance(configuration.get(Configuration.REQUEST_INVOCATION_FILTER));

        this.bindListener(Matchers.any(), new TypeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public <I> void hear(TypeLiteral<I> typeLiteral,
                    TypeEncounter<I> typeEncounter) {
                if (Component.class.isAssignableFrom(typeLiteral
                        .getRawType())) {
                    typeEncounter.register(autoRegisterListener);
                }
            }
        });

        requestInjection(this);
        requestInjection(autoRegisterListener);
        //requestInjection(pageScope);
        
        WebApplicationServletModule servletModule =
                new WebApplicationServletModule(configuration,
                        configuration.get(Configuration.PROPERTY_PROVIDER),
                        pageScope,
                        developmentTools);

        install(servletModule);
        
    }

    @Singleton
    @Provides
    public Gson provideGson(Injector injector) {

        GsonBuilder builder = new GsonBuilder();

        for (KeyValue<Class<?>, Class<? extends JsonSerializer<?>>> entry : configuration
                .get(Configuration.JSON_SERIALIZER)) {
            builder.registerTypeAdapter(entry.getKey(), injector.getInstance(entry.getValue()));
        }

        for (KeyValue<Class<?>, Class<? extends JsonDeserializer<?>>> entry : configuration
                .get(Configuration.JSON_DESERIALIZER)) {
            builder.registerTypeAdapter(entry.getKey(), injector.getInstance(entry.getValue()));
        }

        for (KeyValue<Class<?>, Class<? extends AttributeJsonSerializer<?>>> entry : configuration
                .get(Configuration.ATTRIBUTE_JSON_SERIALIZER)) {
            builder.registerTypeAdapter(entry.getKey(), injector.getInstance(entry.getValue()));
        }

        return builder.create();
    }

    @Provides
    @Singleton
    public DirectoryWatcher resourceDirectoryWatcher() {
        List<String> paths = null;
        if (configuration.get(Configuration.DEVELOPMENT_MODE)) {
            paths = new ArrayList<String>();
            paths.addAll(configuration.get(Configuration.RESOURCE_PATH));
        }
        
        Pattern matcher = Pattern.compile(".+\\.(xsl|css|js|properties)", Pattern.CASE_INSENSITIVE);
        
        if (!configuration.get(Configuration.CLASS_RELOADING_ENABLED)) {
            matcher = Pattern.compile(".+\\.(xsl|css|js|class|properties)", Pattern.CASE_INSENSITIVE);
        }
        return new DirectoryWatcher(paths, matcher); 
    }
    
    @Provides
    @Singleton
    public WebApplicationConf provideWebApplicationConf() {
        return new WebApplicationConf(
                configuration.get(Configuration.DEVELOPMENT_MODE),
                configuration.get(Configuration.XML_PARAM_NAME),
                configuration.get(Configuration.NAMESPACE));
    }
    
    
    private void handleDevelopmentTools() {
        developmentTools = new DevelopmentToolsImpl(configuration);
    }
    
    @Provides
    @Singleton
    public DevelopmentTools provideDevelopmentTools() {
        return developmentTools;
    }
    
    @Provides
    @Singleton
    public InternalDevelopmentTools provideInternalDevelopmentTools() {
        return developmentTools;
    }
    
    @Provides
    @Singleton
    public PageScopedExecutor providePageScopedExecutor(PageScope pageScope,
                                                        WebApplicationStorage storage) {
        return new PageScopedExecutorImpl(storage, pageScope);
    }

    @SuppressWarnings("unchecked")
    @Provides
    @Singleton
    public LifecycleListener provideLifecycleListener(Injector injector) {
        Object obj = configuration.get(Configuration.LIFECYCLE_LISTENER);
        LifecycleListener listener = null;
        if (obj instanceof LifecycleListener) {
            listener = (LifecycleListener) obj;
        } else {
            listener = injector.getInstance(((Class<LifecycleListener>) obj));
        }
        pageScope.setListener(listener);
        return listener;
    }
}
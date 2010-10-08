package net.contextfw.web.application;

import java.io.IOException;
import java.util.List;

import net.contextfw.web.application.annotations.WebApplicationScoped;
import net.contextfw.web.application.dom.AttributeHandler;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.elements.enhanced.EnhancedElement;
import net.contextfw.web.application.initializer.Initializer;
import net.contextfw.web.application.internal.enhanced.AutoRegisterListener;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.providers.HttpContextProvider;
import net.contextfw.web.application.internal.providers.RequestProvider;
import net.contextfw.web.application.internal.providers.WebApplicationHandleProvider;
import net.contextfw.web.application.internal.scope.WebApplicationScope;
import net.contextfw.web.application.internal.util.PackageUtils;
import net.contextfw.web.application.request.Request;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class WebApplicationModule extends AbstractModule {

    private final ModuleConfiguration configuration;
    
    @SuppressWarnings("rawtypes")
    private AutoRegisterListener autoRegisterListener = new AutoRegisterListener();
    
    public WebApplicationModule(ModuleConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        WebApplicationScope webApplicationScope = new WebApplicationScope();
        bindScope(WebApplicationScoped.class, webApplicationScope);

        bind(WebApplicationScope.class).annotatedWith(Names.named("webApplicationScope")).toInstance(
                webApplicationScope);

        bind(HttpContext.class).toProvider(HttpContextProvider.class);
        bind(WebApplicationHandle.class).toProvider(WebApplicationHandleProvider.class);
        bind(Request.class).toProvider(RequestProvider.class);
        bind(AttributeHandler.class).to(configuration.getAttributeHandlerClass());
        bind(InitializerProvider.class).toInstance(configureInitializers());
        bind(ModuleConfiguration.class).toInstance(configuration);
        this.bindListener(Matchers.any(), new TypeListener() {
            @Override
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                if (EnhancedElement.class.isAssignableFrom(typeLiteral.getRawType())) {
                    typeEncounter.register(autoRegisterListener);
                }
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    private InitializerProvider configureInitializers() {
        InitializerProvider provider = new InitializerProvider();
        
        for (String pck : configuration.getInitializerRootPackages()) {
            
            List<Class<?>> classes;
            try {
                classes = PackageUtils.getClasses(pck,
                        Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new WebApplicationException(e);
            } catch (IOException e) {
                throw new WebApplicationException(e);
            }
            
            for (Class<?> cl : classes) {
                if (CElement.class.isAssignableFrom(cl) && cl.getAnnotation(Initializer.class) != null) {
                    provider.addInitializer((Class<? extends CElement>) cl);
                }
            }
        }
        
        return provider;
    }
}
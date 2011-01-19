package net.contextfw.web.application;

import java.util.List;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import net.contextfw.web.application.annotations.PageScoped;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.conf.PropertyProvider;
import net.contextfw.web.application.conf.WebConfiguration;
import net.contextfw.web.application.converter.ObjectAttributeSerializer;
import net.contextfw.web.application.internal.component.AutoRegisterListener;
import net.contextfw.web.application.internal.initializer.InitializerProvider;
import net.contextfw.web.application.internal.providers.HttpContextProvider;
import net.contextfw.web.application.internal.providers.RequestProvider;
import net.contextfw.web.application.internal.providers.WebApplicationHandleProvider;
import net.contextfw.web.application.internal.scope.WebApplicationScope;
import net.contextfw.web.application.internal.service.WebApplicationContextHandler;
import net.contextfw.web.application.internal.util.AttributeHandler;
import net.contextfw.web.application.internal.util.ClassScanner;
import net.contextfw.web.application.request.Request;
import net.contextfw.web.application.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public final class WebApplicationModule extends AbstractModule {

    private final WebConfiguration configuration;
    
    private Logger logger = LoggerFactory.getLogger(WebApplicationModule.class);

    @SuppressWarnings("rawtypes")
    private AutoRegisterListener autoRegisterListener = new AutoRegisterListener();

    public WebApplicationModule(WebConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void configure() {
        WebApplicationScope webApplicationScope = new WebApplicationScope();
        bindScope(PageScoped.class, webApplicationScope);

        bind(WebApplicationScope.class).annotatedWith(
                Names.named("webApplicationScope")).toInstance(
                webApplicationScope);

        bind(HttpContext.class).toProvider(HttpContextProvider.class);
        bind(ObjectAttributeSerializer.class).to(AttributeHandler.class);
        bind(WebApplicationHandle.class).toProvider(
                WebApplicationHandleProvider.class);
        bind(Request.class).toProvider(RequestProvider.class);
        bind(InitializerProvider.class).toInstance(configureInitializers());
        bind(WebConfiguration.class).toInstance(configuration);
        bind(PropertyProvider.class).to(configuration.getPropertyProvider());
        
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
        
        WebApplicationServletModule servletModule = new WebApplicationServletModule(configuration);
        requestInjection(servletModule);
        install(servletModule);
    }

    @Singleton
    @Provides
    public Gson provideGson(Injector injector) {
        GsonBuilder builder = new GsonBuilder();
        
        for (Entry<Class<?>, Class<? extends JsonSerializer<?>>> entry: configuration.getJsonSerializerClasses()) {
            builder.registerTypeAdapter(entry.getKey(), injector.getInstance(entry.getValue()));
        }

        for (Entry<Class<?>, Class<? extends JsonDeserializer<?>>> entry: configuration.getJsonDeserializerClasses()) {
            builder.registerTypeAdapter(entry.getKey(), injector.getInstance(entry.getValue()));
        }        
        
        return builder.create();
    }
    
    @Singleton
    @Provides
    public WebApplicationContextHandler provideWebApplicationContextHandler(WebConfiguration configuration) {
        final WebApplicationContextHandler handler = new WebApplicationContextHandler(configuration);
        Timer timer = new Timer(true);
        logger.info("Starting scheduled removal for expired web applications");
        
        timer.schedule(new TimerTask() {
            public void run() {
                handler.removeExpiredApplications();
            }
        }, configuration.getRemovalSchedulePeriod(), 
        configuration.getRemovalSchedulePeriod()); 
        
        return handler;
    }
    
    @SuppressWarnings("unchecked")
    private InitializerProvider configureInitializers() {
        InitializerProvider provider = new InitializerProvider(configuration);

        List<Class<?>> classes = ClassScanner.getClasses(configuration.getViewComponentRootPackages());

        for (Class<?> cl : classes) {
            if (Component.class.isAssignableFrom(cl)
                    && cl.getAnnotation(View.class) != null) {
                provider.addInitializer((Class<? extends Component>) cl);
            }
        }

        return provider;
    }
}
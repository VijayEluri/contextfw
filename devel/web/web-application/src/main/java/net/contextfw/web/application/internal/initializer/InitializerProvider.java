package net.contextfw.web.application.internal.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.annotations.PageScoped;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.conf.PropertyProvider;
import net.contextfw.web.application.properties.Properties;
import net.contextfw.web.application.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class InitializerProvider {

    private Logger logger = LoggerFactory.getLogger(InitializerProvider.class);
    
    @Inject
    private PropertyProvider properties;
    
    private final Map<Pattern, Class<? extends Component>> initializers 
        = new HashMap<Pattern, Class<? extends Component>>();
    
    private final Map<Class<? extends Component>, List<Class<? extends Component>>> chain
         = new HashMap<Class<? extends Component>, List<Class<? extends Component>>>(); 

    private final String contextPath;
    
    public InitializerProvider(Properties configuration) {
        contextPath = configuration.get(Properties.CONTEXT_PATH);
    }

    public void addInitializer(Class<? extends Component> cl) {

        if (cl == null) {
            throw new WebApplicationException("Initializer was null");
        }

        View annotation = processClass(cl);

        if ("".equals(annotation.url())) {
            return;
        }
        try {
            for (String url : annotation.url()) {
                if (!"".equals(url)) {
                    initializers.put(Pattern.compile(contextPath + url, Pattern.CASE_INSENSITIVE), cl);
                }
            }
            for (String property : annotation.property()) {
                if (!"".equals(property)) {
                    String url = properties.get().getProperty(property);
                    if (url != null && !"".equals(url)) {
                        initializers.put(Pattern.compile(contextPath + url, Pattern.CASE_INSENSITIVE), cl);
                    }
                }
            }
            List<Class<? extends Component>> classes = new ArrayList<Class<? extends Component>>();
            
            Class<? extends Component> currentClass = annotation.parent();
            
            logger.info("Registered initializer: {}", cl.getName());
            classes.add(cl);
            while (!currentClass.equals(Component.class)) {
                View anno = processClass(currentClass);
                classes.add(0, currentClass);
                currentClass = anno.parent();
            }
            chain.put(cl, classes);
        } catch (PatternSyntaxException pse) {
            throw new WebApplicationException("Could not compile url:" + annotation.url(), pse);
        }
    }

    public List<Class<? extends Component>> findChain(String url) {
        for (Entry<Pattern, Class<? extends Component>> entry : initializers.entrySet()) {
            if (entry.getKey().matcher(url).matches()) {
                return chain.get(entry.getValue());
            }
        }
        return null;
    }
    
    private View processClass(Class<?> cl) {
        
        if (cl.getAnnotation(PageScoped.class) == null) {
            throw new WebApplicationException("Initializer '"+cl.getName()+"' is missing @PageScoped-annotation");
        }
        
        View annotation = cl.getAnnotation(View.class);
        
        if (annotation == null) {
            throw new WebApplicationException("Initializer '"+cl.getName()+"' is missing @View-annotation");
        }
        return annotation;
    }
}
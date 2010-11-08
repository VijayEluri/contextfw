package net.contextfw.web.application.internal.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.annotations.WebApplicationScoped;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.initializer.Initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

@Singleton
public class InitializerProvider {

    private Logger logger = LoggerFactory.getLogger(InitializerProvider.class);
    
    private final Map<Pattern, Class<? extends Component>> initializers 
        = new HashMap<Pattern, Class<? extends Component>>();
    
    private final Map<Class<? extends Component>, List<Class<? extends Component>>> chain
         = new HashMap<Class<? extends Component>, List<Class<? extends Component>>>(); 

    public InitializerProvider() {}

    public void addInitializer(Class<? extends Component> cl) {

        if (cl == null) {
            throw new WebApplicationException("Initializer was null");
        }

        Initializer annotation = processClass(cl);

        if ("".equals(annotation.url())) {
            return;
        }
        try {
            String url = "".equals(annotation.urlMatcher()) ? annotation.url() : annotation.urlMatcher(); 
            
            initializers.put(Pattern.compile(url, Pattern.CASE_INSENSITIVE), cl);
            
            List<Class<? extends Component>> classes = new ArrayList<Class<? extends Component>>();
            
            Class<? extends Component> currentClass = annotation.parent();
            
            logger.info("Registered initializer: {}", cl.getName());
            classes.add(cl);
            while (!currentClass.equals(Component.class)) {
                Initializer anno = processClass(currentClass);
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
    
    private Initializer processClass(Class<?> cl) {
        
        if (cl.getAnnotation(WebApplicationScoped.class) == null) {
            throw new WebApplicationException("Initializer '"+cl.getName()+"' is missing @WebApplicationScoped-annotation");
        }
        
        Initializer annotation = cl.getAnnotation(Initializer.class);
        
        if (annotation == null) {
            throw new WebApplicationException("Initializer '"+cl.getName()+"' is missing @Initializer-annotation");
        }
        return annotation;
    }
}
package net.contextfw.web.application.internal.initializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.contextfw.web.application.WebApplicationServletModule;
import net.contextfw.web.application.annotations.WebApplicationScoped;
import net.contextfw.web.application.elements.CElement;
import net.contextfw.web.application.initializer.Initializer;
import net.contextfw.web.service.application.WebApplicationException;

import com.google.inject.Singleton;

@Singleton
public class InitializerProvider {

    private Logger logger = LoggerFactory.getLogger(InitializerProvider.class);
    
    private final Map<Pattern, Class<? extends CElement>> initializers 
        = new HashMap<Pattern, Class<? extends CElement>>();
    
    private final Map<Class<? extends CElement>, List<Class<? extends CElement>>> chain
         = new HashMap<Class<? extends CElement>, List<Class<? extends CElement>>>(); 

    public InitializerProvider() {}

    public void addInitializer(Class<? extends CElement> cl) {

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
            
            List<Class<? extends CElement>> classes = new ArrayList<Class<? extends CElement>>();
            
            Class<? extends CElement> currentClass = annotation.parent();
            
            logger.info("Registered initializer: {}", cl.getName());
            classes.add(cl);
            while (!currentClass.equals(CElement.class)) {
                Initializer anno = processClass(currentClass);
                classes.add(0, currentClass);
                currentClass = anno.parent();
            }
            chain.put(cl, classes);
        } catch (PatternSyntaxException pse) {
            throw new WebApplicationException("Could not compile url:" + annotation.url(), pse);
        }
    }

    public List<Class<? extends CElement>> findChain(String url) {
        for (Entry<Pattern, Class<? extends CElement>> entry : initializers.entrySet()) {
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
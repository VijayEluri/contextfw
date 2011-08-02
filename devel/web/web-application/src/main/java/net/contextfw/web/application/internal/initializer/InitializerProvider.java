package net.contextfw.web.application.internal.initializer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.PageScoped;
import net.contextfw.web.application.lifecycle.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializerProvider {

    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(InitializerProvider.class);

    public InitializerProvider() {
    }

    public List<Class<? extends Component>> getInitializerChain(Class<? extends Component> cl) {

        if (cl == null) {
            throw new WebApplicationException("View was null");
        }
        if (!Component.class.isAssignableFrom(cl)) {
            throw new WebApplicationException("View " + cl.getName()
                    + " does not extend Component");
        }

        View annotation = processClass(cl);

        if ("".equals(annotation.url())) {
            return null;
        }
        try {
//            for (String url : annotation.url()) {
//                if (!"".equals(url)) {
//                    initializers.put(Pattern.compile(contextPath + toUrl(url),
//                            Pattern.CASE_INSENSITIVE), cl);
//                }
//            }
//            for (String property : annotation.property()) {
//                if (!"".equals(property)) {
//                    String url = properties.get().getProperty(property);
//                    if (url != null && !"".equals(url)) {
//                        initializers.put(Pattern.compile(contextPath + toUrl(url),
//                                Pattern.CASE_INSENSITIVE), cl);
//                    }
//                }
//            }
            List<Class<? extends Component>> classes = new ArrayList<Class<? extends Component>>();

            Class<? extends Component> currentClass = annotation.parent();
            classes.add(cl);
            while (!currentClass.equals(Component.class)) {
                View anno = processClass(currentClass);
                classes.add(0, currentClass);
                currentClass = anno.parent();
            }
            return classes;
        } catch (PatternSyntaxException pse) {
            throw new WebApplicationException("Could not compile url:"
                    + annotation.url(), pse);
        }
    }

    private View processClass(Class<?> cl) {

        if (cl.getAnnotation(PageScoped.class) == null) {
            throw new WebApplicationException("View '" + cl.getName()
                    + "' is missing @PageScoped-annotation");
        }

        View annotation = cl.getAnnotation(View.class);

        if (annotation == null) {
            throw new WebApplicationException("View '" + cl.getName()
                    + "' is missing @View-annotation");
        }
        return annotation;
    }
}
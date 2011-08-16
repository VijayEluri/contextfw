package net.contextfw.web.application.internal.initializer;

import java.util.ArrayList;
import java.util.List;

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

    public List<Class<? extends Component>> getInitializerChain(Class<?> rawCl) {

        if (rawCl == null) {
            throw new WebApplicationException("View was null");
        }
        
        if (!Component.class.isAssignableFrom(rawCl)) {
            throw new WebApplicationException(rawCl, "View"
                    + " does not extend Component", null);
        }

        @SuppressWarnings("unchecked")
        Class<? extends Component> cl = (Class<? extends Component>) rawCl;
        
        View annotation = processClass(cl);

        List<Class<? extends Component>> classes = new ArrayList<Class<? extends Component>>();

        Class<? extends Component> currentClass = annotation.parent();
        classes.add(cl);
        while (!currentClass.equals(Component.class)) {
            View anno = processClass(currentClass);
            classes.add(0, currentClass);
            currentClass = anno.parent();
        }
        return classes;
    }

    private View processClass(Class<?> cl) {

        if (cl.getAnnotation(PageScoped.class) == null) {
            throw new WebApplicationException(cl, "View "
                    + " is missing @PageScoped-annotation", null);
        }

        View annotation = cl.getAnnotation(View.class);

        if (annotation == null) {
            throw new WebApplicationException(cl, "View "
                    + " is missing @View-annotation", null);
        }
        return annotation;
    }
}
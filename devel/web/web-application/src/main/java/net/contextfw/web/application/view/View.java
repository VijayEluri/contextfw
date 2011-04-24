package net.contextfw.web.application.view;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.contextfw.web.application.component.Component;

/**
 * Maps a component to URL making it a view component. 
 * 
 * <p>
 *  This annotation must be used in conjunction with @PageScoped annotation
 * </p>
 * <p>
 *  When component is marked as a view, the page initialization process can be
 *  followed an intercepted by implementing an interface {@link ViewComponent}. 
 *  Implementing the interface is optional.
 * </p>
 * 
 * @see net.contextfw.web.application.annotations.PageScoped @PageScoped
 * @see ViewComponent
 * 
 * @author marko
 *
 */
@Target( { TYPE })
@Retention(RUNTIME)
public @interface View {
    /**
     * Maps view to given set of URLs. URLs are given as regular expressions.
     */
    String[] url() default "";
    /**
     * Maps view to given set of URLs that are mapped to property keys.
     * 
     * <p>
     *  The values of the properties are handled as regular expressions.
     * </p>
     */
    String[] property() default "";
    /**
     * Defines the parent view for this view.
     * 
     * <p>
     *  A view may have a parent which is initialized before this view. The parent
     *  may choose not to initialize this view, if it is not seen necessary.
     * </p>
     * 
     * @see ViewComponent
     */
    Class<? extends Component> parent() default Component.class;
}
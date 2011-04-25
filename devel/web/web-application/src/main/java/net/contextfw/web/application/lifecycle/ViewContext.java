package net.contextfw.web.application.lifecycle;

import java.util.Locale;

import net.contextfw.web.application.component.Component;

/**
 * Provides an access to child view.
 *
 */
public interface ViewContext {
    
    /**
     * @return
     *   The class of child component or <code>null</code> if there is no child view.
     */
    Class<? extends Component> getChildClass();
    
    /**
     * Initializes a child component if it exists. Initialization does not register the component
     * to the parent and must be made manually. Throws exception if child does not exist.
     * 
     * @return
     *   The child component.
     */
    Component initChild();
    
    /**
     * Sets the locale to be used in the page
     */
    void setLocale(Locale locale);
}
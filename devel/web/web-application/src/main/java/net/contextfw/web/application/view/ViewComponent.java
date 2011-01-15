package net.contextfw.web.application.view;

/**
 * Denotes a view component to be listening the initialization process.
 * 
 * <p>
 *  When a view component implements this interface, system calls the <code>initialize</code>-method
 *  after member injection has been done. The main purpose of this interface is to ask the system
 *  to initialize a child view, if it can be done. The child view can be obtained by the 
 *  {@link ViewContext}
 * </p>
 * 
 */
public interface ViewComponent {
    void initialize(ViewContext context);
}

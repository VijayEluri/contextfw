package net.contextfw.web.application.remote;

import javax.servlet.http.HttpServletRequest;

import net.contextfw.web.application.component.Component;

/**
 * The base interface for handling delayed updates
 * 
 * <p>
 *  When an update handler is called it must make appropriate preparations to delay invocation.
 *  It should be noted that the framework itself does note provide any delaying features because
 *  they are web container specific. That is for instance, Jetty works differently from Tomcat.
 * </p>
 * <p>
 *  It is developers responsibility to use Continuations or similar features to create proper
 *  delay.
 * </p>
 * 
 * @param <T>
 *   The type of component
 */
public interface DelayedUpdateHandler<T extends Component> {
	
    /**
     * Returns <code>true</code> if update should be delayed
     * @param component
     *   The component
     * @param request
     *   The request that is used to create the delay
     * @return
     *   <code>true</code> if update should be delayed, false otherwise
     */
    boolean isUpdateDelayed(T component, HttpServletRequest request);
}

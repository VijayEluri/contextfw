package net.contextfw.web.application;

import net.contextfw.web.application.internal.WebResponder;
import net.contextfw.web.application.servlet.CSSServlet;
import net.contextfw.web.application.servlet.ScriptServlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Cleans cached resources 
 *
 * <p>
 *  This class can be used to clean cached resources if needed. This is useful if 
 *  part of the resources have been externalized somewhere else than classpath.
 * </p>
 */
@Singleton
public class ResourceCleaner {
    
    @Inject
    private WebResponder webResponder;
    
    @Inject
    private CSSServlet cssServlet;
    
    @Inject
    private ScriptServlet scriptServlet;
    
    /**
     * Cleans resources
     */
    public void clean() {
        webResponder.clean();
        cssServlet.clean();
        scriptServlet.clean();
    }
}

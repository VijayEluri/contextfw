package net.contextfw.web.application;

import net.contextfw.web.application.internal.WebResponder;
import net.contextfw.web.application.servlet.CSSServlet;
import net.contextfw.web.application.servlet.ScriptServlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ResourceCleaner {
    
    @Inject
    private WebResponder webResponder;
    
    @Inject
    private CSSServlet cssServlet;
    
    @Inject
    private ScriptServlet scriptServlet;
    
    public void clean() {
        webResponder.clean();
        cssServlet.clean();
        scriptServlet.clean();
    }
}

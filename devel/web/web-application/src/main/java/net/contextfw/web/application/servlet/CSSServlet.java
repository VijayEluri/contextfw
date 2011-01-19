package net.contextfw.web.application.servlet;

import java.util.List;
import java.util.regex.Pattern;

import net.contextfw.web.application.conf.WebConfiguration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CSSServlet extends ResourceServlet {

    private static final long serialVersionUID = 7512496095453218996L;
    
    private final boolean debugMode;

    private final List<String> resourcePaths;

    @Inject
    public CSSServlet(WebConfiguration configuration) {
        this.debugMode = configuration.isDebugMode();
        this.resourcePaths = configuration.getResourcePaths();
    }
    
    @Override
    public boolean clear() {
        return debugMode;
    }

    @Override
    protected Pattern getAcceptor() {
        return Pattern.compile(".*\\.css", Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected List<String> getRootPaths() {
        return resourcePaths;
    }
}

package net.contextfw.web.application.internal.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.contextfw.web.application.properties.Properties;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CSSServlet extends ResourceServlet {

    private static final long serialVersionUID = 7512496095453218996L;
    
    private final boolean debugMode;

    private final List<String> resourcePaths = new ArrayList<String>();

    @Inject
    public CSSServlet(Properties configuration) {
        this.debugMode = configuration.get(Properties.DEBUG_MODE);
        this.resourcePaths.addAll(configuration.get(Properties.RESOURCE_PATH));
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

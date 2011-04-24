package net.contextfw.web.application.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.contextfw.web.application.properties.Properties;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ScriptServlet extends ResourceServlet {

    private static final long serialVersionUID = 1L;

    private final boolean debugMode;

    private final List<String> resourcePaths = new ArrayList<String>();

    @Inject
    public ScriptServlet(Properties configuration) {
        this.debugMode = configuration.get(Properties.DEBUG_MODE);
        this.resourcePaths.addAll(configuration.get(Properties.RESOURCE_PATH));
    }
    
    @Override
    public boolean clear() {
        return debugMode;
    }

    @Override
    protected Pattern getAcceptor() {
        return Pattern.compile(".*\\.js", Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected List<String> getRootPaths() {
        return resourcePaths;
    }

}
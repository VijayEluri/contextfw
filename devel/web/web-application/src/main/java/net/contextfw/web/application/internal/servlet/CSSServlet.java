package net.contextfw.web.application.internal.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.contextfw.web.application.configuration.Configuration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CSSServlet extends ResourceServlet {

    private static final long serialVersionUID = 7512496095453218996L;
    
    private final List<String> resourcePaths = new ArrayList<String>();
    
    @Inject
    public CSSServlet(Configuration configuration) {
        this.resourcePaths.addAll(configuration.get(Configuration.RESOURCE_PATH));
    }
    
    @Override
    public boolean clear() {
        return false;
    }

    @Override
    protected Pattern getAcceptor() {
        return Pattern.compile(".*\\.css", Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected List<String> getRootPaths() {
        return resourcePaths;
    }

    @Override
    protected String getContentType() {
        return "text/css";
    }
}

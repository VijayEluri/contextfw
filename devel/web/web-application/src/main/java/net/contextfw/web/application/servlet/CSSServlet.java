package net.contextfw.web.application.servlet;

import java.io.File;
import java.util.List;

import net.contextfw.web.application.ModuleConfiguration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CSSServlet extends JarResourceServlet {

    private static final long serialVersionUID = 7512496095453218996L;
    
    private final boolean debugMode;

    private final List<String> resourcePackages;

    @Inject
    public CSSServlet(ModuleConfiguration configuration) {
        this.debugMode = configuration.isDebugMode();
        this.resourcePackages = configuration.getResourceRootPackages();
    }
    
    @Override
    public List<String> getRootPackages() {
        return resourcePackages;
    }

    @Override
    public boolean clear() {
        return debugMode;
    }

    @Override
    public boolean accept(File file) {
        return file.getName().endsWith(".css");
    }
}

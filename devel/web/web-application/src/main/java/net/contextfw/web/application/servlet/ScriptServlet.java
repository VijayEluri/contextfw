package net.contextfw.web.application.servlet;

import java.io.File;
import java.util.List;

import net.contextfw.web.application.ModuleConfiguration;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ScriptServlet extends JarResourceServlet {

    private static final long serialVersionUID = 1L;

    private final boolean debugMode;

    private final List<String> resourcePackages;

    @Inject
    public ScriptServlet(ModuleConfiguration configuration) {
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
        return file.getName().endsWith(".js");
    }
}
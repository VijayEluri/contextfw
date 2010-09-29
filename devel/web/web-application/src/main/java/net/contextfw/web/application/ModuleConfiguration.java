package net.contextfw.web.application;

import java.util.ArrayList;
import java.util.List;

import net.contextfw.web.application.dom.AttributeHandler;

import com.google.inject.Singleton;

@Singleton
public class ModuleConfiguration {

    private Class<? extends AttributeHandler> attributeHandler;

    private final List<String> initializerRootPackages = new ArrayList<String>();
    private final List<String> resourceRootPackages = new ArrayList<String>();
    private boolean debugMode = false;
    private boolean logXML = false;
    private String resourcesPrefix = "/resources";
    private long pollTime = 1000*60*5; // 5 minutes
    private long maxInactivity = 1000*60*11; // 11 minutes
    private long errorTime = 2000;
    
    
    private String xmlParamName = null;
    
    public ModuleConfiguration attributeHandlerClass(Class<? extends AttributeHandler> attributeHandler) {
        this.attributeHandler = attributeHandler; 
        return this;
    }

    public Class<? extends AttributeHandler> getAttributeHandlerClass() {
        return attributeHandler;
    }
    
    public ModuleConfiguration debugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }
    
    public ModuleConfiguration initializerRootPackages(String... packages) {
        if (packages != null) {
            for (String pck : packages) {
                if (pck != null && pck.trim().length() != 0) {
                    initializerRootPackages.add(pck);
                }
            }
        }
        return this;
    }
    
    public ModuleConfiguration resourceRootPackages(String... packages) {
        if (packages != null) {
            for (String pck : packages) {
                if (pck != null && pck.trim().length() != 0) {
                    resourceRootPackages.add(pck);
                }
            }
        }
        return this;
    }

    public List<String> getInitializerRootPackages() {
        return initializerRootPackages;
    }

    public List<String> getResourceRootPackages() {
        return resourceRootPackages;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setXmlParamName(String xmlParamName) {
        this.xmlParamName = xmlParamName;
    }

    public String getXmlParamName() {
        return xmlParamName;
    }

    public void setLogXML(boolean logXML) {
        this.logXML = logXML;
    }

    public boolean isLogXML() {
        return logXML;
    }

    public void setResourcesPrefix(String resourcesPrefix) {
        this.resourcesPrefix = resourcesPrefix;
    }

    public String getResourcesPrefix() {
        return resourcesPrefix;
    }

    public void setPollTime(long pollTime) {
        this.pollTime = pollTime;
    }

    public long getPollTime() {
        return pollTime;
    }

    public void setMaxInactivity(long maxInactivity) {
        this.maxInactivity = maxInactivity;
    }

    public long getMaxInactivity() {
        return maxInactivity;
    }

    public void setErrorTime(long errorTime) {
        this.errorTime = errorTime;
    }

    public long getErrorTime() {
        return errorTime;
    }
}

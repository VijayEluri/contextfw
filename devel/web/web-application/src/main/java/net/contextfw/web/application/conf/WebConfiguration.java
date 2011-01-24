package net.contextfw.web.application.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.contextfw.web.application.DefaultLifecycleListener;
import net.contextfw.web.application.LifecycleListener;
import net.contextfw.web.application.converter.AttributeJsonSerializer;
import net.contextfw.web.application.converter.AttributeSerializer;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.inject.Singleton;

/**
 * Configures the application during initialization
 */
@Singleton
public class WebConfiguration {

    private final List<String> viewComponentRootPackages = new ArrayList<String>();
    private final List<String> resourcePaths = new ArrayList<String>();
    
    private final Map<Class<?>, Class<? extends AttributeSerializer<?>>> attributeSerializerClasses 
    = new HashMap<Class<?>, Class<? extends AttributeSerializer<?>>>();
    
    private final Map<Class<?>, Class<? extends JsonSerializer<?>>> jsonSerializerClasses 
        = new HashMap<Class<?>, Class<? extends JsonSerializer<?>>>();
    
    private final Map<Class<?>, Class<? extends JsonDeserializer<?>>> jsonDeserializerClasses 
        = new HashMap<Class<?>, Class<? extends JsonDeserializer<?>>>();
    
    private final Map<String, String> namespaces = new HashMap<String, String>();
    
    private boolean debugMode = false;
    private boolean logXML = false;
    private String resourcesPrefix = "/resources";
    private long pollTime = 1000*60*5; // 5 minutes
    private long maxInactivity = 1000*60*11; // 11 minutes
    private long removalSchedulePeriod = 1000*60*5;
    private long errorTime = 2000;
    private String contextPath = "";
    private int transformerCount = 1;
    
    private Class<? extends PropertyProvider> propertyProvider = SystemPropertyProvider.class;
    
    private String xmlParamName = null;
    private Class<? extends LifecycleListener> lifecycleListener = DefaultLifecycleListener.class;
    
    public WebConfiguration addXMLNamespace(String prefix, String path) {
        namespaces.put(prefix, path);
        return this;
    }
    
    public Map<String, String> getXMLNamespaces() {
        return Collections.unmodifiableMap(namespaces);
    }
    
    public WebConfiguration debugMode(boolean debugMode) {
        this.debugMode = debugMode;
        return this;
    }
    
    public WebConfiguration setViewComponentRootPackages(String... packages) {
        if (packages != null) {
            for (String pck : packages) {
                if (pck != null && pck.trim().length() != 0) {
                    viewComponentRootPackages.add(pck);
                }
            }
        }
        return this;
    }

    /**
     * Add paths where to look for resources.
     * 
     * <p>Supported possibilities are</p>
     * 
     * <ul>
     *  <li>package name</li>
     *  <li>classpath:directory</li>
     *  <li>file:directory</li>
     * </ul>
     * @param resources
     * @return
     */
    public WebConfiguration addResourcePaths(String... resources) {
        if (resources != null) {
            for (String pck : resources) {
                if (pck != null && pck.trim().length() != 0) {
                    resourcePaths.add(pck);
                }
            }
        }
        return this;
    }

    public List<String> getViewComponentRootPackages() {
        return viewComponentRootPackages;
    }

    public List<String> getResourcePaths() {
        return resourcePaths;
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
    
    /**
     * Set's the prefix for public resources files.
     * 
     * <p>That is javascript and css-files</p>
     * @param resourcesPrefix
     */
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

    public String getContextPath() {
        return contextPath;
    }

    public WebConfiguration setContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    /**
     * Sets the number of concurrent transformers
     * 
     * <p>Transformers are used to transform DOM-tree via XSL to XHTML.</p>
     * 
     * <p>
     *  This methods sets the number of that can be used concurrently. 
     *  This requirement comes from the fact that transformers are not thread-safe.
     * </p>
     * <p>
     *   By default the number of transformers is 1 and is usable in development.
     *   Hoverer for production the number should be increased, but there is a down side
     *   for large number of transformer, which is memory consumption. Templates
     *   must be loaded for every transformer and they cannot be shared.
     * </p>
     *  
     * @param transformerCount
     *  The number of transformers. Minimum is 1.
     * @return
     *  The configuration
     */
    public WebConfiguration setTransformerCount(int transformerCount) {
        if (transformerCount < 1) {
            throw new IllegalArgumentException("At least 1 transformed must be specified");
        }
        this.transformerCount = transformerCount;
        return this;
    }

    public int getTransformerCount() {
        return transformerCount;
    }

    /**
     * Convinience method to add all serializer types at same time
     * @param <S>
     *      Type type of source
     * @param type
     *      The class of type
     * @param serializer
     *      The class of serializer
     * @return
     */
    public <S> WebConfiguration addAttributeJsonSerializer(Class<S> type, 
            Class<? extends AttributeJsonSerializer<S>> serializer) {
        addJsonSerializerClass(type, serializer);
        addJsonDeserializerClass(type, serializer);
        addAttributeSerializerClass(type, serializer);
        return this;
    }
    
    public <S> WebConfiguration addAttributeSerializerClass(Class<S> cl, Class<? extends AttributeSerializer<S>> serializerClass) {
        attributeSerializerClasses.put(cl, serializerClass);
        return this;
    }
    public <S> WebConfiguration addJsonSerializerClass(Class<S> cl, Class<? extends JsonSerializer<S>> serializerClass) {
        jsonSerializerClasses.put(cl, serializerClass);
        return this;
    }

    public <S> WebConfiguration addJsonDeserializerClass(Class<S> cl, 
                Class<? extends JsonDeserializer<S>> serializerClass) {
        jsonDeserializerClasses.put(cl, serializerClass);
        return this;
    }
    
    public Set<Entry<Class<?>, Class<? extends JsonDeserializer<?>>>> getJsonDeserializerClasses() {
        return Collections.unmodifiableSet(jsonDeserializerClasses.entrySet());
    }

    public Set<Entry<Class<?>, Class<? extends JsonSerializer<?>>>> getJsonSerializerClasses() {
        return Collections.unmodifiableSet(jsonSerializerClasses.entrySet());
    }
    
    public Set<Entry<Class<?>, Class<? extends AttributeSerializer<?>>>> getAttributeSerializerClasses() {
        return Collections.unmodifiableSet(attributeSerializerClasses.entrySet());
    }

    public WebConfiguration setLifecycleListener(Class<? extends LifecycleListener> lifecycleListener) {
        this.lifecycleListener = lifecycleListener;
        return this;
    }

    public Class<? extends LifecycleListener> getLifecycleListener() {
        return lifecycleListener;
    }

    public void setRemovalSchedulePeriod(long removalSchedulePeriod) {
        this.removalSchedulePeriod = removalSchedulePeriod;
    }

    public long getRemovalSchedulePeriod() {
        return removalSchedulePeriod;
    }

    public WebConfiguration setPropertyProvider(Class<? extends PropertyProvider> propertyProvider) {
        this.propertyProvider = propertyProvider;
        return this;
    }

    public Class<? extends PropertyProvider> getPropertyProvider() {
        return propertyProvider;
    }
}

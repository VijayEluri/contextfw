package net.contextfw.web.application.internal.service;

import java.util.Collection;
import java.util.Collections;

import net.contextfw.web.application.internal.configuration.KeyValue;

public class WebApplicationConf {
    
    private final boolean developmentMode;
    
    private final String xmlParamName;
    
    private final Collection<KeyValue<String, String>> namespaces;

    @SuppressWarnings("unchecked")
    public WebApplicationConf(boolean developmentMode, String xmlParamName,
            Collection<KeyValue<String, String>> namespaces) {
        this.developmentMode = developmentMode;
        this.xmlParamName = xmlParamName;
        this.namespaces = (Collection<KeyValue<String, String>>)
            (namespaces == null ? Collections.emptyList() : namespaces);
    }

    Collection<KeyValue<String, String>> getNamespaces() {
        return namespaces;
    }
    
    String getXmlParamName() {
        return xmlParamName;
    }

    boolean isDevelopmentMode() {
        return developmentMode;
    }
    

}

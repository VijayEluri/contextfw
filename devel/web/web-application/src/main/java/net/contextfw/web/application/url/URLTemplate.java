package net.contextfw.web.application.url;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provides a common storage for url templates that can be modified dynamically.
 * 
 * @author Marko Lavikainen
 * 
 */
public class URLTemplate {

    private String key;

    private Map<String, String> params;

    public void setKey(String key) {
        this.key = key;
    }

    public void removeParam(String name) {
        getParams().remove(name);
    }

    public String getParam(String name) {
        return getParams().get(name);
    }

    public Set<String> paramNames() {
        return getParams().keySet();
    }

    public void setParam(String name, String value) {
        getParams().put(name, value);
    }

    public void setParam(String name, long value) {
        setParam(name, Long.toString(value));
    }

    public void setParam(String name, double value) {
        setParam(name, Double.toString(value));
    }

    public void setParam(String name, boolean value) {
        setParam(name, Boolean.toString(value));
    }

    public String getKey() {
        return key;
    }

    public URLTemplate() {
        params = new HashMap<String, String>();
    }

    public URLTemplate(String key) {
        this();
        this.key = key;
    }

    Map<String, String> getParams() {
        return params;
    }
}
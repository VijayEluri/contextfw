package net.contextfw.web.application.url;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * The map entity to construct mapping
 * 
 * @author marko
 */
public class URLMap {

    private URLModel model;
    private URLTemplate template;
    private String key;

    private Map<String, String> params;

    private URLMap(URLModel model) {

        if (model == null) {
            throw new IllegalArgumentException("Argument 'model': null not allowed");
        }
        this.model = model;
        params = new HashMap<String, String>();
    }

    URLMap(String key, URLModel model) {

        this(model);

        if (key == null) {
            throw new IllegalArgumentException("Argument 'key': null not allowed");
        }

        this.key = key;
    }

    URLMap(URLTemplate template, URLModel model) {

        this(model);

        if (template == null) {
            throw new IllegalArgumentException("Argument 'template': null not allowed");
        }
        this.template = template;
    }

    /**
     * Add parameter
     * 
     * @param name
     *            The parameter name
     * @param value
     *            Parameter value
     * @return Map itsel
     */
    public URLMap param(String name, String value) {
        if (isOk(name, value)) {
            params.put(name, value);
        }
        return this;
    }

    /**
     * Add parameter
     * 
     * @param name
     *            The parameter name
     * @param value
     *            Parameter value
     * @return Map itsel
     */
    public URLMap param(String name, double value) {
        return param(name, Double.toString(value));
    }

    /**
     * Add parameter
     * 
     * @param name
     *            The parameter name
     * @param value
     *            Parameter value
     * @return Map itsel
     */
    public URLMap param(String name, long value) {
        return param(name, Long.toString(value));
    }

    /**
     * Add parameter
     * 
     * @param name
     *            The parameter name
     * @param value
     *            Parameter value
     * @return Map itsel
     */
    public URLMap param(String name, boolean value) {
        return param(name, Boolean.toString(value));
    }

    /**
     * Maps the URL to actually usable string
     * 
     * @return
     */
    public String map() {

        if (template != null) {
            params.putAll(template.getParams());
            return model.getURL(template.getKey(), params);
        }
        else {
            return model.getURL(key, params);
        }

        /*
         * StringBuilder url = new StringBuilder();
         * 
         * String separator = "?";
         * 
         * if (template != null) {
         * url.append(model.getBaseURL(template.getKey())); } else {
         * url.append(model.getBaseURL(key)); }
         * 
         * if (url.indexOf("?") > -1) { separator = "&"; }
         * 
         * if (template != null) { for(String name : template.paramNames()) {
         * url.append(separator + getQueryPart(name, template.getParam(name)));
         * separator = "&"; } }
         * 
         * for(String name : params.keySet()) { url.append(separator +
         * getQueryPart(name, params.get(name))); separator = "&"; }
         * 
         * return model.toPublicURL(url.toString());
         */
    }

    private boolean isOk(String name, String value) {
        if (value == null || value.trim().length() == 0)
            return false;
        if (name == null || name.trim().length() == 0)
            return false;
        return true;
    }

    @SuppressWarnings("unused")
    private String getQueryPart(String name, String value) {
        try {
            return URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
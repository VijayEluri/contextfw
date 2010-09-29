package net.contextfw.web.application.url;

/**
 * Provides way to handle URLs in a consistent way.
 * 
 * <p>
 * This class provides abstraction over dynamically created URLs. This means,
 * that controllers and views don't have to know actual addresses they are
 * using.
 * </p>
 * 
 * <p>
 * This approach makes the system more flexible to different environments and
 * makes localization easier.
 * </p>
 * 
 */
public class URLMapper {

    private URLModel model = null;

    public URLMapper(URLModel model) {

        if (model == null) {
            throw new IllegalArgumentException("Argument 'model': null not allowed");
        }

        this.model = model;
    }

    public URLMap forKey(String key) {
        return new URLMap(key, model);
    }

    public URLMap forTemplate(URLTemplate template) {
        return new URLMap(template, model);
    }
}
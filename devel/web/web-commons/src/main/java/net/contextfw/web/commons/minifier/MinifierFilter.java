package net.contextfw.web.commons.minifier;

public interface MinifierFilter {
    
    /**
     * Tells if path should be included in to the minified file
     */
    boolean include(String path);

    /**
     * Tells if the file in the path should be minified
     */
    boolean minify(String path);
}

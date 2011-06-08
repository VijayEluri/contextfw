package net.contextfw.web.application;

import java.util.Properties;

/**
 * This interface defines where system properties are read.
 * 
* <p>
 *  An XSL-post-processor can be set through 
 *  {@link net.contextfw.web.application.properties.Properties}.
 * </p>
 * 
 * @see net.contextfw.web.application.properties.Properties#PROPERTY_PROVIDER
 */
public interface PropertyProvider {
    
    Properties get();
}

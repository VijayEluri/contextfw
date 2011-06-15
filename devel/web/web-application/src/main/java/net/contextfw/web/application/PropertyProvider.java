package net.contextfw.web.application;

import java.util.Properties;

/**
 * This interface defines where system properties are read.
 * 
 * @see net.contextfw.web.application.properties.Properties#PROPERTY_PROVIDER
 */
public interface PropertyProvider {
    
    Properties get();
}

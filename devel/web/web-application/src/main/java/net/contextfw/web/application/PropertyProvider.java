package net.contextfw.web.application;

import java.util.Properties;

/**
 * This interface defines where system properties are read.
 * 
 * @see net.contextfw.web.application.configuration.Configuration#PROPERTY_PROVIDER
 */
public interface PropertyProvider {
    
    Properties get();
}
